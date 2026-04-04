package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.AuthResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.LoginRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.RegisterRequest;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import com.CourseCatlog.CourseCatlogApplication.exception.ConflictException;
import com.CourseCatlog.CourseCatlogApplication.repository.UserRepository;
import com.CourseCatlog.CourseCatlogApplication.security.JwtService;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        User u = User.builder()
                .name(req.getName())
                .email(req.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.STUDENT)
                .build();
        u = userRepository.save(u);
        UserPrincipal principal = UserPrincipal.from(u);
        return AuthResponse.builder()
                .token(jwtService.generateToken(principal))
                .email(u.getEmail())
                .userId(u.getId())
                .role(u.getRole())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail().toLowerCase(), req.getPassword()));
        User u = userRepository.findByEmailIgnoreCase(req.getEmail().toLowerCase())
                .orElseThrow(() -> new com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException("User not found"));
        UserPrincipal principal = UserPrincipal.from(u);
        return AuthResponse.builder()
                .token(jwtService.generateToken(principal))
                .email(u.getEmail())
                .userId(u.getId())
                .role(u.getRole())
                .build();
    }
}
