package com.billing.billing_service.bootstrap;

import com.billing.billing_service.models.Role;
import com.billing.billing_service.models.RoleEnum;
import com.billing.billing_service.models.User;
import com.billing.billing_service.repository.RoleRepository;
import com.billing.billing_service.repository.UserRepository;
import com.billing.billing_service.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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

    @InjectMocks
    private AdminSeeder adminSeeder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject values for @Value fields using reflection
        adminSeeder = new AdminSeeder(roleRepository, userRepository, passwordEncoder);
        TestUtils.setField(adminSeeder, "adminUsername", "super.admin@email.com");
        TestUtils.setField(adminSeeder, "adminPassword", "123456");
    }

    @Test
    void testCreateSuperAdminWhenNotExists() {
        Role superAdminRole = new Role();
        superAdminRole.setName(RoleEnum.SUPER_ADMIN);

        when(roleRepository.findByName(RoleEnum.SUPER_ADMIN)).thenReturn(Optional.of(superAdminRole));
        when(userRepository.findByEmail("super.admin@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPwd");

        adminSeeder.onApplicationEvent(null); // simulate startup

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("super.admin@email.com")
                        && user.getPassword().equals("encodedPwd")
                        && user.getRole().equals(superAdminRole)
        ));
    }

    @Test
    void testDoNotCreateAdminIfAlreadyExists() {
        Role superAdminRole = new Role();
        superAdminRole.setName(RoleEnum.SUPER_ADMIN);
        User existingUser = new User().setEmail("super.admin@email.com");

        when(roleRepository.findByName(RoleEnum.SUPER_ADMIN)).thenReturn(Optional.of(superAdminRole));
        when(userRepository.findByEmail("super.admin@email.com")).thenReturn(Optional.of(existingUser));

        adminSeeder.onApplicationEvent(null);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRoleNotFoundLogsErrorAndDoesNothing() {
        when(roleRepository.findByName(RoleEnum.SUPER_ADMIN)).thenReturn(Optional.empty());

        adminSeeder.onApplicationEvent(null);

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
    }
}
