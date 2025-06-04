package com.example.bankcards.dto.api.req.filters;

/**
 * Marker interface that marks a class as a filter for search.
 */
public interface SearchFilter {

    static <T extends SearchFilter> T cartToConcreteFilter(SearchFilter filter, Class<T> tClass) {
        if (filter.getClass().equals(tClass)) {
            return tClass.cast(filter);
        }

        throw new IllegalArgumentException("Filter is not of type " + tClass.getName());
    }

}
