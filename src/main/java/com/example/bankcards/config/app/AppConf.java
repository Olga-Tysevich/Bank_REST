package com.example.bankcards.config.app;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class for storing adjustable application parameters. Stores constants set externally
 */
@Component
@Getter
public class AppConf {

    @Value("${spring.application.card.transfer.permissions.yourselfOnly:false}")
    boolean transferAllowedYourselfOnly;

    @Value("${spring.application.card.balance.min:0.01}")
    private String minBalanceStr;

    @Value("${spring.application.card.expiration.years:3}")
    private int cardExpirationYears;

}
