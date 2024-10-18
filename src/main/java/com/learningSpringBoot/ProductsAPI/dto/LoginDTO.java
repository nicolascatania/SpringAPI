package com.learningSpringBoot.ProductsAPI.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String name;
    private String email;
    private String password;
    //TODO: need to aggregate the roles here cuz the frontend must know if the user is admin to show some hidden tools.
}
