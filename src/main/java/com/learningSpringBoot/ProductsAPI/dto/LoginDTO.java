package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String name;
    private String email; //TODO: investigate if email field is required in another function besides login
    private String password;
}
