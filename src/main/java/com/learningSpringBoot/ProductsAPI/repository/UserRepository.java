package com.learningSpringBoot.ProductsAPI.repository;

import com.learningSpringBoot.ProductsAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    Optional<User> findUserByName(String name);
}
