package com.tacz.guns.command.sub;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tacz.guns.api.item.IGun;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class AttachmentLockCommand {
    private static final String ATTACHMENT_LOCK_NAME = "attachment_lock";
    private static final String ENTITY = "target";
    private static final String GUN_ATTACHMENT_LOCK = "AttachmentLock";

    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        LiteralArgumentBuilder<ServerCommandSource> attachmentLock = CommandManager.literal(ATTACHMENT_LOCK_NAME);
        RequiredArgumentBuilder<ServerCommandSource, EntitySelector> entities = CommandManager.argument(ENTITY, EntityArgumentType.entities());
        RequiredArgumentBuilder<ServerCommandSource, Boolean> locked = CommandManager.argument(GUN_ATTACHMENT_LOCK, BoolArgumentType.bool());
        attachmentLock.then(entities.then(locked.executes(AttachmentLockCommand::setAttachmentLock)));
        return attachmentLock;
    }

    private static int setAttachmentLock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var entities = EntityArgumentType.getEntities(context, ENTITY);
        int cnt = 0;
        boolean locked = BoolArgumentType.getBool(context, GUN_ATTACHMENT_LOCK);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                ItemStack stack = living.getMainHandStack();
                if (stack.getItem() instanceof IGun iGun) {
                    iGun.setAttachmentLock(stack, locked);
                    cnt++;
                }
            }
        }
        return cnt;
    }
}
