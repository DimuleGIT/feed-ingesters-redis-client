package com.oddschecker.nfg.feedingestersredisclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
public class FeedIngestersRedisClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedIngestersRedisClientApplication.class, args);
	}

}
