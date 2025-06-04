package com.example.bankcards.service.impl;

import com.example.bankcards.BaseTest;
import com.example.bankcards.dto.api.req.UserLoginReqDTO;
import com.example.bankcards.dto.api.resp.LoggedUserRespDTO;
import com.example.bankcards.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.bankcards.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceImplTest extends BaseTest {

    @Autowired
    private AuthService authService;

    @Test
    void loginUser_withEmail_success() {
        UserLoginReqDTO loginDTO = new UserLoginReqDTO();
        loginDTO.setUsername(ADMIN_USERNAME);
        loginDTO.setPassword(ADMIN_RAW_PASSWORD);

        LoggedUserRespDTO loggedUser = authService.loginUser(loginDTO);

        assertNotNull(loggedUser);
        assertNotNull(loggedUser.getAccessToken());
    }

    @Test
    void loginUser_withWrongPassword_fails() {
        UserLoginReqDTO loginDTO = new UserLoginReqDTO();
        loginDTO.setUsername(ADMIN_USERNAME);
        loginDTO.setPassword("wrongPassword");

        assertThrows(Exception.class, () -> authService.loginUser(loginDTO));
    }
}