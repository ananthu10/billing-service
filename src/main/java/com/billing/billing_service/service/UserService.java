package com.billing.billing_service.service;

import com.billing.billing_service.dtos.RegisterUserDto;
import com.billing.billing_service.models.Role;
import com.billing.billing_service.models.User;
import com.billing.billing_service.repository.RoleRepository;
import com.billing.billing_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> allUsers() {

        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);

        return users;
    }

    public User createUser(RegisterUserDto input) {

        Optional<Role> optionalRole = roleRepository.findByName(input.getRole());

        if (optionalRole.isEmpty()) {
            return null;
        }

        User user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(optionalRole.get());
         user = userRepository.save(user);

        return user;
    }
}