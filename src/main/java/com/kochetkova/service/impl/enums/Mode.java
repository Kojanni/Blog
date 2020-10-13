package com.kochetkova.service.impl.enums;

public enum Mode {
    RECENT("recent"),
    POPULAR("popular"),
    BEST("best"),
    EARLY("early");

    private final String name;

    Mode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
