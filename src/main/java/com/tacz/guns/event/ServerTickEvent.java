package com.tacz.guns.event;

import com.tacz.guns.util.CycleTaskHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class ServerTickEvent implements ServerTickEvents.StartTick, ServerTickEvents.EndTick {

    @Override
    public void onStartTick(MinecraftServer server) {
        onServerTick();
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        onServerTick();
    }

    public static void onServerTick() {
        // 更新 CycleTaskHelper 中的任务
        CycleTaskHelper.tick();
    }
}
