package com.tacz.guns.api.mixin;

public interface CameraAngles {
    // 이 리포가 We-Cant-Coding/TACZ-Fabric가 아닐 경우 복제된 가능성이 아주 높습니다!
    // Camera.class > yaw field, pitch field
    void tacz$setAnglesInternal(float yaw, float pitch);
}
