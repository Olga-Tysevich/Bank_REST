package com.example.bankcards.service.impl;

import com.example.bankcards.config.app.AppConf;
import com.example.bankcards.dto.api.req.AddCardDTO;
import com.example.bankcards.dto.api.req.EnrollDTO;
import com.example.bankcards.dto.api.req.UpdateCardDTO;
import com.example.bankcards.dto.api.resp.CardDTO;
import com.example.bankcards.dto.mappers.CardMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.entity.enums.RoleEnum;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.EncryptionService;
import com.example.bankcards.util.PrincipalExtractor;
import com.example.bankcards.utils.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private AppConf appConf;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private CardMapper cardMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCard_ShouldCreateCardSuccessfully() {
        AddCardDTO request = new AddCardDTO(CardType.VISA, TestConstants.REGULAR_USER_ID);
        User mockUser = new User();
        mockUser.setId(TestConstants.REGULAR_USER_ID);

        when(userRepository.findById(TestConstants.REGULAR_USER_ID)).thenReturn(Optional.of(mockUser));
        when(cardRepository.findLastByCardType(any(), any())).thenReturn(Optional.empty());
        when(appConf.getCardExpirationYears()).thenReturn(3);
        when(cardRepository.save(any())).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            card.setId(1L);
            return card;
        });

        Long cardId = cardService.createCard(request);

        assertThat(cardId).isEqualTo(1L);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void updateCard_ShouldUpdateBalance() {
        UpdateCardDTO request = new UpdateCardDTO();
        EnrollDTO enroll = new EnrollDTO();
        enroll.setAmount(TestConstants.TRANSFER_1_AMOUNT);
        request.setEnrollment(enroll);
        request.setCardId(TestConstants.VISA_CARD_ID_OWNER_ADMIN);

        when(cardRepository.addToBalance(eq(TestConstants.VISA_CARD_ID_OWNER_ADMIN), eq(TestConstants.TRANSFER_1_AMOUNT)))
                .thenReturn(1);

        Long updatedCardId = cardService.updateCard(request);

        assertThat(updatedCardId).isEqualTo(TestConstants.VISA_CARD_ID_OWNER_ADMIN);
    }

    @Test
    void updateCard_ShouldFail_WhenCardNotFound() {
        UpdateCardDTO request = new UpdateCardDTO();
        EnrollDTO enroll = new EnrollDTO();
        enroll.setAmount(TestConstants.TRANSFER_1_AMOUNT);
        request.setEnrollment(enroll);
        request.setCardId(999L);

        when(cardRepository.addToBalance(anyLong(), any())).thenReturn(0);

        assertThatThrownBy(() -> cardService.updateCard(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Card not found");
    }

    @Test
    void deleteCard_ShouldMarkCardAsDeleted() {
        Card card = new Card();
        card.setId(5L);
        card.setIsDeleted(false);

        when(cardRepository.findById(5L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);

        Long deletedCardId = cardService.deleteCard(5L);

        assertThat(deletedCardId).isEqualTo(5L);
        assertThat(card.getIsDeleted()).isTrue();
    }

    @Test
    void setCardStatus_ShouldUpdateStatus() {
        Card card = new Card();
        card.setId(6L);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(6L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);

        Long updatedId = cardService.setCardStatus(6L, CardStatus.BLOCKED);

        assertThat(updatedId).isEqualTo(6L);
        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    void setCardStatus_ShouldFail_WhenStatusAlreadySet() {
        Card card = new Card();
        card.setId(6L);
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(6L)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.setCardStatus(6L, CardStatus.BLOCKED))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Status already set");
    }

    @Test
    void getCard_ShouldReturnCardDTO_ForAdmin() {
        Card card = new Card();
        card.setId(7L);

        User admin = new User();
        admin.setId(TestConstants.ADMIN_ID);
        admin.setRoleSet(Set.of(new Role(1, RoleEnum.ROLE_ADMIN)));

        CardDTO expectedDto = new CardDTO();

        try (MockedStatic<PrincipalExtractor> mockedStatic = mockStatic(PrincipalExtractor.class)) {
            mockedStatic.when(PrincipalExtractor::getCurrentUser).thenReturn(admin);
            when(cardRepository.findById(7L)).thenReturn(Optional.of(card));
            when(cardMapper.toDto(card)).thenReturn(expectedDto);

            CardDTO result = cardService.getCard(7L);

            assertThat(result).isEqualTo(expectedDto);
        }
    }

}
