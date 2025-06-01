package com.example.bankcards.dto.mappers;

import com.example.bankcards.dto.redis.TransferMessage;
import com.example.bankcards.entity.Transfer;
import org.mapstruct.*;
/**
 * Mapper interface responsible for mapping between {@link Transfer} entity and {@link TransferMessage} DTO.
 * This interface is used by MapStruct to generate the implementation for object-to-object mappings.
 * The generated implementation will convert a {@link Transfer} entity into a {@link TransferMessage} DTO
 * and vice versa, while handling the conversion of nested objects and their corresponding fields.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TransferMapper {
    /**
     * Maps a {@link Transfer} entity to a {@link TransferMessage} DTO.
     *
     * @param transfer the {@link Transfer} entity to be mapped to the DTO
     * @return a {@link TransferMessage} DTO corresponding to the {@link Transfer} entity
     * @throws NullPointerException if any required fields in the source {@link Transfer} are null
     */
    @Mapping(source = "fromCard.id", target = "fromCardId")
    @Mapping(source = "toCard.id", target = "toCardId")
    TransferMessage toTransferMessage(Transfer transfer);

}
