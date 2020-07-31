package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddedComment {
    @JsonProperty("parent_id")
    private String parentId;

    @JsonProperty("post_id")
    private int postId;

    private String text;
}
