package com.learningSpringBoot.ProductsAPI.controller;

import com.learningSpringBoot.ProductsAPI.dto.AuthResponseDTO;
import com.learningSpringBoot.ProductsAPI.dto.LoginDTO;
import com.learningSpringBoot.ProductsAPI.dto.LoginErrorDTO;
import com.learningSpringBoot.ProductsAPI.dto.RegisterDTO;
import com.learningSpringBoot.ProductsAPI.model.Role;
import com.learningSpringBoot.ProductsAPI.model.User;
import com.learningSpringBoot.ProductsAPI.repository.RoleRepository;
import com.learningSpringBoot.ProductsAPI.repository.UserRepository;
import com.learningSpringBoot.ProductsAPI.security.JwtGenerator;
import com.learningSpringBoot.ProductsAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                AuthResponseDTO response = new AuthResponseDTO(token);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            LoginErrorDTO errorResponse = new LoginErrorDTO("Invalid credentials.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);


    }



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

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            return new ResponseEntity<>("Role not found-", HttpStatus.BAD_REQUEST);
        }

        List<Role> roles = Collections.singletonList(role);
        user.setRoles(roles);

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }
}
