package com.learningSpringBoot.ProductsAPI.service;

import com.learningSpringBoot.ProductsAPI.exceptions.EmailAlreadyExistsException;
import com.learningSpringBoot.ProductsAPI.exceptions.UserAlreadyExistsException;
import com.learningSpringBoot.ProductsAPI.exceptions.UserNotFoundException;
import com.learningSpringBoot.ProductsAPI.model.Role;
import com.learningSpringBoot.ProductsAPI.model.User;
import com.learningSpringBoot.ProductsAPI.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.learningSpringBoot.ProductsAPI.constants.RolesConstants.USER_ROLE;
import static com.learningSpringBoot.ProductsAPI.constants.RolesConstants.USER_ROLE_ID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<Void> createUser(String name, String email, String password) {

        if (userRepository.existsByName(name)) {
            throw new UserAlreadyExistsException("Username '" + name + "' already in use.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("An account with email '" + email + "' already exists.");
        }


        User user = new User();
        Role defaultRole = new Role();
        defaultRole.setName(USER_ROLE);
        defaultRole.setId(USER_ROLE_ID);

        user.setName(name);
        user.setEmail(email);

        String hashedPassword = DigestUtils.sha256Hex(password);
        user.setPassword(hashedPassword);

        user.setRoles(new HashSet<>(Set.of(defaultRole)));

        userRepository.save(user);

        return  new ResponseEntity<>(HttpStatus.CREATED);
    }

    public ResponseEntity<Void> updateUser(int id, String name, String email) {

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id '" + id + "' not found.");
        }




        if (userRepository.existsByName(name)) {
            throw new UserAlreadyExistsException("Username '" + name + "' already in use.");
        }
        User userToUpdate = user.get();
        userToUpdate.setName(name);
        userToUpdate.setEmail(email);

        userRepository.save(userToUpdate);

        return  new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id '" + id + "' not found.");
        }
        userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
