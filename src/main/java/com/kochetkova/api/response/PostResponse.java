package com.kochetkova.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kochetkova.converter.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private Integer id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime timestamp;
    private UserResponse user;
    private String title;
    private String announce;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer commentCount;
    private Integer viewCount;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommentResponse> comments;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> tags;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Integer> years;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Integer> posts;
}
