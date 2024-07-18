package com.tacz.guns.command.sub;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class DummyAmmoCommand {
    private static final String DUMMY_NAME = "dummy";
    private static final String ENTITY = "target";
    private static final String AMOUNT = "dummyAmount";

    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        LiteralArgumentBuilder<ServerCommandSource> dummy = CommandManager.literal(DUMMY_NAME);
        RequiredArgumentBuilder<ServerCommandSource, EntitySelector> entities = CommandManager.argument(ENTITY, EntityArgumentType.entities());
        RequiredArgumentBuilder<ServerCommandSource, Integer> amount = CommandManager.argument(AMOUNT, IntegerArgumentType.integer(0));
        dummy.then(entities.then(amount.executes(DummyAmmoCommand::setDummy)));
        return dummy;
    }

    private static int setDummy(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var entities = EntityArgumentType.getEntities(context, ENTITY);
        int cnt = 0;
        int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                ItemStack stack = living.getMainHandStack();
                if (stack.getItem() instanceof IGun iGun) {
                    iGun.setDummyAmmoAmount(stack, amount);
                    cnt++;
                }
            }
        }
        return cnt;
    }
}
