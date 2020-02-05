package com.oddschecker.nfg.feedingestersredisclient.controller;


import com.oddschecker.nfg.feedingestersredisclient.exception.ResourceNotFoundException;
import com.oddschecker.nfg.feedingestersredisclient.model.RedisResponse;
import com.oddschecker.nfg.feedingestersredisclient.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("v1/redis")
public class JedisController {

    private RedisService redisService;

    @Autowired
    public JedisController(RedisService redisSevice) {
        this.redisService = redisSevice;
    }

    @GetMapping("/info/{section}")
    public ResponseEntity<String> getRedisInfo(@PathVariable String section) throws Exception {

        return ResponseEntity.ok(redisService.getRedisInfo(section));
    }

    @PostMapping("/keys")
    public ResponseEntity<RedisResponse<Set<String>>> getRedisValuesByKeys(@RequestBody Map<String, String> keyPatternMap)
            throws Exception {
        return ResponseEntity.ok(redisService.getRedisKeysByPattern(keyPatternMap));
    }

    @GetMapping("/{key}")
    public ResponseEntity<RedisResponse> getRedisKey(@PathVariable String key) throws Exception {
        return ResponseEntity.ok(redisService.getRedisKey(key));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<RedisResponse<String>> deleteRedisKey(@PathVariable String key)
            throws Exception {

        return Optional.ofNullable(redisService.deleteRedisKey(key))
                .map(delLong -> ResponseEntity.ok().body(delLong))
                .orElseThrow(() -> new ResourceNotFoundException(key));
    }

    @DeleteMapping("/keys")
    public ResponseEntity<RedisResponse<String>> deleteRedisKeysByPattern(@RequestBody Map<String, String> keyPatternMap)
            throws Exception {
        return Optional.ofNullable(redisService.deleteRedisKeysByPattern(keyPatternMap))
                .map(delLong -> ResponseEntity.ok().body(delLong))
                .orElseThrow(() -> new ResourceNotFoundException(keyPatternMap.get("pattern")));
    }


}
