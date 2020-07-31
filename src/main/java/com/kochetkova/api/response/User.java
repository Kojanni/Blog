package com.kochetkova.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    int id;
    String name;
    String photo;
    String email;
    Boolean moderation;
    Integer moderationCount;
    Boolean setting;
}
