package com.demo.jwt.service.impl;

import com.demo.jwt.config.JwtConfig;
import com.demo.jwt.model.Jwt;
import com.demo.jwt.service.JwtService;
import com.demo.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public Jwt generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenTTL());
    }

    @Override
    public Jwt generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenTTL());
    }

    @Override
    public Jwt parseToken(String token) {
        Claims claims = getClaims(token);
        return new Jwt(claims, getSecretKey());
    }

    private Jwt generateToken(User user, long ttl) {
        Claims claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("username", user.getUsername())
                .add("email", user.getEmail())
                .add("roles", user.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (ttl * 1000)))
                .build();
        return new Jwt(claims, getSecretKey());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }
}
