package com.learningSpringBoot.ProductsAPI.controller;

import com.learningSpringBoot.ProductsAPI.constants.RolesConstants;
import com.learningSpringBoot.ProductsAPI.dto.*;
import com.learningSpringBoot.ProductsAPI.model.Role;
import com.learningSpringBoot.ProductsAPI.model.User;
import com.learningSpringBoot.ProductsAPI.repository.RoleRepository;
import com.learningSpringBoot.ProductsAPI.repository.UserRepository;
import com.learningSpringBoot.ProductsAPI.security.JwtGenerator;
import com.learningSpringBoot.ProductsAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtGenerator;

    @Autowired
    public AuthController(UserRepository userRepository, AuthenticationManager authenticationManager,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                          JwtGenerator jwtGenerator, UserService userService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.userService = userService;
    }



    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getName(),loginDTO.getPassword())
            );

            Optional<User> user = userRepository.findUserByName(loginDTO.getName());

            if(user.isPresent() && passwordEncoder.matches(loginDTO.getPassword(),user.get().getPassword())){
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtGenerator.generateToken(authentication);
                UserDTO userDTO = new UserDTO();
                userDTO.setName(user.get().getName());
                userDTO.setEmail(user.get().getEmail());
                AuthResponseDTO response = new AuthResponseDTO(token, userDTO);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            LoginErrorDTO errorResponse = new LoginErrorDTO("Invalid credentials.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);


    }

    //TODO: logout function
    //TODO: resolve refresh token thing
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO){
        if(userRepository.existsByName(registerDTO.getName())){
            return new ResponseEntity<>("Username is taken.", HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(registerDTO.getEmail())){
            return new ResponseEntity<>("Email is taken.", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        List<Role> roles = new ArrayList<>();
        roles.add(new Role(RolesConstants.USER_ROLE_ID, RolesConstants.USER_ROLE));

        user.setRoles(roles);

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/make-admin")
    public ResponseEntity<String> makeAdmin(@RequestBody String name) {
        Optional<User> userOptional = userRepository.findUserByName(name);
        if (userOptional.isPresent()) {
            User updateUser = userOptional.get();
            List<Role> roles = updateUser.getRoles();
            if (roles == null) {
                roles = new ArrayList<>();
            }

            boolean isAdmin = roles.stream().anyMatch(role -> role.getName().equals(RolesConstants.ADMIN_ROLE));
            if (!isAdmin) {
                roles.add(new Role(RolesConstants.ADMIN_ROLE_ID, RolesConstants.ADMIN_ROLE));
                updateUser.setRoles(roles);
                userRepository.save(updateUser);
                return new ResponseEntity<>("Operation successful.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User is already an admin.", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("Username not found.", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/remove-admin")
    public ResponseEntity<String> removeAdmin(@RequestBody String name) {
        Optional<User> userOptional = userRepository.findUserByName(name);
        if (userOptional.isPresent()) {
            User updateUser = userOptional.get();
            List<Role> roles = updateUser.getRoles();

            if (roles != null) {
                boolean isAdmin = roles.stream().anyMatch(role -> role.getName().equals(RolesConstants.ADMIN_ROLE));
                if (isAdmin) {
                    roles.removeIf(role -> role.getName().equals(RolesConstants.ADMIN_ROLE));
                    updateUser.setRoles(roles);
                    userRepository.save(updateUser);
                    return new ResponseEntity<>("Admin role removed successfully.", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("User is not an admin.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("User has no roles assigned.", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("Username not found.", HttpStatus.NOT_FOUND);
    }


}
