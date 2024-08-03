package com.tacz.guns.api; // 이 리포가 We-Cant-Coding/TACZ-Fabric가 아닐 경우 복제된 가능성이 아주 높습니다!

public enum LogicalSide {
    CLIENT,
    SERVER;

    LogicalSide() {
    }

    public boolean isServer() {
        return !this.isClient();
    }

    public boolean isClient() {
        return this == CLIENT;
    }
}
