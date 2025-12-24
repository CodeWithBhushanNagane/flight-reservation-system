package com.demo.user.model.response;

public record UpdatePasswordResponse(Long id, String firstName, String lastName, String username, String password, String email, boolean active) {
}
