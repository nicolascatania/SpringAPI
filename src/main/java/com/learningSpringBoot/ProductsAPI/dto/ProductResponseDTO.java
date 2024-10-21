package com.learningSpringBoot.ProductsAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponseDTO {
    private String name;
    private String description;
    private double price;
    private int stock;
    private String image_url;
    private String category_name;
}