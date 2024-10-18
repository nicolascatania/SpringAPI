package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private final   String tokenType = "Bearer ";
    private UserDTO user;

    public AuthResponseDTO(String accessToken, UserDTO user) {
        this.accessToken = accessToken;
        this.user = user;
    }

}
