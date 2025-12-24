package com.demo.auth.model.response;


import lombok.Builder;

@Builder
public record TokenResponse(String accessToken, long accessTokenExpireIn, String refreshToken, long refreshTokenExpireIn, String tokenType, String scope) {
    public TokenResponse(String accessToken, long accessTokenExpireIn, String refreshToken, long refreshTokenExpireIn, String tokenType) {
        this(accessToken, accessTokenExpireIn, refreshToken, refreshTokenExpireIn, tokenType, "");
    }
}
