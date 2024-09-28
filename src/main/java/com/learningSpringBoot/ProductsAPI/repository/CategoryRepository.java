package com.learningSpringBoot.ProductsAPI.repository;

import com.learningSpringBoot.ProductsAPI.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
