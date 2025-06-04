package com.example.bankcards.repository.impl.spec;

import com.example.bankcards.dto.api.req.filters.CardSearchFilter;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Card_;
import com.example.bankcards.entity.User_;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The class representing a specification for searching cards based on a provided filter.
 */
public class CardSpecification {

    /**
     * Creates a specification based on the provided filter.
     *
     * @param filter The filter used to search for cards.
     * @param userId if the search is being conducted for the admin, this parameter does not need to be passed
     * @return A Specification object representing the search criteria.
     */
    public static Specification<Card> search(CardSearchFilter filter, Long userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(filter)) {
                if (Objects.nonNull(filter.getIdSet()) && !filter.getIdSet().isEmpty()) {
                    predicates.add(root.get(Card_.ID).in(filter.getIdSet()));
                }

                if (Objects.nonNull(filter.getCardTypes()) && !filter.getCardTypes().isEmpty()) {
                    predicates.add(root.get(Card_.TYPE).in(filter.getCardTypes()));
                }

                if (Objects.nonNull(filter.getNumber()) && !filter.getNumber().isEmpty()) {
                    predicates.add(root.get(Card_.NUMBER).in(filter.getNumber()));
                }

                if (Objects.nonNull(filter.getExpirationFrom()) && Objects.nonNull(filter.getExpirationTo())
                        & (filter.getExpirationFrom().isBefore(filter.getExpirationTo())
                        || filter.getExpirationFrom().equals(filter.getExpirationTo()))) {

                    predicates.add(cb.between(root.get(Card_.EXPIRATION), filter.getExpirationFrom(), filter.getExpirationTo()));

                } else if (Objects.nonNull(filter.getExpirationFrom())) {
                    predicates.add(cb.equal(root.get(Card_.EXPIRATION), filter.getExpirationFrom()));
                }

                if (Objects.nonNull(filter.getStatus()) && !filter.getStatus().isEmpty()) {
                    predicates.add(root.get(Card_.STATUS).in(filter.getStatus()));
                }

                if (Objects.nonNull(filter.getBalanceFrom()) && Objects.nonNull(filter.getBalanceTo())
                        && filter.getBalanceFrom().compareTo(filter.getBalanceTo()) <= 0) {

                    predicates.add(cb.between(root.get(Card_.BALANCE), filter.getBalanceFrom(), filter.getBalanceTo()));

                } else if (Objects.nonNull(filter.getBalanceFrom())) {
                    predicates.add(cb.equal(root.get(Card_.BALANCE), filter.getBalanceFrom()));
                }

                if (Objects.nonNull(filter.getHoldFrom()) && Objects.nonNull(filter.getHoldTo())
                        && filter.getHoldFrom().compareTo(filter.getHoldTo()) <= 0) {

                    predicates.add(cb.between(root.get(Card_.BALANCE), filter.getHoldFrom(), filter.getHoldTo()));

                } else if (Objects.nonNull(filter.getHoldFrom())) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Card_.BALANCE), filter.getHoldFrom()));
                }

                if (Objects.nonNull(filter.getOwnerIdSet()) && !filter.getOwnerIdSet().isEmpty()) {
                    predicates.add(root.get(Card_.OWNER).get(User_.ID).in(filter.getOwnerIdSet()));
                }

                if (Objects.nonNull(filter.getIsDeleted())) {
                    predicates.add(cb.equal(root.get(Card_.IS_DELETED), filter.getIsDeleted()));
                }
            }

            if (Objects.nonNull(userId)) {
                predicates.add(cb.equal(root.get(Card_.OWNER).get(User_.ID), userId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}