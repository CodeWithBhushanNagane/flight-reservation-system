package com.demo.auth.service.impl;

import com.demo.auth.model.RefreshToken;
import com.demo.auth.model.mapper.TokenMapper;
import com.demo.auth.model.request.TokenRequest;
import com.demo.auth.model.response.TokenResponse;
import com.demo.auth.repository.RefreshTokenRepository;
import com.demo.auth.service.RefreshTokenService;
import com.demo.jwt.config.JwtConfig;
import com.demo.jwt.model.Jwt;
import com.demo.jwt.service.JwtService;
import com.demo.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtConfig jwtConfig;


    @Override
    public Optional<RefreshToken> findByTokenAndActiveTrue(String token) {
        return refreshTokenRepository.findByTokenAndActiveTrue(token);
    }

    @Override
    public TokenResponse createRefreshToken(User user) {
        Jwt accessToken = jwtService.generateAccessToken(user);
        Jwt refreshToken = jwtService.generateRefreshToken(user);

        var expiration = Instant.now().plusMillis(jwtConfig.getRefreshTokenTTL());
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken.getToken());
        rt.setExpiryDate(expiration);
        rt.setUser(user);
        refreshTokenRepository.save(rt);

        return TokenResponse.builder()
                .accessToken(accessToken.getToken())
                .accessTokenExpireIn(jwtConfig.getAccessTokenTTL())
                .refreshToken(refreshToken.getToken())
                .refreshTokenExpireIn(jwtConfig.getRefreshTokenTTL())
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void deleteRefreshToken(Long id) {
        refreshTokenRepository.deleteById(id);
    }
}

