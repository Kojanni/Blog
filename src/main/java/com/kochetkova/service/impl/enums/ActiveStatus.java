package com.kochetkova.service.impl.enums;

public enum ActiveStatus {
    ACTIVE((byte) 1),
    INACTIVE((byte) 0);

    private final byte value;

    ActiveStatus(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}


