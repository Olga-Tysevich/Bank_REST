package com.example.bankcards.util;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * Utility class responsible for loading environment variables from a .env file
 * and setting them as system properties.
 * <p>
 * This loader dynamically sets the host and port for database and Redis connections
 * based on whether the application is running inside a Docker container or locally.
 * <p>
 * Expected environment variables:
 * <ul>
 *     <li>IS_DOCKERIZED=true/false</li>
 *     <li>BANK_REST_DB_HOST_LOCAL - host used when running outside Docker</li>
 *     <li>BANK_REST_DB_HOST_DOCKER - host used when running inside Docker</li>
 *     <li>BANK_REST_DB_PORT - internal DB port (used in Docker)</li>
 *     <li>BANK_REST_DB_EPORT - external DB port (used from host machine)</li>
 *     <li>REDIS_HOST_LOCAL - host used for Redis when running outside Docker</li>
 *     <li>REDIS_HOST_DOCKER - host used for Redis when running inside Docker</li>
 *     <li>REDIS_PORT - internal Redis port (used in Docker)</li>
 *     <li>REDIS_PORT_EPORT - external Redis port (used from host machine)</li>
 * </ul>
 * <p>
 * This class should be invoked at the very beginning of the application lifecycle,
 * before any Spring context is initialized.
 */
@UtilityClass
public class DotenvLoader {
    public static void load() {
        Dotenv dotenv = Dotenv.load();

        String isDockerized = Objects.requireNonNull(dotenv.get("IS_DOCKERIZED"));
        System.setProperty("IS_DOCKERIZED", isDockerized);

        String dbHost;
        String dbPort;
        String redisHost;
        String redisPort;

        // DB and Redis conf
        if ("true".equalsIgnoreCase(isDockerized)) {
            dbHost = Objects.requireNonNull(dotenv.get("BANK_REST_DB_HOST"));
            dbPort = Objects.requireNonNull(dotenv.get("BANK_REST_DB_PORT"));

            redisHost = Objects.requireNonNull(dotenv.get("REDIS_HOST"));
            redisPort = Objects.requireNonNull(dotenv.get("REDIS_PORT"));
        } else {
            dbHost = Objects.requireNonNull(dotenv.get("BANK_REST_APPLICATION_HOST"));
            dbPort = Objects.requireNonNull(dotenv.get("BANK_REST_DB_EPORT"));

            redisHost = Objects.requireNonNull(dotenv.get("BANK_REST_APPLICATION_HOST"));
            redisPort = Objects.requireNonNull(dotenv.get("REDIS_PORT_EPORT"));
        }

        //DB
        System.setProperty("BANK_REST_APPLICATION_HOST", dbHost);
        System.setProperty("BANK_REST_DB_PORT", dbPort);
        System.setProperty("BANK_REST_DB_NAME", Objects.requireNonNull(dotenv.get("BANK_REST_DB_NAME")));
        System.setProperty("BANK_REST_DB_USER", Objects.requireNonNull(dotenv.get("BANK_REST_DB_USER")));
        System.setProperty("BANK_REST_DB_PASSWORD", Objects.requireNonNull(dotenv.get("BANK_REST_DB_PASSWORD")));

        //Redis
        System.setProperty("REDIS_HOST", redisHost);
        System.setProperty("REDIS_PORT", redisPort);
        System.setProperty("REDIS_PASSWORD", Objects.requireNonNull(dotenv.get("REDIS_PASSWORD")));

        //JWT
        System.setProperty("JWT_ACCESS_KEY_SECRET", Objects.requireNonNull(dotenv.get("JWT_ACCESS_KEY_SECRET")));
        System.setProperty("JWT_ACCESS_KEY_EXPIRATION_TIME", Objects.requireNonNull(dotenv.get("JWT_ACCESS_KEY_EXPIRATION_TIME")));
        System.setProperty("JWT_REFRESH_KEY_SECRET", Objects.requireNonNull(dotenv.get("JWT_REFRESH_KEY_SECRET")));
        System.setProperty("JWT_REFRESH_KEY_EXPIRATION_TIME", Objects.requireNonNull(dotenv.get("JWT_REFRESH_KEY_EXPIRATION_TIME")));

        //Application
        System.setProperty("MIN_CARD_BALANCE", Objects.requireNonNull(dotenv.get("MIN_CARD_BALANCE")));

        System.setProperty("BANK_REST_WEB_ALLOWED_SOURCES", Objects.requireNonNull(dotenv.get("BANK_REST_WEB_ALLOWED_SOURCES")));
        System.setProperty("BANK_REST_WEB_ALLOWED_METHODS", Objects.requireNonNull(dotenv.get("BANK_REST_WEB_ALLOWED_METHODS")));
        System.setProperty("BANK_REST_WEB_ALLOWED_HEADERS", Objects.requireNonNull(dotenv.get("BANK_REST_WEB_ALLOWED_HEADERS")));
        System.setProperty("BANK_REST_WEB_IGNORED_URLS", Objects.requireNonNull(dotenv.get("BANK_REST_WEB_IGNORED_URLS")));
    }
}
