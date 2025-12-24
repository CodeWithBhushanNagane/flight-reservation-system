package com.demo.jwt.filter;

import com.demo.auth.util.AuthConstants;
import com.demo.jwt.model.Jwt;
import com.demo.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.demo.auth.util.AuthConstants.AUTHORIZATION_HEADER;
import static com.demo.auth.util.AuthConstants.AUTHORIZATION_TOKEN_PREFIX;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        Jwt jwt;
        if (authHeader != null && authHeader.startsWith(AUTHORIZATION_TOKEN_PREFIX)) {
            final String token = authHeader.substring(7);
            jwt = jwtService.parseToken(token);
            if (jwt != null && !jwt.isClaimExpired()) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        jwt.getUsername(),
                        null,
                        List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
