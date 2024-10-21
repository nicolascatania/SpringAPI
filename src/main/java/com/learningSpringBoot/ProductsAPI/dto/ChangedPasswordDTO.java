package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class ChangedPasswordDTO {
    private String name;
    private String oldPassword;
    private String newPassword;
}
