package com.example.bankcards.controller;

import com.example.bankcards.BaseTest;
import com.example.bankcards.utils.TestConstants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static com.example.bankcards.util.Constants.TOKEN_HEADER;
import static com.example.bankcards.util.Constants.TOKEN_TYPE;
import static com.example.bankcards.utils.TestConstants.*;
import static io.restassured.RestAssured.given;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.lessThan;


public class RestBaseTest extends BaseTest {
    protected static RequestSpecification requestSpecification;

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setUp() {
        requestSpecification = RestAssured.given()
                .baseUri(String.format(BASE_URL, port))
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);
    }

    protected ValidatableResponse checkStatusCodeAndBodyInPostRequest(String url, int code, String schema,
                                                                      Object requestBody, String validUserDataJson) {
        String accessToken = getAccessToken(validUserDataJson);
        return RestAssured.given(requestSpecification)
                .header(TOKEN_HEADER, TOKEN_TYPE + accessToken)
                .body(Objects.requireNonNullElse(requestBody, "{}"))
                .port(TestConstants.DEFAULT_APP_PORT)
                .post(url)
                .then()
                .statusCode(code)
                .body(matchesJsonSchemaInClasspath(schema))
                .time(lessThan(TestConstants.DEFAULT_TIMEOUT));
    }


    protected ValidatableResponse checkStatusCodeAndBodyInDeleteRequest(String url, int code, String schema,
                                                                        Long id, String validUserDataJson) {
        String accessToken = getAccessToken(validUserDataJson);
        return RestAssured.given(requestSpecification)
                .header(TOKEN_HEADER, TOKEN_TYPE + accessToken)
                .param(USER_ID_PARAM, id)
                .when()
                .delete(url)
                .then()
                .statusCode(code)
                .body(matchesJsonSchemaInClasspath(schema))
                .time(lessThan(TestConstants.DEFAULT_TIMEOUT));
    }

    protected ValidatableResponse checkStatusCodeAndBodyInGetRequest(String url, int code, String schema,
                                                                     Object requestBody, String validUserDataJson) {
        String accessToken = getAccessToken(validUserDataJson);
        return RestAssured.given(requestSpecification)
                .header(TOKEN_HEADER, TOKEN_TYPE + accessToken)
                .body(Objects.requireNonNullElse(requestBody, "{}"))
                .port(DEFAULT_APP_PORT)
                .get(url)
                .then()
                .statusCode(code)
                .body(matchesJsonSchemaInClasspath(schema))
                .time(lessThan(DEFAULT_TIMEOUT));
    }

    protected String getAccessToken(String validUserDataJson) {
        return given(requestSpecification)
                .body(validUserDataJson)
                .when()
                .post("/v1/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response()
                .jsonPath()
                .getString("accessToken");
    }

    @Test
    public void baseTest() {
    }

}