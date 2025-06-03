package com.example.bankcards.dto.api.req;


import com.example.bankcards.dto.api.req.filters.SearchFilter;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class that describes the query data for searching entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchReq<F extends SearchFilter> {

    @Positive(message = "Page number must be positive!")
    private int pageNumber;

    @Positive(message = "Page size must be positive!")
    private int pageSize;

    @Builder.Default
    private boolean searchWithoutFilter = false;

    /**
     * @see SearchFilter
     */
    private F filter;

}
