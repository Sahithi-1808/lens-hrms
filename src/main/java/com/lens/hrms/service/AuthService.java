package com.lens.hrms.service;

import com.lens.hrms.dto.*;
import com.lens.hrms.entity.*;
import com.lens.hrms.repository.UserRepository;
import com.lens.hrms.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    public LoginResponse login(LoginRequest request) {
        User user = users.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!encoder.matches(request.password(), user.getPassword()))
            throw new RuntimeException("Invalid email or password");
        return new LoginResponse(jwt.generateToken(user), user.getEmail(), user.getRole().name());
    }
}
