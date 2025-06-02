package com.example.bankcards.entity;

import com.example.bankcards.dto.mappers.SenderDataConverter;
import com.example.bankcards.entity.enums.SourceOfFunds;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

import static com.example.bankcards.util.Constants.*;

/**
 * This class represents an Account for receiving funds if the card is missing or blocked.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "backup_accounts")
public class BackupAccount {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "backup_accountIdSeq")
    @SequenceGenerator(name = "backup_accountIdSeq", sequenceName = "backup_account_id_seq", allocationSize = 1)
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_of_funds")
    private SourceOfFunds sourceOfFunds;

    @NotEmpty(message = SENDER_DATA_CANNOT_BE_NULL_OR_EMPTY)
    @Convert(converter = SenderDataConverter.class)
    @Column(name = "sender_data", columnDefinition = "jsonb")
    private Map<SourceOfFunds.SenderDataKey, String> senderData;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull(message = USER_CANNOT_BE_NULL)
    private User owner;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    @NotNull(message = TRANSFER_AMOUNT_CANNOT_BE_NUL)
    @DecimalMin(value = "0.00", message = TRANSFER_AMOUNT_BE_POSITIVE)
    private BigDecimal amount;

}

