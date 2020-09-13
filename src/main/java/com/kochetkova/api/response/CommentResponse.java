package com.kochetkova.api.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kochetkova.converter.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class CommentResponse {
    int id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime time;

    String text;

    UserResponse user;
}
