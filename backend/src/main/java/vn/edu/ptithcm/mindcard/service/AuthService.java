package vn.edu.ptithcm.mindcard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

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
import vn.edu.ptithcm.mindcard.utils.OTPUtils;

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

    public LoginResponse login(LoginRequest request){
        Optional<User> user = userRepository.findByEmailOrUsername(request.identity());
        if (
                user.isEmpty() ||
                (!passwordEncoder.matches(request.password(), user.get().getPasswordHash()))
        ){
            throw new AppException(ErrorCode.LOGIN_FAILED, "Identity or password mismatch");
        }

        String accessToken = jwtService.generateJwtToken(user.get().getUsername(), JwtService.TokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.generateJwtToken(user.get().getUsername(), JwtService.TokenType.REFRESH_TOKEN);

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * Add token to black list(if token not expired)
     * @param accessToken String or null
     * @param refreshToken String or null
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

    public RefreshResponse refreshAccessToken(String refreshToken) throws AppException{
        var claims = jwtService.validateJwtToken(refreshToken, JwtService.TokenType.REFRESH_TOKEN);
        String userName = claims.getSubject();
        String accessToken = jwtService.generateJwtToken(userName, JwtService.TokenType.ACCESS_TOKEN);
        return new RefreshResponse(accessToken);
    }

    /**
     * Check user exist, generate otp and send email
     * @param identity {@code username} or {@code email}
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
     * Check user exit, check otp and reset password
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
    }

}
