package com.demo.auth.model.request;

import com.demo.user.model.User;

import java.time.Instant;

public record TokenRequest(String token, User user, Instant expiryDate, boolean active) {
    public TokenRequest(String token, User user, Instant expiryDate) {
        this(token, user, expiryDate, true);
    }
}
