package com.oddschecker.ngf.feedingestersredisclient.service.impl;

import com.oddschecker.ngf.feedingestersredisclient.model.RedisResponse;
import com.oddschecker.ngf.feedingestersredisclient.model.RedisType;
import com.oddschecker.ngf.feedingestersredisclient.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl implements RedisService {

    public static final String PATTERN = "pattern";
    private static final String KEY_DELETED_SUCCESSFULLY = "Key %s deleted successfully";
    private static final String KEYS_BY_PATTERN_DELETED_SUCCESSFULLY = "Keys by pattern %s deleted successfully";
    private static final String ALL = "ALL";
    public static final String KEY = "key";

    private JedisPool jedisPool;

    @Autowired
    public RedisServiceImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    @Retryable(value = JedisConnectionException.class, backoff = @Backoff(delay = 1000))
    public RedisResponse<List<String>> getRedisKeysByPattern(final Map<String, String> keyPatternMap) throws Exception {

        try (final Jedis jedis = jedisPool.getResource()) {

            final List<String> keys = jedis.keys(keyPatternMap.get(PATTERN))
                    .stream()
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toList());

            return RedisResponse.<List<String>>builder()
                    .size(keys.size())
                    .value(keys)
                    .build();
        }
    }

    @Override
    @Retryable(value = JedisConnectionException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public RedisResponse getRedisKeyPost(final Map<String, String> keyPatternMap) {

        final String key = keyPatternMap.get(KEY);
        try (final Jedis jedis = jedisPool.getResource()) {

            return getRedisKey(key);
        }
    }

    @Override
    @Retryable(value = JedisConnectionException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public RedisResponse getRedisKey(final String key) {
        try (final Jedis jedis = jedisPool.getResource()) {

            return Optional.ofNullable(jedis)
                    .filter(jd -> jedis.exists(key))
                    .map(jd -> buildRedisResponse(key, jedis))
                    .orElse(null);
        }
    }

    private RedisResponse buildRedisResponse(String key, Jedis jedis) {

        final RedisType redisType = RedisType.valueOf(jedis.type(key).toUpperCase());

        final Long ttl = jedis.ttl(key);

        return RedisResponse.builder()
                .ttl(ttl)
                .ttlDateTill(LocalDateTime.now().plusSeconds(ttl))
                .type(redisType.getName())
                .value(redisType.getRedisValue(jedis, key))
                .size(redisType == RedisType.SET ? jedis.smembers(key).size() : null)
                .build();
    }

    @Override
    @Retryable(value = JedisConnectionException.class, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    public RedisResponse<String> deleteRedisKey(final String key) {
        try (final Jedis jedis = jedisPool.getResource()) {

            return Optional.ofNullable(jedis)
                    .filter(jed -> jedis.exists(key))
                    .map(jed -> jed.del(key))
                    .map(delLong -> RedisResponse.<String>builder()
                            .message(String.format(KEY_DELETED_SUCCESSFULLY, key))
                            .build())
                    .orElse(null);
        }

    }

    @Override
    @Retryable(value = JedisConnectionException.class, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    public RedisResponse<String> deleteRedisKeysByPattern(final Map<String, String> keyPatternMap) {
        try (final Jedis jedis = jedisPool.getResource()) {

            final String pattern = keyPatternMap.get(PATTERN);
            final Set<String> keys = jedis.keys(pattern);

            if (keys.size() > 0) {
                jedis.del(keys.toArray(new String[0]));
                return RedisResponse.<String>builder()
                        .message(String.format(KEYS_BY_PATTERN_DELETED_SUCCESSFULLY, pattern))
                        .size(keys.size())
                        .build();

            }

        }
        return null;

    }

    @Override
    @Retryable(value = JedisConnectionException.class, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    public String getRedisInfo(final String section) {
        try (final Jedis jedis = jedisPool.getResource()) {
            return Optional.ofNullable(section)
                    .filter(sec -> !sec.equalsIgnoreCase(ALL))
                    .map(jedis::info)
                    .orElseGet(jedis::info);
        }
    }

    @Override
    public RedisResponse<Set<String>> getRedisKeyDiff(Set<String> keys) {
        try (final Jedis jedis = jedisPool.getResource()) {

            final Set<String> sdiff = jedis.sdiff(keys.toArray(new String[]{}));
            return Optional.ofNullable(sdiff)
                    .map(sd -> RedisResponse.<Set<String>>builder()
                            .size(sd.size())
                            .value(sd)
                            .build())
                    .orElse(null);
        }
    }
}
