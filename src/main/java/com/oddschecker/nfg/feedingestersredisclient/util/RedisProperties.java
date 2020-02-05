package com.oddschecker.nfg.feedingestersredisclient.util;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import static lombok.AccessLevel.PRIVATE;


@Getter
@Component
@Validated
@FieldDefaults(level = PRIVATE)
public class RedisProperties {

	@Value("${redis.host}")
	@NotBlank String redisHost;
	@Value("${redis.port}")
	@NonNull @Min(0) @Max(65535) int redisPort;
	@Value("${redis.maxTotal}")
	@NonNull @Positive int maxTotal;
	@Value("${redis.maxIdle}")
	@NonNull @Positive int maxIdle;
	@Value("${redis.minIdle}")
	@NonNull @Positive int minIdle;
	@Value("${redis.minEvictableIdleTimeMillis}")
	@NonNull @Positive int minEvictableIdleTimeMillis;
	@Value("${redis.timeBetweenEvictionRunsMillis}")
	@NonNull @Positive int timeBetweenEvictionRunsMillis;
	@Value("${redis.numTestsPerEvictionRun}")
	@NonNull @Positive int numTestsPerEvictionRun;
	@Value("${redis.blockWhenExhausted}")
	@NonNull boolean blockWhenExhausted;

}
