package com.demo.user.model.request;

public record UpdatePasswordRequest(String oldPassword, String newPassword) {
}
