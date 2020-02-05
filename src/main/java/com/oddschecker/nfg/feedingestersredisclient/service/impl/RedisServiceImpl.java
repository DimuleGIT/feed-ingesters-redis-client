package com.oddschecker.nfg.feedingestersredisclient.service.impl;

import com.oddschecker.nfg.feedingestersredisclient.model.RedisResponse;
import com.oddschecker.nfg.feedingestersredisclient.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
public class RedisServiceImpl implements RedisService {

    private static final String PATTERN = "pattern";
    private static final String KEY_DELETED_SUCCESSFULLY = "Key %s deleted successfully";
    private static final String KEYS_BY_PATTERN_DELETED_SUCCESSFULLY = "Keys by pattern %s deleted successfully";
    private static final String ALL = "ALL";

    private JedisPool jedisPool;

    @Autowired
    public RedisServiceImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    @Retryable(value = JedisConnectionException.class, backoff = @Backoff(delay = 1000))
    public RedisResponse<Set<String>> getRedisKeysByPattern(final Map<String, String> keyPatternMap) throws Exception  {

            try (final Jedis jedis = jedisPool.getResource()) {

                Set<String> keys = jedis.keys(keyPatternMap.get(PATTERN));
                return RedisResponse.<Set<String>>builder()
                        .size(keys.size())
                        .value(keys)
                        .build();
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
        final String redisType = jedis.type(key);

        final Long ttl = jedis.ttl(key);

        return RedisResponse.builder()
                .ttl(ttl)
                .ttlDateTill(LocalDateTime.now().plusSeconds(ttl))
                .type(redisType)
                .value(getRedisValues(redisType, key).apply(jedis))
                .build();
    }


    private Function<Jedis, String> getRedisValues(String redisType, String key) {

        switch (redisType) {
            case "string":
                return jedis -> jedis.get(key);
            case "set":
                return jedis -> jedis.smembers(key).toString();
            case "list":
                return jedis -> jedis.lrange(key, 0, 1).toString();
            default:
                return jedis -> StringUtils.EMPTY;
        }
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

            String pattern = keyPatternMap.get(PATTERN);
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
}
