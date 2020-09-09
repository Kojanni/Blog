package com.kochetkova.api.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kochetkova.converter.LocalDateTimeSerializer;
import com.kochetkova.converter.LocalDatetimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class NewPostRequest {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDatetimeDeserializer.class)
    private LocalDateTime timestamp;
    private byte active;
    private String title;
    private List<String> tags;
    private String text;
}
