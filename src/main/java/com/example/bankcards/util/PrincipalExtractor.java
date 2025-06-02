package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

@UtilityClass
public class PrincipalExtractor {

    public static User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        return null;
    }

    public static Long getCurrentUserId() {
        User user = getCurrentUser();

        if (Objects.isNull(user)) {
            return null;
        }

        return user.getId();
    }

}