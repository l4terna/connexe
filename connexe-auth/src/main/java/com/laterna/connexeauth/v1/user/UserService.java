package com.laterna.connexeauth.v1.user;

import com.laterna.connexeauth.v1._shared.exception.EntityAlreadyExistsException;
import com.laterna.connexeauth.v1.auth.dto.RegisterDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public User createUser(RegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.email()) || userRepository.existsByLogin(registerDTO.login())) {
            throw new EntityAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .login(registerDTO.login())
                .email(registerDTO.email())
                .password(passwordEncoder.encode(registerDTO.password()))
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean matchPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Transactional
    public void delete(Long deletedUserId) {
        User user = findUserById(deletedUserId);

        user.setDeletedAt(Instant.now());
        userRepository.save(user);
    }

    @Transactional
    public void update(Long userId, String login) {
        User user = findUserById(userId);
        user.setLogin(login);
        userRepository.save(user);
    }
}
