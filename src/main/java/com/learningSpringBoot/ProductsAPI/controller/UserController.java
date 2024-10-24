package com.learningSpringBoot.ProductsAPI.controller;

import com.learningSpringBoot.ProductsAPI.dto.*;
import com.learningSpringBoot.ProductsAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            summary = "Update an existing user's information",
            description = "Updates a user's name and/or email based on the provided `UpdatedUserDTO`. This endpoint requires JWT authorization.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully and new JWT token issued",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "User already exists or email is already in use",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access, invalid JWT token",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdatedUserDTO updatedUserDTO) {
        return userService.updateUser(updatedUserDTO);
    }


    @Operation(
            summary = "Delete a user by username",
            description = "Deletes a user from the database based on the provided username. Requires JWT authorization and admin privileges.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access, invalid JWT token",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestBody String name) {
        return userService.deleteUser(name);
    }


    @Operation(
            summary = "Change a user's password",
            description = "Allows a user to change their password by providing the current password and a new password. Requires JWT authorization.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully, returns new JWT token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PasswordChangeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, old password does not match",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access, invalid JWT token",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/change-password")
    public ResponseEntity<PasswordChangeResponseDTO> changePassword(@RequestBody ChangedPasswordDTO changedPasswordDTO) {
        return userService.changePassword(changedPasswordDTO);
    }

    @Operation(summary = "Obtain all users",
            description = "Must have Admin role to access this endpoint",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getUsers")
    public ResponseEntity<List<UserWithRolesDTO>> getAllUsers() {
        return userService.getUsers();
    }
}
