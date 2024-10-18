package com.learningSpringBoot.ProductsAPI.service;
import com.learningSpringBoot.ProductsAPI.dto.ProductDTO;
import com.learningSpringBoot.ProductsAPI.dto.UpdatedProductDTO;
import com.learningSpringBoot.ProductsAPI.exceptions.ProductAlreadyExistsException;
import com.learningSpringBoot.ProductsAPI.model.Category;
import com.learningSpringBoot.ProductsAPI.model.Product;
import com.learningSpringBoot.ProductsAPI.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImage_url(),
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }

    public Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setImage_url(productDTO.getImage_url());
        return product;
    }

    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO) {
        if (productRepository.findByName(productDTO.getName()).isPresent()) {
            throw new ProductAlreadyExistsException("A product with the name '" + productDTO.getName() + "' already exists.");
        }

        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        ProductDTO savedProductDTO = convertToDTO(savedProduct);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(productDTOs, HttpStatus.OK);
    }


    public ResponseEntity<UpdatedProductDTO> updateProduct(UpdatedProductDTO updatedProductDTO) {

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
        Product existingProduct = existingProductOpt.get();
        existingProduct.setName(updatedProductDTO.getName());
        existingProduct.setDescription(updatedProductDTO.getDescription());
        existingProduct.setPrice(updatedProductDTO.getPrice());
        existingProduct.setStock(updatedProductDTO.getStock());
        existingProduct.setImage_url(updatedProductDTO.getImage_url());

        Product savedProduct = productRepository.save(existingProduct);

        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }


    public ResponseEntity<Void> deleteProductById(int id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}