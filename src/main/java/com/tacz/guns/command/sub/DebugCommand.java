package com.tacz.guns.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class DebugCommand {
    public static boolean DEBUG = false;
    private static final String DEBUG_NAME = "debug";
    private static final String ENABLE = "enable";

    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        LiteralArgumentBuilder<ServerCommandSource> debugCommand = CommandManager.literal(DEBUG_NAME);
        RequiredArgumentBuilder<ServerCommandSource, Boolean> enable = CommandManager.argument(ENABLE, BoolArgumentType.bool());
        debugCommand.then(enable.executes(DebugCommand::setValue));
        return debugCommand;
    }

    private static int setValue(CommandContext<ServerCommandSource> context) {
        DEBUG = BoolArgumentType.getBool(context, ENABLE);
        if (context.getSource().getEntity() instanceof ServerPlayerEntity serverPlayer) {
            if (DEBUG) {
                serverPlayer.sendMessage(Text.literal("TacZ Debug Mode is Turn On"));
            } else {
                serverPlayer.sendMessage(Text.literal("TacZ Debug Mode is Turn Off"));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
