package vn.edu.ptithcm.mindcard.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.security.JwtService;
import vn.edu.ptithcm.mindcard.security.UserPrincipal;

import java.io.IOException;
import java.util.List;


@Component
public class JWTRequestFilter extends OncePerRequestFilter {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/api/auth/register/**",
            "/api/auth/logout",
            "/api/auth/login",
            "/api/auth/reset_password",
            "/api/auth/forgot_password",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    );

    @Autowired
    JwtService jwtService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtService.extractAccessTokenFromRequest(request);
        if (token == null){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Claims claims = jwtService.validateJwtToken(token, JwtService.TokenType.ACCESS_TOKEN);

            String username = claims.getSubject();
            Integer userId = claims.get("id", Integer.class);

            if (userId == null){
                throw new AppException(ErrorCode.SERVER_ERROR, "Can not extract user id from jwt");
            }

            UserPrincipal principal = new UserPrincipal(userId, username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }catch (AppException e){
            response.setStatus(e.getErrorCode().getHttpStatusCode());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            APIResponse<?> jsonRes = APIResponse.error(e.getErrorCode(), e.getMessage());

            response.getWriter().write(objectMapper.writeValueAsString(jsonRes));
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        return PUBLIC_ROUTES.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
