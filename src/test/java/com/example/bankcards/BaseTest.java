package com.example.bankcards;


import com.example.bankcards.util.DotenvLoader;
import com.example.bankcards.utils.PostgresSQL;
import com.redis.testcontainers.RedisContainer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.validation.constraints.NotBlank;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.DockerImageName;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class BaseTest {
    @Autowired
    private UserDetailsService userDetailsService;
    private static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("""
            redis:latest"""))
            .withExposedPorts(6379);


    @BeforeAll
    static void initContainers() {
        PostgresSQL.container.start();
        redisContainer.start();
    }

    @BeforeEach
    public void testSomethingUsingLettuce() {
        String redisURI = redisContainer.getRedisURI();
        RedisClient client = RedisClient.create(redisURI);
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            RedisCommands<String, String> commands = connection.sync();
            Assertions.assertEquals("PONG", commands.ping());
        }
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        DotenvLoader.load();
        System.setProperty("DOTENV_CONFIG_PATH", "src/test/resources/.env.test");

        //crypto
        registry.add("spring.application.security.crypto.type", () -> "AES");
        registry.add("spring.application.security.crypto.transformation", () -> "ECB/PKCS5Padding");
        registry.add("spring.application.security.crypto.key", () -> "MySecretKey12345");

        //DB
        registry.add("spring.datasource.url", PostgresSQL.container::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresSQL.container::getUsername);
        registry.add("spring.datasource.password", PostgresSQL.container::getPassword);

        //liquibase
        registry.add("spring.liquibase.user", PostgresSQL.container::getUsername);
        registry.add("spring.liquibase.password", PostgresSQL.container::getPassword);

        //REDIS
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("REDIS_HOST", redisContainer::getHost);
        registry.add("REDIS_PORT", () -> redisContainer.getMappedPort(6379));
    }

    protected void setAuthentication(@NotBlank String username, @NotBlank String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void test() {
    }

}