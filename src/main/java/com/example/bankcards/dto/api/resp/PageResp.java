package com.example.bankcards.dto.api.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


/**
 * A class that describes the page returned by a search query.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResp<T> {
    private Set<T> objects;
    private int page;
    private int totalPages;
    private int totalObjects;
}
