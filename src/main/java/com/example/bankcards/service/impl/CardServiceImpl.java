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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the {@link CardService} interface.
 * Handles user`s cards.
 */
@Slf4j
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Long createCard(AddCardDTO request) {

        checkIfActionIsProhibited();

        Long ownerId = request.getOwnerId();
        CardType cardType = request.getCardType();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));


        Optional<Card> lastCardOpt = cardRepository.findLastByCardType(ownerId, cardType);
        String lastCardNumber = lastCardOpt.map(Card::getNumber).orElse(null);

        String newCardNumber = CardNumberGenerator.generate(cardType, ownerId, lastCardNumber);
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
    @Cacheable(
            value = "cardsBySearch",
            key = "#req.getFilter().toString() + #req.getPageNumber() + #req.getPageSize()",
            unless = "#result.objects.isEmpty()",
            cacheManager = "cacheManager"
    )
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
     * Marks expired cards as expired.
     * This method processes all cards that have expired and marks them with an expired status.
     */
    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 100)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markExpiredCards() {
        int page = 0;
        int size = 500;
        Page<Card> cards;

        do {
            cards = cardRepository.findCardsByExpirationBeforeAndStatusNot(PageRequest.of(page, size), LocalDate.now(), "EXPIRED");
            cards.forEach(card -> {
                try {
                    processCardExpiration(card);
                } catch (Exception e) {
                    log.error("Error processing expired card {}: {}", card.getId(), e.getMessage());
                }
            });
            page++;
        } while (cards.hasNext());
    }

    /**
     * Processes a single card expiration.
     * This method marks the card as expired.
     *
     * @param card the card to be marked as expired
     */
    @Transactional
    public void processCardExpiration(Card card) {
        card.setStatus(CardStatus.EXPIRED);
        cardRepository.save(card);
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
     * A utility class responsible for generating card numbers based on card type, owner ID, and the last issued card number.
     * The generated card number adheres to the Luhn algorithm and follows the card type's prefix and length requirements.
     *
     * <p>The card number generation process involves the following steps:
     * <ul>
     *   <li>Building the base number using the card type prefix, owner ID, and sequence number.</li>
     *   <li>Validating the length of the base number to ensure it matches the required card type length.</li>
     *   <li>Calculating the Luhn check digit and appending it to the base number.</li>
     * </ul>
     *
     * <p>The class ensures that no more than the allowed maximum number of cards can be generated for a particular owner.
     * If the card number exceeds the maximum sequence, an exception will be thrown.
     */
    private static class CardNumberGenerator {

        /** The length of the owner ID part in the generated card number. */
        private static final int OWNER_ID_LENGTH = 6;

        /** The length of the sequence part in the generated card number. */
        private static final int SEQUENCE_LENGTH = 3;

        /** The maximum allowed sequence number for a card number. */
        private static final int MAX_SEQUENCE = 999;

        /**
         * Generates a new card number for a given card type, owner ID, and the last issued card number.
         *
         * <p>The generated card number consists of a prefix based on the card type, followed by a formatted owner ID,
         * a sequence number, and a Luhn check digit at the end.
         *
         * @param type The type of card for which the number is being generated (e.g., VISA, MasterCard).
         * @param ownerId The unique ID of the card owner.
         * @param lastNumber The last issued card number for the owner (if available) to continue the sequence.
         *
         * @return The generated card number, which is a valid number conforming to the Luhn algorithm and card type rules.
         * @throws IllegalStateException If the maximum sequence is reached for the owner or if any error occurs in generating the card number.
         */
        public static String generate(@NotNull CardType type, @NotNull Long ownerId, @NotNull String lastNumber) {
            String prefix = type.getPrefix();
            int totalLength = type.getLength();

            String base = buildBaseNumber(prefix, ownerId, lastNumber, totalLength);

            return base + calculateLuhnCheckDigit(base);
        }

        /**
         * Builds the base card number, excluding the Luhn check digit, based on the given parameters.
         *
         * @param prefix The prefix associated with the card type.
         * @param ownerId The unique ID of the card owner.
         * @param lastNumber The last issued card number for the owner to continue the sequence.
         * @param totalLength The expected total length of the card number (including the check digit).
         *
         * @return The base card number, which includes the prefix, owner ID, and sequence number, but excludes the check digit.
         * @throws IllegalStateException If the sequence exceeds the maximum allowed or if the base number length is invalid.
         */
        private static String buildBaseNumber(String prefix, Long ownerId, String lastNumber, int totalLength) {
            String ownerIdPart = formatOwnerId(ownerId);
            int sequence = 0;

            if (lastNumber != null && lastNumber.startsWith(prefix)) {
                sequence = extractSequence(lastNumber, prefix, ownerIdPart) + 1;
            }

            if (sequence > MAX_SEQUENCE) {
                throw new IllegalStateException("Max card sequence reached for owner: " + ownerId);
            }

            String sequencePart = String.format("%0" + SEQUENCE_LENGTH + "d", sequence);
            String base = prefix + ownerIdPart + sequencePart;

            validateLength(base, totalLength);
            return base;
        }

        /**
         * Formats the owner ID to ensure it is always 6 digits long.
         *
         * @param ownerId The unique ID of the card owner.
         * @return A string representation of the owner ID, formatted to 6 digits.
         */
        private static String formatOwnerId(Long ownerId) {
            String idString = ownerId.toString();
            String trimmed = idString.substring(Math.max(0, idString.length() - OWNER_ID_LENGTH));
            return String.format("%0" + OWNER_ID_LENGTH + "d", Long.parseLong(trimmed));
        }

        /**
         * Extracts the sequence part from the last issued card number to determine the next sequence number.
         *
         * @param lastNumber The last issued card number.
         * @param prefix The prefix associated with the card type.
         * @param ownerIdPart The formatted owner ID.
         * @return The extracted sequence number, or 0 if the sequence cannot be extracted.
         */
        private static int extractSequence(String lastNumber, String prefix, String ownerIdPart) {
            try {
                int ownerIdStart = prefix.length();
                int ownerIdEnd = ownerIdStart + OWNER_ID_LENGTH;

                if (!lastNumber.substring(ownerIdStart, ownerIdEnd).equals(ownerIdPart)) {
                    return 0;
                }

                String sequencePart = lastNumber.substring(ownerIdEnd, ownerIdEnd + SEQUENCE_LENGTH);
                return Integer.parseInt(sequencePart);
            } catch (Exception e) {
                log.warn("Error extracting sequence from: {}", lastNumber);
                return 0;
            }
        }

        /**
         * Validates that the length of the base card number is correct according to the specified total length.
         *
         * @param base The base card number to validate.
         * @param totalLength The total length the card number should have, including the check digit.
         * @throws IllegalStateException If the base number length does not match the expected total length minus 1.
         */
        private static void validateLength(String base, int totalLength) {
            if (base.length() != totalLength - 1) {
                throw new IllegalStateException(
                        "Invalid base length: " + base.length() +
                                " for required total: " + totalLength
                );
            }
        }

        /**
         * Calculates the Luhn check digit for a given card number, excluding the check digit itself.
         *
         * @param numberWithoutCheckDigit The card number excluding the check digit.
         * @return The calculated Luhn check digit.
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
