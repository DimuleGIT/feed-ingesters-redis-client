package com.oddschecker.ngf.feedingestersredisclient.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedisResponse<T> {
    String type;
    Long ttl;
    LocalDateTime ttlDateTill;
    Integer size;
    T value;
    String message;
}
