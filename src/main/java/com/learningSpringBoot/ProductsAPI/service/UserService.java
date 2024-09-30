package com.learningSpringBoot.ProductsAPI.service;

import com.learningSpringBoot.ProductsAPI.model.Role;
import com.learningSpringBoot.ProductsAPI.model.User;
import com.learningSpringBoot.ProductsAPI.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.HashSet;
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


    public ResponseEntity<Void> deleteUserById(int id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
