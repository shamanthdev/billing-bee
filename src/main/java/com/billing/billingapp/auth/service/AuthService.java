package com.billing.billingapp.auth.service;
import com.billing.billingapp.auth.dto.LoginRequestDto;
import com.billing.billingapp.auth.dto.ResetPasswordDto;
import com.billing.billingapp.auth.dto.SignupRequestDto;
import com.billing.billingapp.auth.entity.User;
import com.billing.billingapp.auth.repository.UserRepository;

import com.billing.billingapp.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private JwtUtil jwtUtil;

    public void signup(SignupRequestDto request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole("ADMIN");

        userRepository.save(user);
    }

    public String login(LoginRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
//    public User login(LoginRequestDto request) {
//
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!encoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid password");
//        }
//
//        return user;
//    }
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        // For now just log (later replace with email)
        System.out.println("Reset link: http://localhost:3000/reset-password?token=" + token);
    }
    public void resetPassword(ResetPasswordDto dto) {

        User user = userRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(encoder.encode(dto.getNewPassword()));

        // clear token after use
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }
}