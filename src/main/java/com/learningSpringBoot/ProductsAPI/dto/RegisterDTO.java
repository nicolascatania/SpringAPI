package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String name;
    private String email;
    private String password;
}
