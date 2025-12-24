package com.demo.auth.service.impl;

import com.demo.auth.exception.InvalidRefreshTokenException;
import com.demo.auth.model.RefreshToken;
import com.demo.auth.model.request.RefreshTokenRequest;
import com.demo.auth.model.response.TokenResponse;
import com.demo.auth.repository.RefreshTokenRepository;
import com.demo.auth.service.AuthService;
import com.demo.auth.service.RefreshTokenService;
import com.demo.jwt.config.JwtConfig;
import com.demo.jwt.model.Jwt;
import com.demo.jwt.service.JwtService;
import com.demo.user.model.User;
import com.demo.user.model.request.LoginRequest;
import com.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static com.demo.auth.util.AuthConstants.INVALID_CREDENTIALS_MESSAGE;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.getUserByUsername(loginRequest.username());
        return refreshTokenService.createRefreshToken(user);
    }

    @Override
    public TokenResponse refresh(RefreshTokenRequest request) {
        Jwt jwt = jwtService.parseToken(request.refreshToken());
        Optional<RefreshToken> tokenOptional = refreshTokenService.findByTokenAndActiveTrue(request.refreshToken());
        if (tokenOptional.isEmpty()) {
            RefreshToken token = tokenOptional.get();
            if (token.getExpiryDate().isBefore(Instant.now())) {
                refreshTokenService.deleteRefreshToken(token.getId());
                throw new InvalidRefreshTokenException("Refresh token expired.");
            }
            throw new InvalidRefreshTokenException("Invalid credentials.");
        }
        User user = userService.getUserByUsername(jwt.getUsername());
        return refreshTokenService.createRefreshToken(user);
    }
}
