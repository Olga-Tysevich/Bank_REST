package com.example.bankcards.util;

import com.example.bankcards.entity.*;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.RoleEnum;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The DataSeeder class is responsible for populating the database with test data. It creates users,
 * cards, and transfers between cards, including different transfer statuses (PENDING and CONFIRMED).
 * This class runs automatically on application startup if the database is empty.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class DataSeeder {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final RoleRepository roleRepository;

    private final Random random = new Random();

    /**
     * Populates the database with test data when the application starts. This method creates roles, users,
     * cards for users, and transfers between those cards.
     * <p>
     * If the database already contains users, this method will not make any changes.
     */
    @PostConstruct
    public void seedData() {


        if (userRepository.count() > 1) return;
        Role userRole = roleRepository.getByRole(RoleEnum.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().role(RoleEnum.ROLE_USER).build()));

        Role adminRole = roleRepository.getByRole(RoleEnum.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().role(RoleEnum.ROLE_ADMIN).build()));

        List<User> users = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String username = "user" + i;
            if (userRepository.findByUsername(username).isPresent()) continue;

            User user = User.builder()
                    .username(username)
                    .name("UserName" + i)
                    .surname("Surname" + i)
                    .dateOfBirth(LocalDate.of(1990, 1, 1).plusDays(i * 100))
                    .password("password" + i)
                    .roleSet(Set.of(userRole))
                    .build();
            users.add(userRepository.save(user));
        }

        for (int i = 1; i <= 2; i++) {
            String username = "admin" + i;
            if (userRepository.findByUsername(username).isPresent()) continue;

            User admin = User.builder()
                    .username(username)
                    .name("AdminName" + i)
                    .surname("AdminSurname" + i)
                    .dateOfBirth(LocalDate.of(1980, 1, 1).plusDays(i * 100))
                    .password("adminpassword" + i)
                    .roleSet(Set.of(adminRole))
                    .build();
            users.add(userRepository.save(admin));
        }

        List<Card> allCards = new ArrayList<>();
        for (User user : users) {
            int cardsCount = random.nextInt(6) + 1;
            for (int j = 0; j < cardsCount; j++) {
                Card card = Card.builder()
                        .number(generateCardNumber())
                        .expiration(LocalDate.now().plusYears(3))
                        .status(CardStatus.ACTIVE)
                        .balance(BigDecimal.valueOf(random.nextInt(50000) + 1000))
                        .hold(BigDecimal.ZERO)
                        .owner(user)
                        .build();
                allCards.add(cardRepository.save(card));
            }
        }

        for (int i = 0; i < 100; i++) {
            Card fromCard = allCards.get(random.nextInt(allCards.size()));
            Card toCard = allCards.get(random.nextInt(allCards.size()));
            if (fromCard.equals(toCard)) continue;

            BigDecimal amount = BigDecimal.valueOf(random.nextInt(5000) + 1);
            if (fromCard.getBalance().compareTo(amount) < 0) continue;

            fromCard.setBalance(fromCard.getBalance().subtract(amount));
            toCard.setBalance(toCard.getBalance().add(amount));
            cardRepository.save(fromCard);
            cardRepository.save(toCard);

            Transfer transfer = Transfer.builder()
                    .fromCard(fromCard)
                    .toCard(toCard)
                    .amount(amount)
                    .status(random.nextBoolean() ? TransferStatus.PENDING : TransferStatus.COMPLETED)
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(60)))
                    .build();
            transferRepository.save(transfer);
        }

        log.info("Test data generated.");
    }

    /**
     * Generates a random 16-digit card number. This is a simplified version and does not
     * include the Luhn algorithm for card validation.
     *
     * @return a random 16-digit card number.
     */
    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
