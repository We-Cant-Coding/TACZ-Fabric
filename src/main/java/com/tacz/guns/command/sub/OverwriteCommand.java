package com.tacz.guns.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tacz.guns.config.common.OtherConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OverwriteCommand {
    private static final String OVERWRITE_NAME = "overwrite";
    private static final String ENABLE = "enable";

    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        LiteralArgumentBuilder<ServerCommandSource> reload = CommandManager.literal(OVERWRITE_NAME);
        RequiredArgumentBuilder<ServerCommandSource, Boolean> enable = CommandManager.argument(ENABLE, BoolArgumentType.bool());
        reload.then(enable.executes(OverwriteCommand::setOverwrite));
        return reload;
    }

    private static int setOverwrite(CommandContext<ServerCommandSource> context) {
        boolean enable = BoolArgumentType.getBool(context, ENABLE);
        OtherConfig.DEFAULT_PACK_DEBUG.set(!enable);
        if (context.getSource().getEntity() instanceof ServerPlayerEntity serverPlayer) {
            if (OtherConfig.DEFAULT_PACK_DEBUG.get()) {
                serverPlayer.sendMessage(Text.translatable("commands.tacz.reload.overwrite_off"));
            } else {
                serverPlayer.sendMessage(Text.translatable("commands.tacz.reload.overwrite_on"));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
