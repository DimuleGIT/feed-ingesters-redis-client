package com.oddschecker.ngf.feedingestersredisclient.configuration;

import com.oddschecker.ngf.feedingestersredisclient.util.RedisProperties;
import com.oddschecker.ngf.feedingestersredisclient.util.RedisUtil;
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
