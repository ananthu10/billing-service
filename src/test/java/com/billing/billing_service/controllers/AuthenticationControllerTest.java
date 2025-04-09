package com.billing.billing_service.controllers;

import com.billing.billing_service.dtos.LoginResponseDTO;
import com.billing.billing_service.dtos.LoginUserDto;
import com.billing.billing_service.models.User;
import com.billing.billing_service.service.AuthenticationService;
import com.billing.billing_service.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_ReturnsToken_WhenCredentialsAreValid() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("testuser");
        loginUserDto.setPassword("password");

        User mockUser = new User();
        mockUser.setEmail("testuser");

        String mockToken = "mock-jwt-token";
        long mockExpiration = 3600;

        when(authenticationService.authenticate(loginUserDto)).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn(mockToken);
        when(jwtService.getExpirationTime()).thenReturn(mockExpiration);

        ResponseEntity<LoginResponseDTO> response = authenticationController.authenticate(loginUserDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockToken, response.getBody().getToken());
        assertEquals(mockExpiration, response.getBody().getExpiresIn());

        verify(authenticationService).authenticate(loginUserDto);
        verify(jwtService).generateToken(mockUser);
        verify(jwtService).getExpirationTime();
    }
}
