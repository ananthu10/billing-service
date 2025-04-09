package com.billing.billing_service.bootstrap;

import com.billing.billing_service.models.Role;
import com.billing.billing_service.models.RoleEnum;
import com.billing.billing_service.models.User;
import com.billing.billing_service.repository.RoleRepository;
import com.billing.billing_service.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Component
@DependsOn("roleSeeder")
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LogManager.getLogger(AdminSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String PASSWORD = "123456";

    public AdminSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        // 1. Check if SUPER_ADMIN role exists
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        if (optionalRole.isEmpty()) {
            logger.error("SUPER_ADMIN role not found! Did RoleSeeder run?");
            return;
        }

        String adminEmail = "super.admin@email.com";
        Optional<User> optionalUser = userRepository.findByEmail(adminEmail);
        if (optionalUser.isPresent()) {
            logger.info("Admin user already exists: {}", adminEmail);
            return;
        }

        User admin = new User()
                .setFullName("Super Admin")
                .setEmail(adminEmail)
                .setPassword(passwordEncoder.encode(PASSWORD))
                .setRole(optionalRole.get());

        userRepository.save(admin);
        logger.info("Created Super Admin: {}", adminEmail);
    }
}