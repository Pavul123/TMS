package com.transport.tms.user.service;

import com.transport.tms.auth.dto.AuthDto;
import com.transport.tms.common.exception.Exceptions;
import com.transport.tms.common.util.IdGenerator;
import com.transport.tms.user.entity.Role;
import com.transport.tms.user.entity.User;
import com.transport.tms.user.repository.RoleRepository;
import com.transport.tms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdGenerator idGenerator;

    @Transactional(readOnly = true)
    public List<AuthDto.UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthDto.UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("User", "id", id));
        return mapToDto(user);
    }

    @Transactional
    public AuthDto.UserDto createUser(User userRequest, String roleName, String rawPassword) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new Exceptions.BadRequestException("Username already exists: " + userRequest.getUsername());
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Role", "name", roleName));

        User user = User.builder()
                .id("USR-" + System.currentTimeMillis() % 100000)
                .username(userRequest.getUsername().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(rawPassword))
                .fullName(userRequest.getFullName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .role(role)
                .status("ACTIVE")
                .build();

        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Transactional
    public AuthDto.UserDto updateUserStatus(String id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("User", "id", id));
        user.setStatus(status);
        return mapToDto(userRepository.save(user));
    }

    private AuthDto.UserDto mapToDto(User user) {
        return AuthDto.UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().getName())
                .status(user.getStatus())
                .build();
    }
}
