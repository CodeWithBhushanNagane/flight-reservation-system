package com.flight.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flight.exception.UsernameAlreadyExistException;
import com.flight.security.models.Role;
import com.flight.security.models.User;
import com.flight.security.models.request.LoginRequest;
import com.flight.security.models.request.RegisterRequest;
import com.flight.security.models.response.TokenResponse;
import com.flight.security.models.response.UserResponse;
import com.flight.security.repository.RoleRepository;
import com.flight.security.repository.UserRepository;
import com.flight.security.service.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistException("User Already exists");
        }
        Role role = roleRepository.getRoleByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
        User user = User.builder().username(request.username())
                .password(encoder.encode(request.password()))
                .email(request.email())
                .roles(Set.of(role))
                .build();
        user = userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails.getUsername());
        System.out.println(userDetails.getAuthorities());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
