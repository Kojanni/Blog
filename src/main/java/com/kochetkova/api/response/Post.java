package com.kochetkova.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post {
    private Integer id;
    private LocalDateTime time;
    private User user;
    private String title;
    private String announce;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer commentCount;
    private Integer viewCount;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Comment> comments;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> tags;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Integer> years;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Integer> posts;
}
