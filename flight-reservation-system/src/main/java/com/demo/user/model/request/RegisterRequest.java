package com.demo.user.model.request;


import lombok.Data;

@Data
public class RegisterRequest {
        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private String password;
}
