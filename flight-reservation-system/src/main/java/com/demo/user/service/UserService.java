package com.demo.user.service;

import com.demo.user.model.User;
import com.demo.user.model.request.RegisterRequest;
import com.demo.user.model.request.UpdatePasswordRequest;
import com.demo.user.model.response.UpdatePasswordResponse;
import com.demo.user.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long userId);

    User getUserByUsername(String username);

    UserResponse getUserByEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);

    UserResponse registerUser(RegisterRequest registerRequest);

    UpdatePasswordResponse updatePassword(Long id, UpdatePasswordRequest updatePasswordRequest);

    void deleteUser(Long id);
}
