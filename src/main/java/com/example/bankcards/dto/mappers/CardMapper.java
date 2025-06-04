package com.example.bankcards.dto.mappers;

import com.example.bankcards.dto.api.resp.CardDTO;
import com.example.bankcards.dto.api.resp.PageResp;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardType;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper interface for converting between {@link Card} entities and different dto objects.
 * <p>
 * Utilizes MapStruct to handle the mapping logic, including custom logic for:
 * <ul>
 *     <li>Converting {@link CardType} to/from an integer code</li>
 *     <li>Masking the card number when mapping to DTO</li>
 *     <li>Creating {@link User} stubs from ownerId</li>
 * </ul>
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CardMapper {

    /**
     * Converts a {@link Card} entity to its corresponding {@link CardDTO}.
     *
     * @param card the entity to convert
     * @return the mapped DTO
     */
    @Mapping(source = "type", target = "cardTypeCode", qualifiedByName = "cardTypeToCode")
    @Mapping(source = "number", target = "numberMask", qualifiedByName = "maskCardNumber")
    @Mapping(source = "owner.id", target = "ownerId")
    CardDTO toDto(Card card);

    /**
     * Converts a {@link CardDTO} back to a {@link Card} entity.
     * <p>
     * Note: The original card number cannot be restored from a mask. The field "number" will be left unset.
     *
     * @param cardDTO the DTO to convert
     * @return the mapped entity
     */
    @InheritInverseConfiguration
    @Mapping(target = "type", source = "cardTypeCode", qualifiedByName = "codeToCardType")
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "ownerFromId")
    Card fromDto(CardDTO cardDTO);

    /**
     * Converts a {@link CardType} enum value to its numeric code.
     *
     * @param type the card type enum
     * @return the corresponding integer code
     */
    @Named("cardTypeToCode")
    static int cardTypeToCode(CardType type) {
        return type.getTypeCode();
    }

    /**
     * Converts a numeric code to the corresponding {@link CardType}.
     *
     * @param code the integer code of the card type
     * @return the matching {@link CardType}
     * @throws IllegalArgumentException if no matching type is found
     */
    @Named("codeToCardType")
    static CardType codeToCardType(int code) {
        for (CardType type : CardType.values()) {
            if (type.getTypeCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid cardTypeCode: " + code);
    }

    /**
     * Masks the card number, exposing only the last 4 digits and replacing the rest with asterisks.
     *
     * @param number the plain card number
     * @return the masked card number (e.g. "**** **** **** 1234")
     */
    @Named("maskCardNumber")
    static String maskCardNumber(String number) {
        return CardType.generateCardMask(number);
    }

    /**
     * Creates a stub {@link User} entity with only the ID field set.
     * <p>
     * Useful for setting foreign key relationships without loading full objects.
     *
     * @param id the user ID
     * @return a {@link User} with only the ID set, or null if ID is null
     */
    @Named("ownerFromId")
    static User ownerFromId(Long id) {
        if (id == null) return null;
        User user = new User();
        user.setId(id);
        return user;
    }

    List<CardDTO> toDTOList(List<Card> cards);

    default PageResp<CardDTO> toPageResp(Page<Card> page, boolean isAdmin) {
        Set<CardDTO> cardDTOs = page.getContent().stream()
                .map(this::toDto)
                .peek(c -> {
                    if (!isAdmin) {
                        c.setHold(BigDecimal.ZERO);
                    }
                })
                .collect(Collectors.toSet());

        return PageResp.<CardDTO>builder()
                .objects(cardDTOs)
                .page(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalObjects((int) page.getTotalElements())
                .build();
    }
}
