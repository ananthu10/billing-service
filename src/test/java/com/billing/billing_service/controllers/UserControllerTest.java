package com.billing.billing_service.controllers;

import com.billing.billing_service.models.User;
import com.billing.billing_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticatedUser_ReturnsCurrentUser() {

        User mockUser = new User();
        mockUser.setEmail("testUser");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<User> response = userController.authenticatedUser();
        assertEquals("testUser", response.getBody().getUsername());
    }

    @Test
    void allUsers_ReturnsListOfUsers() {
        User user1 = new User();
        user1.setEmail("admin");

        User user2 = new User();
        user2.setEmail("superadmin");

        List<User> mockUsers = Arrays.asList(user1, user2);

        when(userService.allUsers()).thenReturn(mockUsers);

        ResponseEntity<List<User>> response = userController.allUsers();

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("admin", response.getBody().get(0).getUsername());
    }
}