package com.billing.billing_service.controllers;

import com.billing.billing_service.dtos.RegisterUserDto;
import com.billing.billing_service.models.Role;
import com.billing.billing_service.models.RoleEnum;
import com.billing.billing_service.models.User;
import com.billing.billing_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private RegisterUserDto registerUserDto;
    private User user;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("admin@example.com");
        registerUserDto.setPassword("password123");
        registerUserDto.setRole(RoleEnum.SUPER_ADMIN);  // Assuming this is string mapped to RoleEnum.ADMIN
        registerUserDto.setFullName("Admin User");

        Role role = new Role()
                .setName(RoleEnum.SUPER_ADMIN)
                .setDescription("Admin role");

        user = new User()
                .setId(1)
                .setEmail("admin@example.com")
                .setPassword("hashed_password")
                .setFullName("Admin User")
                .setRole(role);
    }

    @Test
    void testCreateUser_Success() {
        when(userService.createUser(registerUserDto)).thenReturn(user);

        ResponseEntity<User> response = adminController.createUser(registerUserDto);


        assertEquals("admin@example.com", response.getBody().getUsername());
        assertEquals("Admin User", response.getBody().getFullName()); // ✅ fixed this line
        assertEquals(RoleEnum.SUPER_ADMIN, response.getBody().getRole().getName());

        verify(userService, times(1)).createUser(registerUserDto);
    }
}
