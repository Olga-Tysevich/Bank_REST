package com.example.bankcards.dto.mappers;

import com.example.bankcards.dto.redis.TransferMessageDTO;
import com.example.bankcards.entity.Transfer;
import org.mapstruct.*;

/**
 * Mapper interface responsible for mapping between {@link Transfer} entity and {@link TransferMessageDTO} DTO.
 * This interface is used by MapStruct to generate the implementation for object-to-object mappings.
 * The generated implementation will convert a {@link Transfer} entity into a {@link TransferMessageDTO} DTO
 * and vice versa, while handling the conversion of nested objects and their corresponding fields.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TransferMapper {
    /**
     * Maps a {@link Transfer} entity to a {@link TransferMessageDTO} DTO.
     *
     * @param transfer the {@link Transfer} entity to be mapped to the DTO
     * @return a {@link TransferMessageDTO} DTO corresponding to the {@link Transfer} entity
     * @throws NullPointerException if any required fields in the source {@link Transfer} are null
     */
    @Mapping(source = "fromCard.id", target = "fromCardId")
    @Mapping(source = "toCard.id", target = "toCardId")
    TransferMessageDTO toTransferMessageDTO(Transfer transfer);

    /**
     * Maps a {@link TransferMessageDTO} DTO to a {@link Transfer} entity.
     *
     * @param transferDTO the {@link TransferMessageDTO} dto to be mapped to the entity
     * @return a {@link Transfer} entity  corresponding to the {@link TransferMessageDTO} DTO
     * @throws NullPointerException if any required fields in the source {@link TransferMessageDTO} are null
     */
    @Mappings({
            @Mapping(target = "fromCard", ignore = true),
            @Mapping(target = "toCard", ignore = true),
    })
    Transfer toEntity(TransferMessageDTO transferDTO);

}
