package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private final   String tokenType = "Bearer ";

    public AuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

}
