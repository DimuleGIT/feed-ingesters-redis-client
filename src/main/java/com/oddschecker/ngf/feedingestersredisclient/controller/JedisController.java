package com.oddschecker.ngf.feedingestersredisclient.controller;


import com.oddschecker.ngf.feedingestersredisclient.exception.ResourceNotFoundException;
import com.oddschecker.ngf.feedingestersredisclient.model.RedisResponse;
import com.oddschecker.ngf.feedingestersredisclient.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.oddschecker.ngf.feedingestersredisclient.service.impl.RedisServiceImpl.KEY;
import static com.oddschecker.ngf.feedingestersredisclient.service.impl.RedisServiceImpl.PATTERN;

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
    public ResponseEntity<RedisResponse<List<String>>> getRedisValuesByKeys(@RequestBody Map<String, String> keyPatternMap)
            throws Exception {
        return ResponseEntity.ok(redisService.getRedisKeysByPattern(keyPatternMap));
    }

    @GetMapping("/{key}")
    public ResponseEntity<RedisResponse> getRedisKey(@PathVariable String key) throws Exception {
        return Optional.ofNullable(redisService.getRedisKey(key))
                .map(redisResponse -> ResponseEntity.ok().body(redisResponse))
                .orElseThrow(() -> new ResourceNotFoundException(key));
    }

    @PostMapping("/")
    public ResponseEntity<RedisResponse> getRedisKeyPost(@RequestBody Map<String, String> keyPatternMap) throws Exception {
        return Optional.ofNullable(redisService.getRedisKeyPost(keyPatternMap))
                .map(redisResponse -> ResponseEntity.ok().body(redisResponse))
                .orElseThrow(() -> new ResourceNotFoundException(keyPatternMap.get(KEY)));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<RedisResponse<String>> deleteRedisKey(@PathVariable String key)
            throws Exception {

        return Optional.ofNullable(redisService.deleteRedisKey(key))
                .map(delLong -> ResponseEntity.ok().body(delLong))
                .orElseThrow(() -> new ResourceNotFoundException(key));
    }

    @PostMapping("/diff")
    public ResponseEntity<RedisResponse<Set<String>>> getDiff(@RequestBody Set<String> keys) throws Exception {
        return ResponseEntity.ok(redisService.getRedisKeyDiff(keys));
    }

    @DeleteMapping("/keys")
    public ResponseEntity<RedisResponse<String>> deleteRedisKeysByPattern(@RequestBody Map<String, String> keyPatternMap)
            throws Exception {
        return Optional.ofNullable(redisService.deleteRedisKeysByPattern(keyPatternMap))
                .map(delLong -> ResponseEntity.ok().body(delLong))
                .orElseThrow(() -> new ResourceNotFoundException(keyPatternMap.get(PATTERN)));
    }


}
