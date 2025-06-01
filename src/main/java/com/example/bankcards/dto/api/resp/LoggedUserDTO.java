package com.example.bankcards.dto.api.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.bankcards.util.Constants.TOKEN_TYPE;


/**
 * A data transfer object (DTO) class representing a logged-in user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoggedUserDTO {

    @Builder.Default
    private String type = TOKEN_TYPE;

    private String accessToken;

    private String refreshToken;

    private Long userId;

}