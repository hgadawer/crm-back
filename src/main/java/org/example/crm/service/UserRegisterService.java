package org.example.crm.service;

import org.example.crm.DTO.UserInfoDTO;
import org.example.crm.DTO.UserRegisterParam;
import org.example.crm.result.R;

public interface UserRegisterService {
    R register(String email, String password, String code);

    R sendVerifyCode(String email);

    UserInfoDTO getUserInfo(Long uid);

    R updatePassword(String email, String password, String code);

}
