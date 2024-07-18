package com.tacz.guns.client.init;

import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.block.entity.TargetBlockEntity;
import com.tacz.guns.client.renderer.block.GunSmithTableRenderer;
import com.tacz.guns.client.renderer.block.StatueRenderer;
import com.tacz.guns.client.renderer.block.TargetRenderer;
import com.tacz.guns.client.renderer.entity.EntityBulletRenderer;
import com.tacz.guns.client.renderer.entity.TargetMinecartRenderer;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.entity.TargetMinecart;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ModEntitiesRender {

    public static void entityRenderers() {
        EntityRendererRegistry.register(EntityKineticBullet.TYPE, EntityBulletRenderer::new);
        EntityRendererRegistry.register(TargetMinecart.TYPE, TargetMinecartRenderer::new);
        BlockEntityRendererFactories.register(GunSmithTableBlockEntity.TYPE, GunSmithTableRenderer::new);
        BlockEntityRendererFactories.register(TargetBlockEntity.TYPE, TargetRenderer::new);
        BlockEntityRendererFactories.register(StatueBlockEntity.TYPE, StatueRenderer::new);
    }
}
