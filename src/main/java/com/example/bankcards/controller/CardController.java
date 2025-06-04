package com.example.bankcards.controller;

import com.example.bankcards.dto.api.req.AddCardDTO;
import com.example.bankcards.dto.api.req.notifications.CardBlockRequestNotificationDTO;
import com.example.bankcards.dto.api.req.SearchReq;
import com.example.bankcards.dto.api.req.UpdateCardDTO;
import com.example.bankcards.dto.api.req.filters.CardSearchFilter;
import com.example.bankcards.dto.api.resp.CardDTO;
import com.example.bankcards.dto.api.resp.PageResp;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.bankcards.util.Constants.ID_CANNOT_BE_NULL;
import static com.example.bankcards.util.Constants.ID_MUST_BE_POSITIVE;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/card")
@Validated
public class CardController {
    private final CardService cardService;
    private final NotificationService notificationService;

    @Operation(
            summary = "Add a new card",
            description = "Creates a new card for the specified user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card creation request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AddCardDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Card successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addCard(@RequestBody @Valid AddCardDTO req) {
        Long accountId = cardService.createCard(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "userId", req.getOwnerId(),
                        "accountId", accountId
                )
        );
    }

    @Operation(
            summary = "Update card",
            description = "Updates details of an existing card",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card update request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCardDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card successfully updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/update")
    public ResponseEntity<?> updateCard(@RequestBody @Valid UpdateCardDTO req) {
        Long cardId = cardService.updateCard(req);
        return ResponseEntity.ok().body(
                Map.of(
                        "updated", true,
                        "cardId", cardId
                )
        );
    }

    @Operation(
            summary = "Set card status",
            description = "Changes the status of a card (e.g., ACTIVE, BLOCKED)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card status successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/{id}/{status}/update")
    public ResponseEntity<?> setCardStatus(
            @Parameter(description = "Card ID", example = "1")
            @PathVariable @Min(1) Long id,
            @Parameter(description = "Card status")
            @PathVariable CardStatus status
    ) {
        Long cardId = cardService.setCardStatus(id, status);
        return ResponseEntity.ok().body(
                Map.of(
                        "updated", true,
                        "cardId", cardId
                )
        );
    }

    @Operation(
            summary = "Delete card",
            description = "Deletes a card by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/{id}/delete")
    public ResponseEntity<?> deleteProject(
            @Parameter(description = "Card ID", example = "1")
            @PathVariable
            @NotNull(message = ID_CANNOT_BE_NULL)
            @Min(value = 1, message = ID_MUST_BE_POSITIVE)
            Long id) {
        Long cardId = cardService.deleteCard(id);
        return ResponseEntity.ok().body(
                Map.of(
                        "deleted", true,
                        "cardId", cardId
                )
        );
    }

    @Operation(
            summary = "Get card by ID",
            description = "Retrieves details of a card by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card found",
                            content = @Content(schema = @Schema(implementation = CardDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<CardDTO> getCardById(
            @Parameter(description = "Card ID", example = "1")
            @PathVariable
            @NotNull(message = ID_CANNOT_BE_NULL)
            @Min(value = 1, message = ID_MUST_BE_POSITIVE)
            Long id) {
        CardDTO card = cardService.getCard(id);
        return ResponseEntity.ok(card);
    }

    @Operation(
            summary = "Get cards with filters",
            description = "Returns a paginated list of cards filtered by various criteria",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Search request with filter conditions",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SearchReq.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered card list returned successfully")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/get")
    public ResponseEntity<PageResp<CardDTO>> getCards(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search request with filters",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SearchReq.class)))
            @RequestBody @Valid SearchReq<CardSearchFilter> req) {
        PageResp<CardDTO> cardPage = cardService.getCards(req);
        return ResponseEntity.ok(cardPage);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/block")
    public ResponseEntity<?> createCardBlockRequest(@RequestBody @Valid CardBlockRequestNotificationDTO req) {

        Long cardId = notificationService.createNotification(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "cardId", req.getCardId(),
                        "CardBlockRequestId:", cardId
                )
        );
    }

}