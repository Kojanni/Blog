package com.kochetkova.api.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CalendarResponse {
    List<Integer> years;
    Map<String, Integer> posts;
}
