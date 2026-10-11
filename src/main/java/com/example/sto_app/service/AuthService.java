package com.example.sto_app.service;

import com.example.sto_app.model.User;
import com.example.sto_app.repository.UserRepository;
import com.example.sto_app.dto.LoginRequest;
import com.example.sto_app.dto.LoginResponse;
import com.example.sto_app.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var userDetails =
                (org.springframework.security.core.userdetails.UserDetails)
                        authentication.getPrincipal();

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден")
                );

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}