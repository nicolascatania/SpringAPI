package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String name;
    private String email;
    private String password;
}
