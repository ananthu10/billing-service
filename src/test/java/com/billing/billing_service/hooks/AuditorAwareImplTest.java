package com.billing.billing_service.hooks;

import com.billing.billing_service.models.User;
import com.billing.billing_service.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditorAwareImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private AuditorAwareImpl auditorAware;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auditorAware = new AuditorAwareImpl(userRepository);
    }

    @Test
    void testGetCurrentAuditor_WhenAuthenticated_ShouldReturnUser() {
        String email = "user@example.com";
        User user = new User().setEmail(email);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = auditorAware.getCurrentAuditor();

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getUsername());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testGetCurrentAuditor_WhenNoAuthentication_ShouldReturnEmpty() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = auditorAware.getCurrentAuditor();

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testGetCurrentAuditor_WhenNotAuthenticated_ShouldReturnEmpty() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = auditorAware.getCurrentAuditor();

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
