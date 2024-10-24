package com.learningSpringBoot.ProductsAPI.service;
import com.learningSpringBoot.ProductsAPI.dto.ProductDTO;
import com.learningSpringBoot.ProductsAPI.dto.ProductResponseDTO;
import com.learningSpringBoot.ProductsAPI.dto.UpdatedProductDTO;
import com.learningSpringBoot.ProductsAPI.exceptions.CategoryNotFoundException;
import com.learningSpringBoot.ProductsAPI.exceptions.ProductAlreadyExistsException;
import com.learningSpringBoot.ProductsAPI.model.Category;
import com.learningSpringBoot.ProductsAPI.model.Product;
import com.learningSpringBoot.ProductsAPI.repository.CategoryRepository;
import com.learningSpringBoot.ProductsAPI.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    final CategoryRepository categoryRepository;
    final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ResponseEntity<String> createProduct(ProductDTO productDTO) {
        if (productRepository.findByName(productDTO.getName()).isPresent()) {
            throw new ProductAlreadyExistsException("A product with the name '" + productDTO.getName() + "' already exists.");
        }

        Optional<Category> categoryOpt = categoryRepository.findById(productDTO.getCategory_id());

        if (categoryOpt.isEmpty()) {
            throw new CategoryNotFoundException("Category ID does not exist");
        }
        Category category = categoryOpt.get();

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setImage_url(productDTO.getImage_url());
        product.setDescription(productDTO.getDescription());
        product.setCategory(category);

        productRepository.save(product);

        return new ResponseEntity<>("Product created Successfully", HttpStatus.CREATED);
    }


    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductResponseDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(productDTOs, productDTOs.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    private ProductResponseDTO convertToDTO(Product product) {
        return new ProductResponseDTO(product.getName(), product.getDescription(), product.getPrice(), product.getStock(),
                product.getImage_url(), product.getCategory().getName());
    }


    public ResponseEntity<String> updateProduct(UpdatedProductDTO updatedProductDTO) {

        Optional<Product> existingProductOpt = productRepository.findByName(updatedProductDTO.getOldName());

        if (existingProductOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Product> duplicateProductOpt = productRepository.findByName(updatedProductDTO.getName());
        if (duplicateProductOpt.isPresent()) {
            int existingProductId = existingProductOpt.get().getId();
            int duplicateProductId = duplicateProductOpt.get().getId();

            if (existingProductId != duplicateProductId) {
                throw new ProductAlreadyExistsException("A product with the name '" + updatedProductDTO.getName() + "' already exists.");
            }
        }

        Optional<Category> categoryOpt = categoryRepository.findById(updatedProductDTO.getCategory_id());

        if (categoryOpt.isEmpty()) {
            throw new CategoryNotFoundException("Category ID does not exist");
        }
        Category category = categoryOpt.get();


        Product existingProduct = existingProductOpt.get();
        existingProduct.setName(updatedProductDTO.getName());
        existingProduct.setDescription(updatedProductDTO.getDescription());
        existingProduct.setPrice(updatedProductDTO.getPrice());
        existingProduct.setStock(updatedProductDTO.getStock());
        existingProduct.setImage_url(updatedProductDTO.getImage_url());
        existingProduct.setCategory(category);
        Product savedProduct = productRepository.save(existingProduct);

        return new ResponseEntity<>("product updated successfully", HttpStatus.OK);
    }


    public ResponseEntity<Void> deleteProduct(String name) {
        Optional<Product> productOpt = productRepository.findByName(name);
        if (productOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productRepository.delete(productOpt.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}