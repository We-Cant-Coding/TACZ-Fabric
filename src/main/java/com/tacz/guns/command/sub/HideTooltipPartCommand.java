package com.tacz.guns.command.sub;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.item.GunTooltipPart;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class HideTooltipPartCommand {
    private static final String HIDE_TOOLTIP_PART_NAME = "hide_tooltip_part";
    private static final String ENTITY = "target";
    private static final String MASK = "mask";

    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        LiteralArgumentBuilder<ServerCommandSource> base = CommandManager.literal(HIDE_TOOLTIP_PART_NAME);
        RequiredArgumentBuilder<ServerCommandSource, EntitySelector> entities = CommandManager.argument(ENTITY, EntityArgumentType.entities());
        RequiredArgumentBuilder<ServerCommandSource, Integer> part = CommandManager.argument(MASK, IntegerArgumentType.integer(0));
        base.then(entities.then(part.executes(HideTooltipPartCommand::setHide)));
        return base;
    }

    private static int setHide(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var entities = EntityArgumentType.getEntities(context, ENTITY);
        int cnt = 0;
        int mask = IntegerArgumentType.getInteger(context, MASK);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                ItemStack stack = living.getMainHandStack();
                if (stack.getItem() instanceof IGun) {
                    GunTooltipPart.setHideFlags(stack, mask);
                    cnt++;
                }
            }
        }
        return cnt;
    }
}
