package com.example.bankcards.config.redis;

import com.example.bankcards.dto.redis.CardBlockRequestCreatedMessageDTO;
import com.example.bankcards.dto.redis.TransferMessageDTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

/**
 * RedisConfig is a configuration class that sets up Redis support for caching and message serialization in the application.
 * It configures the `RedisTemplate` and `RedisCacheManager` beans used to interact with Redis, with custom serialization
 * of objects to JSON format.
 * It also configures the cache manager with default serialization settings.
 */
@EnableCaching
@Configuration
public class RedisConfig {

    /**
     * Creates a {@link RedisTemplate} configured to serialize keys as Strings and values using
     * Jackson with custom settings to support polymorphic types and Java time module support.
     *
     * @param connectionFactory the Redis connection factory
     * @param clazz             the class type of the value to be stored in Redis
     * @param <T>               the type of the value
     * @return a configured RedisTemplate for the specified type
     */
    private <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory connectionFactory, Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * Creates a RedisTemplate bean for handling {@link TransferMessageDTO} objects.
     *
     * @param connectionFactory the Redis connection factory
     * @return a RedisTemplate configured for TransferMessageDTO
     */
    @Bean
    public RedisTemplate<String, TransferMessageDTO> transferMessageRedisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, TransferMessageDTO.class);
    }

    /**
     * Creates a RedisTemplate bean for handling {@link CardBlockRequestCreatedMessageDTO} objects.
     *
     * @param connectionFactory the Redis connection factory
     * @return a RedisTemplate configured for CardBlockRequestCreatedMessageDTO
     */
    @Bean
    public RedisTemplate<String, CardBlockRequestCreatedMessageDTO> cardBlockRedisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, CardBlockRequestCreatedMessageDTO.class);
    }


    /**
     * Configures a RedisCacheManager for managing Redis caches with custom serialization settings.
     * This cache manager is used for caching operations and ensures that the values are serialized to JSON format
     * with proper type handling.
     *
     * @param redisConnectionFactory the factory used to create connections to the Redis server.
     * @return a configured RedisCacheManager.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        RedisSerializationContext.SerializationPair<Object> jsonSerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(jsonSerializer)
                .entryTtl(Duration.ofHours(2));

        RedisCacheConfiguration cardsBySearchConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(jsonSerializer)
                .entryTtl(Duration.ofHours(1));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("cardsBySearch", cardsBySearchConfig)
                .build();
    }
}
