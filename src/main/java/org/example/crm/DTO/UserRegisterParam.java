package org.example.crm.DTO;

import lombok.Data;

@Data
public class UserRegisterParam {
    private String email;
    private String code;
    private String password;
}
