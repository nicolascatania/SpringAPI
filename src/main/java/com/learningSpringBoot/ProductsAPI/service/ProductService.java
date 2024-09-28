package com.learningSpringBoot.ProductsAPI.service;

import com.learningSpringBoot.ProductsAPI.model.Product;
import com.learningSpringBoot.ProductsAPI.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    final
    ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ResponseEntity<Product> createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(products, HttpStatus.OK); // 200 OK
    }


    public ResponseEntity<Product> getProductById(int id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Void> deleteProductById(int id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Product> updateProduct(int id, Product updatedProduct) {
        if (!productRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedProduct.setId(id);
        Product savedProduct = productRepository.save(updatedProduct);
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
    }


}
