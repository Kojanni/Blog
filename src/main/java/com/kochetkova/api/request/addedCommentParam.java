package com.kochetkova.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlElement;

@Component
public class addedCommentParam {
    @JsonProperty("parent_id")
    private String parentId;

    @JsonProperty("post_id")
    private int postId;

    @XmlElement
    private String text;
}
