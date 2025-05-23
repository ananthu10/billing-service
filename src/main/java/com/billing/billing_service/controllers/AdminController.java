package com.billing.billing_service.controllers;


import com.billing.billing_service.dtos.RegisterUserDto;
import com.billing.billing_service.models.User;
import com.billing.billing_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admins")
@RestController
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody RegisterUserDto registerUserDto) {
        User createdAdmin = userService.createUser(registerUserDto);

        return ResponseEntity.ok(createdAdmin);
    }

}