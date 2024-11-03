package com.vadym.board.services;

import com.vadym.board.models.User;
import com.vadym.board.models.enums.Role;
import com.vadym.board.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailSenderService mailSenderService;

    public boolean createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) return false;
        user.setActive(false);
        user.getRoles().add(Role.ROLE_ADMIN);
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        mailSenderService.sendActivationCode(user);
        return true;
    }

    public void banAndUnbanUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActive(!user.isActive());
        }
        userRepository.save(user);
    }

    public void userChangeRole(User user, Map<String, String> form) {
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();
        for (String role : form.keySet()) {
            if (roles.contains(role)) {
                user.getRoles().add(Role.valueOf(role));
            }
        }
        userRepository.save(user);
    }

    public void updateProfile(User user, String name, String surname, String email, String password) {
        if (!user.getName().equals(name) && !StringUtils.isEmpty(name)) {
            user.setName(name);
        }
        if (!user.getSurname().equals(surname) && !StringUtils.isEmpty(surname)) {
            user.setSurname(surname);
        }
        if (!user.getEmail().equals(email) && !StringUtils.isEmpty(email)) {
            user.setEmail(email);
            user.setActivationCode(UUID.randomUUID().toString());
            user.setActive(false);
            mailSenderService.sendActivationCode(user);
        }
        if (!user.getPassword().equals(passwordEncoder.encode(password)) && !StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        userRepository.save(user);
    }

    public void activateUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if (user != null) {
            user.setActivationCode(null);
            user.setActive(true);
            userRepository.save(user);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
