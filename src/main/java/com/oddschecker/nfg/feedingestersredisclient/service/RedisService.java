package com.oddschecker.nfg.feedingestersredisclient.service;

import com.oddschecker.nfg.feedingestersredisclient.model.RedisResponse;

import java.util.Map;
import java.util.Set;

public interface RedisService {

    RedisResponse<Set<String>> getRedisKeysByPattern(Map<String, String> keyPatternMap) throws Exception;

    RedisResponse getRedisKey(String key) throws Exception ;

    RedisResponse<String> deleteRedisKey(String key) throws Exception ;

    RedisResponse<String> deleteRedisKeysByPattern(Map<String, String> keyPattern) throws Exception ;

    String getRedisInfo(String section) throws Exception ;
}
