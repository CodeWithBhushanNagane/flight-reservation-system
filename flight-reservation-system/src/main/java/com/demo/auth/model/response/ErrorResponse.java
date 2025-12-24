package com.demo.auth.model.response;

public record ErrorResponse(int status, String message, String path) {
}
