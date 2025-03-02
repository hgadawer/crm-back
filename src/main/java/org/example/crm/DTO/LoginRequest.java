package org.example.crm.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    // getter 和 setter
}