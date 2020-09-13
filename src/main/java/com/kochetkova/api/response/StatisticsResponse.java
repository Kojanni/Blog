package com.kochetkova.api.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kochetkova.converter.LocalDateTimeSerializer;
import com.kochetkova.converter.LocalDatetimeDeserializer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StatisticsResponse {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;

    @JsonDeserialize(using = LocalDatetimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime firstPublication;
}
