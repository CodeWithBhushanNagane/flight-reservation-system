package com.demo.jwt.model;

import com.demo.user.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@AllArgsConstructor
public class Jwt {

    private final Claims claims;
    private final SecretKey secretKey;

    public Long getUserId() {
        return Long.parseLong(claims.getSubject());
    }

    public String getUsername() {
        return (String) claims.get("username");
    }

    public Set<Role> getRoles() {
        return (Set<Role>) claims.get("roles");
    }

    public boolean isClaimExpired() {
        return getExpiration().before(new Date());
    }

    public Date getExpiration() {
        return claims.getExpiration();
    }

    public String getToken() {
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }
}
