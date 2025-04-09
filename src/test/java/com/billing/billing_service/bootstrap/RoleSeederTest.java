package com.billing.billing_service.bootstrap;

import com.billing.billing_service.models.Role;
import com.billing.billing_service.models.RoleEnum;
import com.billing.billing_service.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;

import static org.mockito.Mockito.*;

class RoleSeederTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ContextRefreshedEvent event;

    @InjectMocks
    private RoleSeeder roleSeeder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAllRolesAlreadyExist_NoNewRoleSaved() {
        // All roles already exist
        for (RoleEnum roleEnum : RoleEnum.values()) {
            when(roleRepository.findByName(roleEnum)).thenReturn(Optional.of(new Role().setName(roleEnum)));
        }

        roleSeeder.onApplicationEvent(event);

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testNoRolesExist_AllRolesCreated() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            when(roleRepository.findByName(roleEnum)).thenReturn(Optional.empty());
        }

        roleSeeder.onApplicationEvent(event);

        verify(roleRepository, times(3)).save(any(Role.class));
    }

    @Test
    void testSomeRolesExist_OnlyMissingRolesAreSaved() {
        when(roleRepository.findByName(RoleEnum.SELLER)).thenReturn(Optional.of(new Role().setName(RoleEnum.SELLER)));
        when(roleRepository.findByName(RoleEnum.BUYER)).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleEnum.SUPER_ADMIN)).thenReturn(Optional.empty());

        roleSeeder.onApplicationEvent(event);

        verify(roleRepository, times(2)).save(any(Role.class));
        verify(roleRepository, never()).save(argThat(role -> role.getName() == RoleEnum.SELLER));
    }
}
