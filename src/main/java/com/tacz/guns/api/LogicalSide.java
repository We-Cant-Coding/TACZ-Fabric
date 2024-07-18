package com.tacz.guns.api;

public enum LogicalSide {
    CLIENT,
    SERVER;

    private LogicalSide() {
    }

    public boolean isServer() {
        return !this.isClient();
    }

    public boolean isClient() {
        return this == CLIENT;
    }
}
