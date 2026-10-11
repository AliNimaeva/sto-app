package com.example.sto_app.service;

import com.example.sto_app.dto.CreateUserRequest;
import com.example.sto_app.dto.UserResponse;
import com.example.sto_app.exception.ResourceNotFoundException;
import com.example.sto_app.mapper.UserMapper;
import com.example.sto_app.model.User;
import com.example.sto_app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.sto_app.dto.UpdateUserRequest;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Пользователь не найден: " + id)
                );
    }

    public User create(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Пользователь с таким email уже существует"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    public User update(Long id, UpdateUserRequest request) {

        User user = getById(id);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Пользователь с таким email уже существует"
            );
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return userRepository.save(user);
    }
}