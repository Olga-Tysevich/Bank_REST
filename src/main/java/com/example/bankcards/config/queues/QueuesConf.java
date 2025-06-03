package com.example.bankcards.config.queues;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class for storing adjustable application queues parameters. Stores constants set externally
 */
@Component
@Getter
public class QueuesConf {

    @Value("${spring.queues.transfer.name:transferQueue}")
    private String transferQueueName;

    @Value("${spring.queues.confirmedTransfer.name:confirmedTransferQueueName}")
    private String confirmedTransferQueueName;

    @Value("${spring.queues.cardBlockRequestCreated.name:cardBlockRequestCreated}")
    private String cardBlockRequestCreated;

}
