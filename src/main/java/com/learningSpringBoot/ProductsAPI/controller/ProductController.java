package com.learningSpringBoot.ProductsAPI.controller;

import com.learningSpringBoot.ProductsAPI.dto.ProductDTO;
import com.learningSpringBoot.ProductsAPI.dto.UpdatedProductDTO;
import com.learningSpringBoot.ProductsAPI.dto.UpdatedUserDTO;
import com.learningSpringBoot.ProductsAPI.exceptions.ProductAlreadyExistsException;
import com.learningSpringBoot.ProductsAPI.service.ProductService;
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

    //TODO: fix problem with categories, they are not beign saved/related, whatever, there is a problem.
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO product) throws ProductAlreadyExistsException {
            return productService.createProduct(product);
    }


    //TODO: make a page on the frontend so the adminn can remove a product
    //TODO: remove the id thing, use product name instead

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProductById(@PathVariable int id) {
        return productService.deleteProductById(id);
    }



    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UpdatedProductDTO> updateProduct(@RequestBody UpdatedProductDTO updatedProduct) throws ProductAlreadyExistsException {
        return productService.updateProduct(updatedProduct);
    }

}
