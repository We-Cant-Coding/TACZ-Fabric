package com.tacz.guns.client.event;

import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.client.gui.GunSmithTableScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.concurrent.atomic.AtomicBoolean;

public class PreventsHotbarEvent {
    public static void onRenderHotbarEvent(AtomicBoolean cancelled) {
        // todo 행동 테스트 필요
        Screen screen = MinecraftClient.getInstance().currentScreen;
        // 화기 합성 스테이션 인터페이스 배경 꺼짐
        if (screen instanceof GunSmithTableScreen) {
            cancelled.set(true);
            return;
        }
        // 총기 수정 인터페이스 배경 해제
        if (screen instanceof GunRefitScreen) {
            cancelled.set(true);
        }
    }
}
