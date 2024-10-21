package com.learningSpringBoot.ProductsAPI.service;

import com.learningSpringBoot.ProductsAPI.dto.*;
import com.learningSpringBoot.ProductsAPI.exceptions.EmailAlreadyExistsException;
import com.learningSpringBoot.ProductsAPI.exceptions.UserAlreadyExistsException;
import com.learningSpringBoot.ProductsAPI.exceptions.UserNotFoundException;
import com.learningSpringBoot.ProductsAPI.model.User;
import com.learningSpringBoot.ProductsAPI.repository.UserRepository;
import com.learningSpringBoot.ProductsAPI.security.CustomUserDetailsService;
import com.learningSpringBoot.ProductsAPI.security.JwtGenerator;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final CustomUserDetailsService customUserDetailsService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.customUserDetailsService = customUserDetailsService;
    }

    public ResponseEntity<?> updateUser(UpdatedUserDTO updatedUserDTO) {
        Optional<User> user = userRepository.findUserByName(updatedUserDTO.getName());

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with name: '" + updatedUserDTO.getName() + "' not found.");
        }

        User userToUpdate = user.get();

        if (updatedUserDTO.getNewName() != null && !updatedUserDTO.getNewName().isEmpty()) {
            if (userRepository.findUserByName(updatedUserDTO.getNewName()).isPresent() && !(updatedUserDTO.getNewName().equals(userToUpdate.getName()))) {
                throw new UserAlreadyExistsException("User with name: '" + updatedUserDTO.getNewName() + "' already exists.");
            }
            userToUpdate.setName(updatedUserDTO.getNewName());
        }

        if (updatedUserDTO.getNewEmail() != null && !updatedUserDTO.getNewEmail().isEmpty()) {
            if (!userRepository.existsByEmail(updatedUserDTO.getNewEmail())) {
                userToUpdate.setEmail(updatedUserDTO.getNewEmail());
            } else if (!userToUpdate.getEmail().equals(updatedUserDTO.getNewEmail())
                    && userRepository.existsByEmail(updatedUserDTO.getNewEmail())) {
                throw new EmailAlreadyExistsException("Email already exists.");
            }
        }

        userRepository.save(userToUpdate);

        UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userToUpdate.getName());
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        String newToken = jwtGenerator.generateToken(newAuth);

        UserDTO userDTO = new UserDTO();
        userDTO.setName(userToUpdate.getName());
        userDTO.setEmail(userToUpdate.getEmail());

        AuthResponseDTO response = new AuthResponseDTO(newToken, userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> deleteUser(String name) {
        User user = getUserByName(name);

        user.getRoles().clear();

        userRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<PasswordChangeResponseDTO> changePassword(ChangedPasswordDTO changedPasswordDTO) {
        User user = getUserByName(changedPasswordDTO.getName());

        if (!passwordEncoder.matches(changedPasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password does not match.");
        }

        user.setPassword(passwordEncoder.encode(changedPasswordDTO.getNewPassword()));
        userRepository.save(user);

        String newToken = updateSecurityContextAndGenerateToken(user);

        PasswordChangeResponseDTO response = new PasswordChangeResponseDTO(newToken, "Password changed successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    private User getUserByName(String username) {
        return userRepository.findUserByName(username)
                .orElseThrow(() -> new UserNotFoundException("User with name: '" + username + "' not found."));
    }


    private String updateSecurityContextAndGenerateToken(User user) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getName());

        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return jwtGenerator.generateToken(newAuth);
    }


    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> users = userRepository.findAll();

        List<UserDTO> userList = users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }
}
