package com.billing.billing_service.service;

import com.billing.billing_service.dtos.RegisterUserDto;
import com.billing.billing_service.models.Role;
import com.billing.billing_service.models.RoleEnum;
import com.billing.billing_service.models.User;
import com.billing.billing_service.repository.RoleRepository;
import com.billing.billing_service.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAllUsers_ShouldReturnAllUsers() {
        User user1 = new User().setId(1).setFullName("John").setEmail("john@example.com");
        User user2 = new User().setId(2).setFullName("Jane").setEmail("jane@example.com");

        List<User> users = List.of(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.allUsers();

        assertEquals(2, result.size());
        assertEquals("john@example.com", result.get(0).getUsername());
        assertEquals("jane@example.com", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUser_WhenRoleExists_ShouldCreateAndReturnUser() {
        RegisterUserDto dto = new RegisterUserDto()
                .setFullName("Alice")
                .setEmail("alice@example.com")
                .setPassword("password123")
                .setRole(RoleEnum.valueOf("SELLER"));

        Role role = new Role()
                .setName(RoleEnum.SELLER)
                .setDescription("Seller Role");

        when(roleRepository.findByName(RoleEnum.valueOf("SELLER"))).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        User createdUser = userService.createUser(dto);

        assertNotNull(createdUser);
        assertEquals("Alice", createdUser.getFullName());
        assertEquals("alice@example.com", createdUser.getUsername());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals(role, createdUser.getRole());
        verify(userRepository).save(any(User.class));
    }


    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
