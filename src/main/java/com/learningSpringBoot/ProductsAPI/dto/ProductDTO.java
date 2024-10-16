package com.learningSpringBoot.ProductsAPI.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private String description;
    private double price;
    private int stock;
    private String image_url;
    private String category_name;
    //Todo: category_name should be category_id or an INT to be more standarized an avoid problems with human errors when typing the category name.
}