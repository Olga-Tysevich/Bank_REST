package com.example.bankcards.config.redis;

import com.example.bankcards.dto.redis.TransferMessage;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nullable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;

/**
 * RedisConfig is a configuration class that sets up Redis support for caching and message serialization in the application.
 * It configures the `RedisTemplate` and `RedisCacheManager` beans used to interact with Redis, with custom serialization
 * of {@link TransferMessage} objects to JSON format.
 * It also configures the cache manager with default serialization settings.
 */
@EnableCaching
@Configuration
public class RedisConfig {

    /**
     * Configures a RedisTemplate for working with Redis and serializing {@link TransferMessage} objects.
     * This template is used to interact with Redis and serialize the objects as JSON using a custom ObjectMapper.
     *
     * @param redisConnectionFactory the factory used to create connections to the Redis server.
     * @return a configured RedisTemplate with custom serializers.
     */
    @Bean
    public RedisTemplate<String, TransferMessage> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, TransferMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper) {
                    @Override
                    public Object deserialize(@Nullable byte[] bytes) throws SerializationException {
                        if (Objects.isNull(bytes) || bytes.length == 0) {
                            return null;
                        }
                        try {
                            return objectMapper.readValue(bytes, TransferMessage.class);
                        } catch (Exception ex) {
                            throw new SerializationException("Could not read JSON", ex);
                        }
                    }
                };

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
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
                .serializeValuesWith(jsonSerializer);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
