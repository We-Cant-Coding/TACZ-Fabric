package com.tacz.guns.event;

import com.tacz.guns.util.CycleTaskHelper;
import net.minecraft.server.MinecraftServer;

public class ServerTickEvent {

    public static void onServerTick(MinecraftServer ignoredServer) {
        // 更新 CycleTaskHelper 中的任务
        CycleTaskHelper.tick();
    }
}
