package com.tacz.guns.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tacz.guns.command.sub.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class RootCommand {
    private static final String ROOT_NAME = "tacz";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal(ROOT_NAME)
                .requires((source -> source.hasPermissionLevel(2)));
        root.then(AttachmentLockCommand.get());
        root.then(DebugCommand.get());
        root.then(DummyAmmoCommand.get());
        root.then(OverwriteCommand.get());
        root.then(ReloadCommand.get());
        root.then(HideTooltipPartCommand.get());
        dispatcher.register(root);
    }
}
