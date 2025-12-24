package com.demo.user.model.response;

public record UserResponse(Long id, String firstName, String lastName, String username, String email, boolean active) {
}
