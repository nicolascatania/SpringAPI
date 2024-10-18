package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class UpdatedUserDTO {
    private String name;
    private String newEmail;
    private String newName;
}
