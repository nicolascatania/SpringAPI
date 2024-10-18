package com.learningSpringBoot.ProductsAPI.exceptions;


public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
