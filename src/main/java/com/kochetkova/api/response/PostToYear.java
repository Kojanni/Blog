package com.kochetkova.api.response;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PostToYear {
    private Set<Integer> years;

    private Map<String, Integer> posts;

    public PostToYear() {
        this.years = new TreeSet<>();
        this.posts = new TreeMap<>();
    }
}
