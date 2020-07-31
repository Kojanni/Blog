package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewVote {
    @JsonProperty("post_id")
    private String postId;
}
