package com.example.bankcards.service.impl;

import com.example.bankcards.config.app.AppConf;
import com.example.bankcards.dto.api.crypto.EncryptedCardNumber;
import com.example.bankcards.dto.api.req.AddCardDTO;
import com.example.bankcards.dto.api.req.EnrollDTO;
import com.example.bankcards.dto.api.req.SearchReq;
import com.example.bankcards.dto.api.req.UpdateCardDTO;
import com.example.bankcards.dto.api.req.filters.CardSearchFilter;
import com.example.bankcards.dto.api.resp.CardDTO;
import com.example.bankcards.dto.api.resp.PageResp;
import com.example.bankcards.dto.mappers.CardMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import com.example.bankcards.exception.ProhibitedException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.impl.spec.CardSpecification;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.EncryptionService;
import com.example.bankcards.util.PrincipalExtractor;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the {@link CardService} interface.
 * Handles user`s cards.
 */
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    /**
     * The AppConf bean.
     *
     * @see AppConf
     */
    private final AppConf appConf;
    /**
     * The CardRepository bean.
     *
     * @see CardRepository
     * @see CardSpecification
     */
    private final CardRepository cardRepository;
    /**
     * The UserRepository bean.
     *
     * @see UserRepository
     */
    private final UserRepository userRepository;
    /**
     * The EncryptionService bean.
     *
     * @see EncryptionService
     */
    private final EncryptionService encryptionService;
    /**
     * The CardMapper bean.
     *
     * @see CardMapper
     */
    private final CardMapper cardMapper;

    /**
     * Creates a new card for the specified user and card type.
     *
     * @param request Contains information about the card to be created (owner, card type, etc.)
     * @return The ID of the newly created card.
     * @throws UserNotFoundException If the user (owner) is not found.
     */
    @Override
    public Long createCard(AddCardDTO request) {

        checkIfActionIsProhibited();

        Long ownerId = request.getOwnerId();
        CardType cardType = request.getCardType();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));


        Optional<Card> lastCardOpt = cardRepository.findLastByCardType(ownerId, cardType);
        String lastCardNumber = lastCardOpt.map(Card::getNumber).orElse(null);
        String decodeLastNumber = encryptionService.decrypt(lastCardNumber);

        String newCardNumber = CardNumberGenerator.generate(cardType, ownerId, decodeLastNumber);
        LocalDate expiration = LocalDate.now().plusYears(appConf.getCardExpirationYears());

        Card card = Card.builder()
                .type(cardType)
                .number(newCardNumber)
                .owner(owner)
                .status(CardStatus.ACTIVE)
                .expiration(expiration)
                .isDeleted(false)
                .build();

        return cardRepository.save(card).getId();
    }

    /**
     * Updates the balance on the card by adding the specified amount.
     *
     * @param request Contains information about the card and the amount to be added.
     * @return The ID of the updated card.
     * @throws IllegalArgumentException If the card is not found or is blocked or expired.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Long updateCard(UpdateCardDTO request) {

        checkIfActionIsProhibited();

        EnrollDTO enrollment = request.getEnrollment();
        BigDecimal amount = enrollment.getAmount();

        Long cardId = resolveCardId(request);

        int updatedRows = cardRepository.addToBalance(cardId, amount);

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Card not found or blocked or expired! Id: " + cardId);
        }

        /*
            Можно сделать историю зачислений и переводов и хранить ее по каждой карте в виде json не привязываясь к типу перевода
            или в зависимости от вида зачисления/перевода/снятия
        */
        return cardId;
    }

    /**
     * Deletes a card by setting its "isDeleted" flag to true.
     *
     * @param id The ID of the card to be deleted.
     * @return The ID of the deleted card.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Long deleteCard(Long id) {

        checkIfActionIsProhibited();

        Card card = cardRepository.findById(id).orElseThrow();
        card.setIsDeleted(true);
        return cardRepository.save(card).getId();
    }

    /**
     * Sets the status of the card (e.g., ACTIVE, BLOCKED, EXPIRED).
     *
     * @param id     The ID of the card.
     * @param status The status to be set.
     * @return The ID of the card with the updated status.
     * @throws DataIntegrityViolationException If the card is expired or the status is already set.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Long setCardStatus(@NotNull Long id, @NotNull CardStatus status) {

        checkIfActionIsProhibited();

        Card card = cardRepository.findById(id).orElseThrow();
        if (CardStatus.EXPIRED.equals(card.getStatus()))
            throw new DataIntegrityViolationException("Card is expired! Id: " + id);
        if (card.getStatus().equals(status))
            throw new DataIntegrityViolationException("Status already set! Card id: " + id);

        card.setStatus(status);
        return cardRepository.save(card).getId();
    }

    /**
     * Retrieves the details of a card.
     *
     * @param id The ID of the card to be fetched.
     * @return A DTO containing the card details.
     * @throws ProhibitedException If the current user does not have permission to view the card.
     */
    @Transactional(readOnly = true)
    @Override
    public CardDTO getCard(Long id) {
        User user = PrincipalExtractor.getCurrentUser();

        if (Objects.nonNull(user) && (user.isAdmin()
                || cardRepository.existsByIdAndOwnerId(id, user.getId()))) {

            return cardRepository.findById(id)
                    .map(cardMapper::toDto)
                    .orElseThrow();
        }

        Long ownerId = Objects.nonNull(user) ? user.getId() : null;
        throw new ProhibitedException(ownerId);
    }

    /**
     * Retrieves a paginated list of cards based on the provided search criteria.
     *
     * @param req The search request containing pagination and filtering details.
     * @return A page response containing the list of cards that match the search criteria.
     */
    @Transactional(readOnly = true)
    @Override
    public PageResp<CardDTO> getCards(SearchReq<CardSearchFilter> req) {
        User user = PrincipalExtractor.getCurrentUser();
        Long ownerId = Objects.nonNull(user) ? user.getId() : null;

        Page<Card> result = Optional.ofNullable(req)
                .map(r -> PageRequest.of(req.getPageNumber(), req.getPageSize()))
                .map(pr -> {

                    CardSearchFilter searchFilter = req.getFilter();

                    if (Objects.nonNull(user) && !user.isAdmin()) {
                        return Objects.isNull(searchFilter) ? cardRepository.findAllByOwner_Id(ownerId, pr) :
                                cardRepository.findAll(CardSpecification.search(searchFilter, ownerId), pr);
                    }

                    return Objects.isNull(searchFilter) ? cardRepository.findAll(pr) :
                            cardRepository.findAll(CardSpecification.search(searchFilter, null), pr);

                })
                .orElseThrow();

        boolean isAdmin = Objects.nonNull(user) && user.isAdmin();

        return cardMapper.toPageResp(result, isAdmin);
    }

    /**
     * Checks if the current action is prohibited for the current user (non-admin users are prohibited).
     *
     * @throws ProhibitedException If the current user is not an admin.
     */
    private void checkIfActionIsProhibited() {
        User user = PrincipalExtractor.getCurrentUser();

        if (Objects.nonNull(user)) {
            if (!user.isAdmin()) {
                throw new ProhibitedException(user.getId());
            }
        }
    }

    /**
     * Resolves the card ID based on the provided request, either by card ID or by card number.
     *
     * @param request The request containing the card ID or card number.
     * @return The resolved card ID.
     * @throws IllegalArgumentException If neither a card ID nor a card number is provided.
     */
    private Long resolveCardId(UpdateCardDTO request) {
        EncryptedCardNumber number = request.getCardNumber();

        if (request.getCardId() != null) {
            return request.getCardId();
        } else if (request.getCardNumber() != null) {
            String decoded = encryptionService.decrypt(number.encrypted());

            return cardRepository.findIdByNumber(decoded)
                    .orElseThrow(() -> new IllegalArgumentException("Card not found by number: " + number.cardMask()));
        } else {
            throw new IllegalArgumentException("ID or card number must be specified! Request: " + request);
        }
    }

    /**
     * A utility class for generating card numbers based on card type, owner ID, and last card number.
     */
    private static class CardNumberGenerator {
        private static final int MAX_CARD_NUMBER_LENGTH = 15;

        /**
         * Generates a new card number based on the specified parameters.
         *
         * @param type       The type of the card (e.g., VISA, MASTERCARD).
         * @param ownerId    The ID of the card owner.
         * @param lastNumber The last card number to generate the new one from (may be null).
         * @return The newly generated card number.
         * @throws IllegalStateException If the generated card number exceeds the maximum allowed length.
         */
        public static String generate(CardType type, Long ownerId, String lastNumber) {
            String bin = getBinPrefix(type);
            String base;

            if (lastNumber == null) {
                base = bin + String.format("%06d", ownerId) + "000";
            } else {
                String numericPart = lastNumber.substring(0, lastNumber.length() - 1);

                BigInteger nextNum = new BigInteger(numericPart).add(BigInteger.ONE);
                base = String.format("%015d", nextNum);

                if (base.length() != MAX_CARD_NUMBER_LENGTH) {
                    throw new IllegalStateException("Card number overflow");
                }
            }
            return base + calculateLuhnCheckDigit(base);
        }

        /**
         * Gets the BIN prefix based on the card type.
         *
         * @param type The type of the card (e.g., VISA, MASTERCARD).
         * @return The corresponding BIN prefix.
         * @see CardType
         */
        private static String getBinPrefix(CardType type) {
            return switch (type) {
                case VISA -> CardType.VISA.getPrefix();
                case MASTERCARD -> CardType.MASTERCARD.getPrefix();
                case AMERICAN_EXPRESS -> CardType.AMERICAN_EXPRESS.getPrefix();
                case BANK_SPECIFIC -> CardType.BANK_SPECIFIC.getPrefix();
            };
        }

        /**
         * How it works:
         * 1. Take all the digits from right to left, but not including the last one (this is the one we want to calculate).
         * 2. Double every second digit (n *= 2).
         * 3. If it turns out to be more than 9, subtract 9 (if (n > 9) n -= 9;), i.e. equivalent to the sum of the digits.
         * 4. Add up all the resulting numbers.
         * 5. Check digit = (10 - (sum % 10)) % 10.
         * This works because the sum of all digits, including the check digit, must be a multiple of 10.
         * <p>
         * Example:
         * Base number (without last digit): 4111 1111 1111 111_
         * Algorithm pass → sum = 30
         * Check digit = (10 - (30 % 10)) % 10 = 0
         * Total: 41111111111111110
         *
         * @param numberWithoutCheckDigit the last card number in decoded form
         * @return a new card number as int
         */
        public static int calculateLuhnCheckDigit(String numberWithoutCheckDigit) {
            int sum = 0;
            boolean alternate = true;

            for (int i = numberWithoutCheckDigit.length() - 1; i >= 0; i--) {
                int n = Character.getNumericValue(numberWithoutCheckDigit.charAt(i));
                if (alternate) {
                    n *= 2;
                    if (n > 9) n -= 9;
                }
                sum += n;
                alternate = !alternate;
            }

            return (10 - (sum % 10)) % 10;
        }
    }

}
