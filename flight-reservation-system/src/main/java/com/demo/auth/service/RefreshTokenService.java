package com.demo.auth.service;

import com.demo.auth.model.RefreshToken;
import com.demo.auth.model.response.TokenResponse;
import com.demo.user.model.User;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByTokenAndActiveTrue(String token);
    TokenResponse createRefreshToken(User user);
    void deleteRefreshToken(Long id);
}
