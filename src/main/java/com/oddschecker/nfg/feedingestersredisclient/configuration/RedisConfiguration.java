package com.oddschecker.nfg.feedingestersredisclient.configuration;

import com.oddschecker.nfg.feedingestersredisclient.util.RedisProperties;
import com.oddschecker.nfg.feedingestersredisclient.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfiguration {

    private RedisProperties redisProperties;

    @Autowired
    public RedisConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JedisPool jedisPool() {

        return RedisUtil.getJedisPool(redisProperties);

    }
}
