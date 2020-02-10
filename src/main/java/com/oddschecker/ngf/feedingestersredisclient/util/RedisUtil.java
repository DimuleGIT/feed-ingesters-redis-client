package com.oddschecker.ngf.feedingestersredisclient.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.validation.Valid;


@UtilityClass
public final class RedisUtil {

	public static JedisPool getJedisPool(final @NonNull @Valid RedisProperties properties) {

		final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

		jedisPoolConfig.setMaxTotal(properties.getMaxTotal());
		jedisPoolConfig.setMaxIdle(properties.getMaxIdle());
		jedisPoolConfig.setMinIdle(properties.getMinIdle());
		jedisPoolConfig.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
		jedisPoolConfig.setNumTestsPerEvictionRun(properties.getNumTestsPerEvictionRun());
		jedisPoolConfig.setBlockWhenExhausted(properties.isBlockWhenExhausted());


		return new JedisPool(jedisPoolConfig, properties.getRedisHost(), properties.getRedisPort());
	}
}
