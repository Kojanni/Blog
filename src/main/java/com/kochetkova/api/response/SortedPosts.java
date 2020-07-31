package com.kochetkova.api.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
public class SortedPosts {
    private int count;
    private List<Post> posts;
}
