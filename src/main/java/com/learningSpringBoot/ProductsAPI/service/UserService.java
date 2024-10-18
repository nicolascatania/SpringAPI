package com.learningSpringBoot.ProductsAPI.service;

import com.learningSpringBoot.ProductsAPI.dto.AuthResponseDTO;
import com.learningSpringBoot.ProductsAPI.dto.UpdatedUserDTO;
import com.learningSpringBoot.ProductsAPI.dto.UserDTO;
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
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
    }

    public ResponseEntity<?> updateUser(UpdatedUserDTO updatedUserDTO) {
        Optional<User> user = userRepository.findUserByName(updatedUserDTO.getName());

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with name: '" + updatedUserDTO.getName() + "' not found.");
        }

        User userToUpdate = user.get();

        // Actualizar el nombre si es necesario
        if (updatedUserDTO.getNewName() != null && !updatedUserDTO.getNewName().isEmpty()) {
            // Verifica si el nuevo nombre ya existe
            if (userRepository.findUserByName(updatedUserDTO.getNewName()).isPresent()) {
                throw new UserAlreadyExistsException("User with name: '" + updatedUserDTO.getNewName() + "' already exists.");
            }
            userToUpdate.setName(updatedUserDTO.getNewName());
        }

        // Actualizar el correo electrónico si es necesario
        if (updatedUserDTO.getNewEmail() != null && !updatedUserDTO.getNewEmail().isEmpty()) {
            if (!userRepository.existsByEmail(updatedUserDTO.getNewEmail())) {
                userToUpdate.setEmail(updatedUserDTO.getNewEmail());
            } else if (!userToUpdate.getEmail().equals(updatedUserDTO.getNewEmail())
                    && userRepository.existsByEmail(updatedUserDTO.getNewEmail())) {
                throw new EmailAlreadyExistsException("Email already exists.");
            }
        }

        // Guardar los cambios en el usuario
        userRepository.save(userToUpdate);

        // Crear un nuevo UserDetails basado en el usuario actualizado

        UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userToUpdate.getName());

        // Crear un nuevo objeto Authentication
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // No necesitas la contraseña
                userDetails.getAuthorities() // Roles
        );

        // Establecer la nueva autenticación en el contexto
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Generar un nuevo token con los datos actualizados del usuario (nuevo username o email)
        String newToken = jwtGenerator.generateToken(newAuth);

        UserDTO userDTO = new UserDTO();
        userDTO.setName(userToUpdate.getName());
        userDTO.setEmail(userToUpdate.getEmail());

        AuthResponseDTO response = new AuthResponseDTO(newToken, userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> deleteUserById(UserDTO userDTO) {
        User user = (User) userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with email '" + userDTO.getEmail() + "' not found."));

        user.getRoles().clear();

        userRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
