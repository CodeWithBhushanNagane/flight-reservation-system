package com.demo.jwt.service;

import com.demo.jwt.model.Jwt;
import com.demo.user.model.User;

public interface JwtService {
    Jwt generateAccessToken(User user);
    Jwt generateRefreshToken(User user);
    Jwt parseToken(String token);
}
