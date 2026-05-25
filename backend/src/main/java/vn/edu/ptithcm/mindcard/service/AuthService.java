package vn.edu.ptithcm.mindcard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.edu.ptithcm.mindcard.dto.request.auth.LoginRequest;
import vn.edu.ptithcm.mindcard.dto.request.auth.RegisterCompleteRequest;
import vn.edu.ptithcm.mindcard.dto.request.auth.RegisterOtpRequest;
import vn.edu.ptithcm.mindcard.dto.request.auth.ResetPasswordRequest;
import vn.edu.ptithcm.mindcard.dto.response.auth.LoginResponse;
import vn.edu.ptithcm.mindcard.dto.response.auth.RefreshResponse;
import vn.edu.ptithcm.mindcard.entity.User;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.UserRepository;
import vn.edu.ptithcm.mindcard.security.JwtBlacklistService;
import vn.edu.ptithcm.mindcard.security.JwtService;
import vn.edu.ptithcm.mindcard.security.UserPrincipal;
import vn.edu.ptithcm.mindcard.utils.OTPUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtBlacklistService blacklistService;

    private String generateAccessToken(User user){
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("id", user.getId());
        additionalClaims.put("email", user.getEmail());
        return jwtService.generateJwtToken(user.getUsername(), additionalClaims, JwtService.TokenType.ACCESS_TOKEN);
    }

    private String generateRefreshToken(User user){
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("id", user.getId());
        additionalClaims.put("email", user.getEmail());
        return jwtService.generateJwtToken(user.getUsername(), additionalClaims, JwtService.TokenType.REFRESH_TOKEN);
    }

    public UserPrincipal getCurrentUserPrincipal(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken){
            return null;
        }

        return (UserPrincipal) authentication.getPrincipal();
    }

    /**
     * Requests user registration by sending a validation OTP to the user's email.
     *
     * @param request the registration request details containing email.
     * @throws AppException if any business validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#RESOURCE_ALREADY_EXIST} - if the email is already registered</li>
     *     <li>{@link ErrorCode#SERVER_ERROR} - if the system fails to send the email</li>
     * </ul>
     *
     * @see AuthService#completeRegistration
     */
    public void requestOtpForRegistration(RegisterOtpRequest request) throws AppException{
        if (userRepository.findByEmail(request.email()).isPresent()){
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXIST, "Email already exists");
        }
        String otp = OTPUtils.generateOTP(6);
        redisTemplate.opsForValue().set("registration:"+request.email(), otp, 5, TimeUnit.MINUTES);

        try {
            mailService.sendEmail(request.email(), "OTP", String.format("Your otp is %s", otp));
        }catch (Exception e){
            throw new AppException(ErrorCode.SERVER_ERROR, "Can not send email");
        }
    }

    /**
     * Completes the user registration process after verifying the OTP.
     * Saves the new user into the database with a hashed password.
     *
     * @param request the registration details containing email, username, password, and OTP
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#RESOURCE_ALREADY_EXIST} - if the email or username is already taken</li>
     *     <li>{@link ErrorCode#INVALID_OTP} - if the OTP is incorrect or expired, or missing in Redis</li>
     * </ul>
     *
     * @see AuthService#requestOtpForRegistration
     */
    public void completeRegistration(RegisterCompleteRequest request){
        if (userRepository.findByEmail(request.email()).isPresent() ||
            userRepository.findByUsername(request.username()).isPresent()
        ){
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXIST, "Email or Username already exists");
        }

        String storedOtp = redisTemplate.opsForValue().get("registration:" + request.email());
        if (!request.otp().equals(storedOtp)){
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(hashedPassword)
                .build();
        userRepository.save(newUser);
    }

    /**
     * Authenticates a user based on their identity (email or username) and password.
     * Generates a pair of short-lived Access Token and long-lived Refresh Token upon success.
     *
     * @param request the login credentials containing identity and password
     * @return a {@link LoginResponse} containing both generated JWT tokens
     * @throws AppException if authentication fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#LOGIN_FAILED} - if the user does not exist or the password is incorrect</li>
     * </ul>
     */
    public LoginResponse login(LoginRequest request){
        Optional<User> user = userRepository.findByEmailOrUsername(request.identity());
        if (
                user.isEmpty() ||
                (!passwordEncoder.matches(request.password(), user.get().getPasswordHash()))
        ){
            throw new AppException(ErrorCode.LOGIN_FAILED, "Identity or password mismatch");
        }

        String accessToken = generateAccessToken(user.get());
        String refreshToken = generateRefreshToken(user.get());

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * Invalidates the provided Access Token and Refresh Token by adding their unique IDs (JTI)
     * to the Redis blacklist until their natural expiration time.
     *
     * @param accessToken the active Access Token string, can be null
     * @param refreshToken the active Refresh Token string, can be null
     */
    public void logout(String accessToken, String refreshToken){
        if (accessToken != null){
            var accessClaims = jwtService.validateJwtToken(accessToken, JwtService.TokenType.ACCESS_TOKEN);
            blacklistService.addToBlackList(accessClaims.getId(), accessClaims.getExpiration());
        }
        if (refreshToken != null){
            var refreshClaims = jwtService.validateJwtToken(refreshToken, JwtService.TokenType.REFRESH_TOKEN);
            blacklistService.addToBlackList(refreshClaims.getId(), refreshClaims.getExpiration());
        }
    }

    /**
     * Generates a new Access Token using a valid Refresh Token.
     *
     * @param refreshToken the valid, non-expired Refresh Token string
     * @return a {@link RefreshResponse} containing the newly generated Access Token
     * @throws AppException if the Refresh Token is invalid, expired, or blacklisted
     */
    public RefreshResponse refreshAccessToken(String refreshToken) throws AppException{
        var claims = jwtService.validateJwtToken(refreshToken, JwtService.TokenType.REFRESH_TOKEN);
        String userName = claims.getSubject();
        Optional<User> user = userRepository.findByUsername(userName);
        if (user.isEmpty()){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        String accessToken = generateAccessToken(user.get());
        return new RefreshResponse(accessToken);
    }

    /**
     * Initiates the password recovery flow by verifying user identity and sending an OTP via email.
     * The recovery OTP is cached in Redis against the username for 5 minutes.
     *
     * @param identity the user's username or email address
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND} - if no user matches the provided identity</li>
     * </ul>
     * 
     * @see AuthService#resetPassword 
     */
    public void forgotPassword(String identity) throws AppException{
        Optional<User> user = userRepository.findByEmailOrUsername(identity);
        if (user.isEmpty()){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        String otp = OTPUtils.generateOTP(6);
        redisTemplate.opsForValue().set("reset_password:"+user.get().getUsername(), otp, 5, TimeUnit.MINUTES);

        String emailContent = String.format("""
                You request reset password
                Your OTP is %s
                """, otp);
        mailService.sendEmail(user.get().getEmail(), "ResetPassword", emailContent);
    }

    /**
     * Resets the user's password after successfully validating the recovery OTP.
     * Updates the password hash in the database.
     *
     * @param request the details containing identity, OTP, and the new password
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND} - if no user matches the provided identity</li>
     *     <li>{@link ErrorCode#INVALID_OTP} - if the recovery OTP is incorrect or expired</li>
     * </ul>
     * 
     * @see AuthService#forgotPassword 
     */
    public void resetPassword(ResetPasswordRequest request) throws AppException{
        Optional<User> user = userRepository.findByEmailOrUsername(request.identity());
        if (user.isEmpty()){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        String storedOtp = redisTemplate.opsForValue().get("reset_password:"+user.get().getUsername());
        if (!request.otp().equals(storedOtp)){
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        User userObj = user.get();
        userObj.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(userObj);
    }

}
