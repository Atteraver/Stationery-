package com.stationery.dto;

import com.stationery.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private Role role; // MANAGER or EMPLOYEE
    private Double maxPurchaseLimit;
}