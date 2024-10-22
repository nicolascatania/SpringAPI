package com.learningSpringBoot.ProductsAPI.controller;

import com.learningSpringBoot.ProductsAPI.dto.*;
import com.learningSpringBoot.ProductsAPI.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdatedUserDTO updatedUserDTO) {
        return userService.updateUser(updatedUserDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestBody String name) {
        return userService.deleteUser(name);
    }

    @PutMapping("/change-password")
    public ResponseEntity<PasswordChangeResponseDTO> changePassword(@RequestBody ChangedPasswordDTO changedPasswordDTO) {
        return userService.changePassword(changedPasswordDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getUsers")
    public ResponseEntity<List<UserWithRolesDTO>> getAllUsers() {
        return userService.getUsers();
    }
}
