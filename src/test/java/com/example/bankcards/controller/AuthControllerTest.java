package com.example.bankcards.controller;

import com.example.bankcards.dto.api.req.UserLoginReqDTO;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.example.bankcards.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthControllerTest extends BaseUITest {

    @Test
    void shouldReturnValidLoginResponse_whenCredentialsAreCorrect() {
        UserLoginReqDTO req = new UserLoginReqDTO(ADMIN_USERNAME, ADMIN_RAW_PASSWORD);

        ValidatableResponse response = checkStatusCodeAndBodyInPostRequest(
                "/v1/api/auth/login",
                HttpStatus.OK.value(),
                SCHEME_SOURCE_PATH + "logged_user_resp.json",
                req,
                ADMIN_CRED
        );

        String responseBody = response.extract().asString();
        assertNotNull(responseBody);
    }
}
