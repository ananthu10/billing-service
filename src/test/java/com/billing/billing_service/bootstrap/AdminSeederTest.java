package com.billing.billing_service.bootstrap;

import com.billing.billing_service.models.Role;
import com.billing.billing_service.models.RoleEnum;
import com.billing.billing_service.models.User;
import com.billing.billing_service.repository.RoleRepository;
import com.billing.billing_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

class AdminSeederTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ContextRefreshedEvent contextRefreshedEvent;

    @InjectMocks
    private AdminSeeder adminSeeder;

    private final String superAdminEmail = "super.admin@email.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenSuperAdminRoleExistsAndUserDoesNotExist_thenCreatesUser() {
        Role role = new Role();
        role.setName(RoleEnum.SUPER_ADMIN);

        when(roleRepository.findByName(RoleEnum.SUPER_ADMIN)).thenReturn(Optional.of(role));
        when(userRepository.findByEmail(superAdminEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");

        adminSeeder.onApplicationEvent(contextRefreshedEvent);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assert savedUser.getUsername().equals(superAdminEmail);
        assert savedUser.getPassword().equals("encodedPassword");
        assert savedUser.getRole() == role;
    }

    @Test
    void whenSuperAdminUserAlreadyExists_thenDoesNotCreateUser() {
        Role role = new Role();
        role.setName(RoleEnum.SUPER_ADMIN);
        User existingUser = new User().setEmail(superAdminEmail);

        when(roleRepository.findByName(RoleEnum.SUPER_ADMIN)).thenReturn(Optional.of(role));
        when(userRepository.findByEmail(superAdminEmail)).thenReturn(Optional.of(existingUser));

        adminSeeder.onApplicationEvent(contextRefreshedEvent);

        verify(userRepository, never()).save(any());
    }

    @Test
    void whenSuperAdminRoleIsMissing_thenDoesNotCreateUser() {
        when(roleRepository.findByName(RoleEnum.SUPER_ADMIN)).thenReturn(Optional.empty());

        adminSeeder.onApplicationEvent(contextRefreshedEvent);

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
    }
}
