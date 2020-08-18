package com.kochetkova.api.request;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.TimeZone;

@Data
public class AddedPost {
    private Timestamp timestamp;
    private byte active;
    private String title;
    private Set<String> tags;
    private String text;


    public LocalDateTime getTime() {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
