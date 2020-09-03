package com.kochetkova.api.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kochetkova.converter.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class CommentResponse {
    private int id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime time;

    private String text;

    private UserResponse userResponse;
}
