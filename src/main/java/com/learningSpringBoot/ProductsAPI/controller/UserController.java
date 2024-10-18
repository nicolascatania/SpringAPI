package com.learningSpringBoot.ProductsAPI.controller;

import com.learningSpringBoot.ProductsAPI.dto.UpdatedUserDTO;
import com.learningSpringBoot.ProductsAPI.dto.UserDTO;
import com.learningSpringBoot.ProductsAPI.model.User;
import com.learningSpringBoot.ProductsAPI.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    //TODO : add an endpoint only to admins so they can see the list of users and manage them (delete them)

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*@PostMapping
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        return userService.createUser(user.getName(), user.getEmail(), user.getPassword());
    }*/

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdatedUserDTO updatedUserDTO) {
        return userService.updateUser(updatedUserDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUserById(@RequestBody UserDTO userDTO) {
        return userService.deleteUserById(userDTO);
    }
}
