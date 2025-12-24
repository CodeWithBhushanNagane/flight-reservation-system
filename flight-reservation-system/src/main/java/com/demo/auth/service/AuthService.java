package com.demo.auth.service;

import com.demo.auth.model.request.RefreshTokenRequest;
import com.demo.auth.model.response.TokenResponse;
import com.demo.user.model.request.LoginRequest;

public interface AuthService {
    TokenResponse login(LoginRequest loginRequest);
    TokenResponse refresh(RefreshTokenRequest request);
}
