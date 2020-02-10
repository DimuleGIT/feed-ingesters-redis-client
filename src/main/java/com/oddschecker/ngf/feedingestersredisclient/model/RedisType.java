package com.oddschecker.ngf.feedingestersredisclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redis.clients.jedis.Jedis;

@AllArgsConstructor
@Getter
public enum RedisType {

    STRING("string") {
        public String getRedisValue(Jedis jedis, String key) {
            return jedis.get(key);
        }
    },
    LIST("list") {
        public String getRedisValue(Jedis jedis, String key) {
            return jedis.lrange(key, 0, 10).toString();
        }
    },
    SET("set") {
        public String getRedisValue(Jedis jedis, String key) {
            return jedis.smembers(key).toString();
        }
    };

    private String name;

    public abstract String getRedisValue(Jedis jedis, String key);
}
