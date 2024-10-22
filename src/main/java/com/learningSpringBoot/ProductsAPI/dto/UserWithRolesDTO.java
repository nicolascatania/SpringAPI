package com.learningSpringBoot.ProductsAPI.dto;

import com.learningSpringBoot.ProductsAPI.model.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserWithRolesDTO {
    private String name;
    private String email;
    private List<Role> roles;

}