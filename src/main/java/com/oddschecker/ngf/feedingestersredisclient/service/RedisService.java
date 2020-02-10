package com.oddschecker.ngf.feedingestersredisclient.service;

import com.oddschecker.ngf.feedingestersredisclient.model.RedisResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {

    RedisResponse<List<String>> getRedisKeysByPattern(Map<String, String> keyPatternMap) throws Exception;

    RedisResponse<String> deleteRedisKeysByPattern(Map<String, String> keyPattern) throws Exception ;

    RedisResponse getRedisKeyPost(final Map<String, String> keyPatternMap);

    RedisResponse getRedisKey(String key) throws Exception ;

    RedisResponse<String> deleteRedisKey(String key) throws Exception;

    String getRedisInfo(String section) throws Exception ;

    RedisResponse<Set<String>> getRedisKeyDiff(Set<String> keys);
}
