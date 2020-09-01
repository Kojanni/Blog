package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AcceptPostRequest {
    @JsonProperty("post_id")
    private int postId;

    private String decision;

}
