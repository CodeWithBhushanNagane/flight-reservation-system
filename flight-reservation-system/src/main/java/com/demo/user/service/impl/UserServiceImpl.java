package com.demo.user.service.impl;

import com.demo.user.exception.UserAlreadyExistsException;
import com.demo.user.exception.UserNotFoundException;
import com.demo.user.model.User;
import com.demo.user.model.mapper.UserMapper;
import com.demo.user.model.request.RegisterRequest;
import com.demo.user.model.request.UpdatePasswordRequest;
import com.demo.user.model.response.UpdatePasswordResponse;
import com.demo.user.model.response.UserResponse;
import com.demo.user.repository.UserRepository;
import com.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::userToUserResponse);
    }

    @Override
    public UserResponse getUserById(Long userId) {
         return userMapper.userToUserResponse(userRepository.getReferenceById(userId));
    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        throw new UsernameNotFoundException("User not found with username : " + username);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userMapper.userToUserResponse(userOptional.get());
        }
        throw new UsernameNotFoundException("User not found with email : " + email);
    }

    @Override
    public boolean existsUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserResponse registerUser(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();

        if (existsUserByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists with email : " + email);
        }
        if (existsUserByUsername(username)) {
            throw new UserAlreadyExistsException("User already exists with username : " + username);
        }
        User user = userMapper.registerRequestToUser(registerRequest);
        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    public UpdatePasswordResponse updatePassword(Long id, UpdatePasswordRequest updatePasswordRequest) {
         User user = userRepository.getReferenceById(id);
         if (user.getId() == null) {
             throw new UserNotFoundException("User not found with id : " +  id);
         }
         user.setPassword(updatePasswordRequest.newPassword());
         return userMapper.userToUpdatePasswordResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
