package com.example.bankcards.controller;

import com.example.bankcards.dto.api.req.EnrollDTO;
import com.example.bankcards.dto.api.req.UpdateCardDTO;
import com.example.bankcards.dto.api.req.notifications.CardBlockRequestNotificationDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.utils.ObjectBuilder;
import com.example.bankcards.utils.TestUtils;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static com.example.bankcards.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CardControllerTest extends RestBaseTest {

    private Long testUserId;
    private User user;

    private final List<Long> testCardIds = new ArrayList<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardBlockRequestRepository cardBlockRequestRepository;

    @BeforeAll
    void setupTestUserAndCards() {
        user = ObjectBuilder.buildCustomUser("test_user_" + System.currentTimeMillis(), ENCODED_PASSWORD, "Test", "User");
        User savedUser = userRepository.save(user);
        testUserId = savedUser.getId();

        CardType cardType = CardType.VISA;
        String prefix = cardType.getPrefix();
        int cardLength = cardType.getLength();

        String ownerIdPart = String.format("%06d", savedUser.getId() % 1_000_000);
        int sequence = 0;

        for (int i = 0; i < 5; i++) {
            String sequencePart = String.format("%03d", sequence++);

            String base = prefix + ownerIdPart + sequencePart;

            base = String.format("%-" + (cardLength - 1) + "s", base).replace(' ', '0');

            String cardNumber = base + TestUtils.calculateLuhnDigit(base);
            System.out.println("Generated valid card: " + cardNumber);

            Card card = ObjectBuilder.buildCustomCard(savedUser, cardType, cardNumber, 3, 1);
            Card savedCard = cardRepository.save(card);
            testCardIds.add(savedCard.getId());
        }

        setAuthentication(user.getUsername(), user.getPassword());
    }



    @AfterAll
    void cleanUp() {
        cardBlockRequestRepository.deleteAll();
        cardRepository.deleteAllById(testCardIds);
        cardRepository.deleteById(testCardIds.getLast() + 1);
        userRepository.deleteById(testUserId);
    }

    @Test
    void shouldReturnValidCardDTO_whenCardExists() {
        Long cardId = testCardIds.getFirst();

        ValidatableResponse response = checkStatusCodeAndBodyInGetRequest(
                "/v1/api/card/get/" + cardId,
                HttpStatus.OK.value(),
                SCHEME_SOURCE_PATH + "card_dto.json",
                null,
                String.format(RANDOM_CRED, user.getUsername(), REGULAR_RAW_PASSWORD)
        );

        String responseBody = response.extract().asString();
        assertNotNull(responseBody);
    }

    @Test
    void shouldReturnValidCardDTO_whenCardIsCreated() {

        ValidatableResponse response = checkStatusCodeAndBodyInPostRequest(
                "/v1/api/card/add",
                HttpStatus.CREATED.value(),
                SCHEME_SOURCE_PATH + "card_creation_response.json",
                String.format("{\"cardType\": 1, \"ownerId\": %d}", user.getId()),
                String.format(RANDOM_CRED, user.getUsername(), REGULAR_RAW_PASSWORD)
        );

        String responseBody = response.extract().asString();
        assertNotNull(responseBody);
    }

    @Test
    void shouldReturnUpdatedCardDTO_whenCardIsUpdated() {

        EnrollDTO enrollDTO = ObjectBuilder.buildEnrollDTO();

        UpdateCardDTO requestBody = UpdateCardDTO.builder()
                .cardId(AMEX_CARD_ID_OWNER_REGULAR)
                .enrollment(enrollDTO)
                .build();

        ValidatableResponse response = checkStatusCodeAndBodyInPostRequest(
                "/v1/api/card/admin/update",
                HttpStatus.OK.value(),
                SCHEME_SOURCE_PATH + "card_update_response.json",
                requestBody,
                String.format(RANDOM_CRED, user.getUsername(), REGULAR_RAW_PASSWORD)
        );

        String responseBody = response.extract().asString();
        assertNotNull(responseBody);
    }


    @Test
    void shouldReturnCardStatusUpdated_whenCardStatusIsUpdated() {
        Long cardId = testCardIds.get(1);
        CardStatus status = CardStatus.BLOCKED;

        ValidatableResponse response = checkStatusCodeAndBodyInPostRequest(
                "/v1/api/card/admin/" + cardId + "/" + status + "/update",
                HttpStatus.OK.value(),
                SCHEME_SOURCE_PATH + "card_status_update_response.json",
                null,
                String.format(RANDOM_CRED, user.getUsername(), REGULAR_RAW_PASSWORD)
        );

        String responseBody = response.extract().asString();
        assertNotNull(responseBody);
    }

    @Test
    void shouldReturnCardDeleted_whenCardIsDeleted() {
        Long cardId = testCardIds.get(2);

        ValidatableResponse response = checkStatusCodeAndBodyInDeleteRequest(
                "/v1/api/card/admin/" + cardId + "/delete",
                HttpStatus.OK.value(),
                SCHEME_SOURCE_PATH + "card_delete_response.json",
                cardId,
                String.format(RANDOM_CRED, user.getUsername(), REGULAR_RAW_PASSWORD)
        );

        String responseBody = response.extract().asString();
        assertNotNull(responseBody);
    }

    @Test
    void shouldReturnCardBlockRequestCreated_whenCardBlockRequestIsMade() {
        CardBlockRequestNotificationDTO requestBody = CardBlockRequestNotificationDTO.builder()
                .cardId(testCardIds.get(4))
                .note("Block card due to fraud")
                .build();

        ValidatableResponse response = checkStatusCodeAndBodyInPostRequest(
                "/v1/api/card/block",
                HttpStatus.CREATED.value(),
                SCHEME_SOURCE_PATH + "card_block_request_response.json",
                requestBody,
                String.format(RANDOM_CRED, user.getUsername(), REGULAR_RAW_PASSWORD)
        );

        String responseBody = response.extract().asString();
        assertNotNull(responseBody);
    }

}
