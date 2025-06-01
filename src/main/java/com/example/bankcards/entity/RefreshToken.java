package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.example.bankcards.util.Constants.USERNAME_CANNOT_BE_NULL_OR_EMPTY;
import static com.example.bankcards.util.Constants.REFRESH_TOKEN_CANNOT_BE_NULL_OR_EMPTY;

/**
 * This class represents a RefreshToken entity with its attributes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refresh_tokens")
@Entity
public class RefreshToken implements Serializable {

    @Id
    @Column(nullable = false)
    @NotBlank(message = USERNAME_CANNOT_BE_NULL_OR_EMPTY)
    private String username;

    @Column(name = "refresh_token", unique = true, nullable = false)
    @NotBlank(message = REFRESH_TOKEN_CANNOT_BE_NULL_OR_EMPTY)
    private String refreshToken;
}