package com.learningSpringBoot.ProductsAPI.service;

import com.learningSpringBoot.ProductsAPI.dto.CategoryDTO;
import com.learningSpringBoot.ProductsAPI.dto.ProductDTO;
import com.learningSpringBoot.ProductsAPI.model.Product;
import com.learningSpringBoot.ProductsAPI.model.Category;
import com.learningSpringBoot.ProductsAPI.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CategoryService {
    final
    CategoryRepository categoryRepository;
    private final ProductService productService;


    public CategoryService(CategoryRepository categoryRepository, ProductService productService) {
        this.categoryRepository = categoryRepository;
        this.productService = productService;
    }

    public CategoryDTO convertToCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getName(),
                category.getDescription()
        );
    }

    public Category convertDTOToCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return category;
    }

    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category category : categories) {
            categoryDTOS.add(convertToCategoryDTO(category));
        }
        return new ResponseEntity<>(categoryDTOS, HttpStatus.OK);
    }

    public ResponseEntity<CategoryDTO> getCategoryById(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(value -> new ResponseEntity<>(convertToCategoryDTO(value), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<CategoryDTO> addCategory(CategoryDTO categoryDTO) {
        Category category = convertDTOToCategory(categoryDTO);
        categoryRepository.save(category);
        return new ResponseEntity<>(convertToCategoryDTO(category), HttpStatus.CREATED);
    }

    public ResponseEntity<CategoryDTO> updateCategory(int id, CategoryDTO updatedCategoryDTO) {
        if (!categoryRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Category updatedCategory = convertDTOToCategory(updatedCategoryDTO);
        updatedCategory.setId(id);
        Category savedCategory = categoryRepository.save(updatedCategory);
        return new ResponseEntity<>(convertToCategoryDTO(savedCategory), HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteCategory(int id) {
        if (!categoryRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        categoryRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*public ResponseEntity<List<ProductDTO>> getProductsByCategoryId(int categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            List<Product> products = category.get().getCategoryProducts();
            List<ProductDTO> productDTOS = new ArrayList<>();
            for(Product p : products){
                productDTOS.add(productService.convertToDTO(p));
            }
            return new ResponseEntity<>(productDTOS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }*/

}
