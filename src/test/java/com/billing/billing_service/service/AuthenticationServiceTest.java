package com.billing.billing_service.service;

import com.billing.billing_service.dtos.LoginUserDto;
import com.billing.billing_service.models.User;
import com.billing.billing_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private LoginUserDto loginUserDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@example.com");
        loginUserDto.setPassword("password");

        mockUser = new User();
        mockUser.setEmail("test@example.com");
    }

    @Test
    void authenticate_ShouldReturnUser_WhenValidCredentials() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        assertNotNull(authenticatedUser);
        assertEquals("test@example.com", authenticatedUser.getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> authenticationService.authenticate(loginUserDto));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void allUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setEmail("a@example.com");

        User user2 = new User();
        user2.setEmail("b@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        var users = authenticationService.allUsers();

        assertEquals(2, users.size());
        assertEquals("a@example.com", users.get(0).getUsername());
        assertEquals("b@example.com", users.get(1).getUsername());
    }
}
