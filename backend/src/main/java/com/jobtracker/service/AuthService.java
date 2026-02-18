package com.jobtracker.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobtracker.dto.LoginRequest;
import com.jobtracker.dto.RegisterRequest;
import com.jobtracker.model.User;
import com.jobtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import com.jobtracker.security.JwtUtil;


@Service
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;


    public AuthService(UserRepository userRepo, @Value("${jwt.secret}") String secret) {
        this.userRepo = userRepo;
        this.jwtUtil = new JwtUtil(secret);
    }

    public User register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(encoder.encode(req.getPassword()));

        return userRepo.save(user);
    }

    public User login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        boolean ok = encoder.matches(req.getPassword(), user.getPasswordHash());
        if (!ok) throw new RuntimeException("Invalid email or password");

        return user;
    }

    public String createToken(String email, Long userId) {
        return jwtUtil.generateToken(email, userId);
    }

}
