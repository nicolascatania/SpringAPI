package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class PasswordChangeResponseDTO {
    private String token;
    private String message;

    public PasswordChangeResponseDTO(String token, String message) {
        this.token = token;
        this.message = message;
    }


}
