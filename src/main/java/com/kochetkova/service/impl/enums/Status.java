package com.kochetkova.service.impl.enums;

public enum Status {
    INACTIVE("inactive"),
    PENDING("pending"),
    DECLINED("declined"),
    PUBLISHED("published");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
