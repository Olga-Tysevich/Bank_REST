package com.example.bankcards.service;

import com.example.bankcards.dto.api.req.MoneyTransferReqDTO;
import com.example.bankcards.dto.redis.TransferMessageDTO;
import org.jetbrains.annotations.NotNull;

public interface TransferService {

    void makeTransfer(@NotNull TransferMessageDTO transferDTO);

    Long createTransferRequest(@NotNull MoneyTransferReqDTO moneyTransferReq);

    void cancelPendingTransfers();

}
