package com.tacz.guns.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.ClientReloadManager;
import com.tacz.guns.config.common.OtherConfig;
import com.tacz.guns.resource.DedicatedServerReloadManager;
import com.tacz.guns.util.EnvironmentUtil;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class ReloadCommand {
    private static final String RELOAD_NAME = "reload";

    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        LiteralArgumentBuilder<ServerCommandSource> reload = CommandManager.literal(RELOAD_NAME);
        reload.executes(ReloadCommand::reloadAllPack);
        return reload;
    }

    private static int reloadAllPack(CommandContext<ServerCommandSource> context) {
        StopWatch watch = StopWatch.createStarted();
        if (EnvironmentUtil.isClient()) {
            ClientReloadManager.reloadAllPack();
        }
        if (EnvironmentUtil.isServer()) {
            DedicatedServerReloadManager.reloadFromCommand(context);
        }
        watch.stop();
        double time = watch.getTime(TimeUnit.MICROSECONDS) / 1000.0;
        if (context.getSource().getEntity() instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.translatable("commands.tacz-fabric.reload.success", time));
            if (OtherConfig.DEFAULT_PACK_DEBUG.get()) {
                serverPlayer.sendMessage(Text.translatable("commands.tacz-fabric.reload.overwrite_off"));
                serverPlayer.sendMessage(Text.translatable("commands.tacz-fabric.reload.overwrite_command.off"));
            } else {
                serverPlayer.sendMessage(Text.translatable("commands.tacz-fabric.reload.overwrite_on"));
                serverPlayer.sendMessage(Text.translatable("commands.tacz-fabric.reload.overwrite_command.on"));
            }
        }
        GunMod.LOGGER.info("Model loading time: {} ms", time);
        return Command.SINGLE_SUCCESS;
    }
}
