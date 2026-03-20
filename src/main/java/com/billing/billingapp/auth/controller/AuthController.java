package com.billing.billingapp.auth.controller;

import com.billing.billingapp.auth.dto.LoginRequestDto;
import com.billing.billingapp.auth.dto.ResetPasswordDto;
import com.billing.billingapp.auth.dto.SignupRequestDto;
import com.billing.billingapp.auth.entity.User;
import com.billing.billingapp.auth.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequestDto request) {

        authService.signup(request);

        return "User created successfully";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequestDto dto) {
        String token = authService.login(dto);
        return Map.of("token", token);
    }

//    @PostMapping("/login")
//    public User login(@RequestBody LoginRequestDto request) {
//
//        return authService.login(request);
//    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return "Reset link sent to email";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordDto dto) {
        authService.resetPassword(dto);
        return "Password updated successfully";
    }
}