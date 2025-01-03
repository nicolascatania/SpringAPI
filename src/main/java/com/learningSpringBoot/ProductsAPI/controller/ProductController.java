package com.learningSpringBoot.ProductsAPI.controller;

import com.learningSpringBoot.ProductsAPI.dto.ProductDTO;
import com.learningSpringBoot.ProductsAPI.dto.ProductResponseDTO;
import com.learningSpringBoot.ProductsAPI.dto.UpdatedProductDTO;
import com.learningSpringBoot.ProductsAPI.dto.UpdatedUserDTO;
import com.learningSpringBoot.ProductsAPI.exceptions.ProductAlreadyExistsException;
import com.learningSpringBoot.ProductsAPI.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    final
    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return productService.getAllProducts();
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO product) throws ProductAlreadyExistsException {
            return productService.createProduct(product);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@RequestBody String name) {
        return productService.deleteProduct(name);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateProduct(@RequestBody UpdatedProductDTO updatedProduct) throws ProductAlreadyExistsException {
        return productService.updateProduct(updatedProduct);
    }

}
