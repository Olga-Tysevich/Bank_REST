package com.example.bankcards.dto.api.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import static com.example.bankcards.util.Constants.PASSWORD_CANNOT_BE_NULL_OR_EMPTY;
import static com.example.bankcards.util.Constants.USERNAME_CANNOT_BE_NULL_OR_EMPTY;


/**
 * Data transfer object representing user login information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginReqDTO {

    @NotBlank(message = USERNAME_CANNOT_BE_NULL_OR_EMPTY)
    private String username;

    @NotBlank(message = PASSWORD_CANNOT_BE_NULL_OR_EMPTY)
    private String password;

}