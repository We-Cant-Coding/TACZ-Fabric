package com.tacz.guns.client.tooltip;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.client.resource.ClientAssetManager;
import com.tacz.guns.client.resource.pojo.PackInfo;
import com.tacz.guns.inventory.tooltip.AttachmentItemTooltip;
import com.tacz.guns.resource.pojo.data.attachment.AttachmentData;
import com.tacz.guns.resource.pojo.data.attachment.RecoilModifier;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ClientAttachmentItemTooltip implements TooltipComponent {
    private static final Cache<Identifier, List<ItemStack>> CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).build();
    private final Identifier attachmentId;
    private final List<Text> components = Lists.newArrayList();
    private final MutableText tips = Text.translatable("tooltip.tacz.attachment.yaw.shift");
    private final MutableText support = Text.translatable("tooltip.tacz.attachment.yaw.support");
    private @Nullable MutableText packInfo;
    private List<ItemStack> showGuns = Lists.newArrayList();

    public ClientAttachmentItemTooltip(AttachmentItemTooltip tooltip) {
        this.attachmentId = tooltip.attachmentId();
        this.addText(tooltip.type());
        this.getShowGuns();
        this.addPackInfo();
    }

    private void addPackInfo() {
        PackInfo packInfoObject = ClientAssetManager.INSTANCE.getPackInfo(attachmentId);
        if (packInfoObject != null) {
            packInfo = Text.translatable(packInfoObject.getName()).formatted(Formatting.BLUE).formatted(Formatting.ITALIC);
        }
    }

    private static List<ItemStack> getAllAllowGuns(List<ItemStack> output, Identifier attachmentId) {
        ItemStack attachment = AttachmentItemBuilder.create().setId(attachmentId).build();
        TimelessAPI.getAllCommonGunIndex().forEach(entry -> {
            Identifier gunId = entry.getKey();
            ItemStack gun = GunItemBuilder.create().setId(gunId).build();
            if (!(gun.getItem() instanceof IGun iGun)) {
                return;
            }
            if (iGun.allowAttachment(gun, attachment)) {
                output.add(gun);
            }
        });
        return output;
    }

    @Override
    public int getHeight() {
        if (!Screen.hasShiftDown()) {
            return components.size() * 10 + 28;
        }
        return (showGuns.size() - 1) / 16 * 18 + 50 + components.size() * 10;
    }

    @Override
    public int getWidth(TextRenderer font) {
        int[] width = new int[]{0};
        if (packInfo != null) {
            width[0] = Math.max(width[0], font.getWidth(packInfo) + 4);
        }
        components.forEach(c -> width[0] = Math.max(width[0], font.getWidth(c)));
        if (!Screen.hasShiftDown()) {
            return Math.max(width[0], font.getWidth(tips) + 4);
        } else {
            width[0] = Math.max(width[0], font.getWidth(support) + 4);
        }
        if (showGuns.size() > 15) {
            return Math.max(width[0], 260);
        }
        return Math.max(width[0], showGuns.size() * 16 + 4);
    }

    @Override
    public void drawText(TextRenderer font, int pX, int pY, Matrix4f matrix4f, VertexConsumerProvider.Immediate bufferSource) {
        int yOffset = pY;
        for (Text component : this.components) {
            font.draw(component, pX, yOffset, 0xffaa00, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            yOffset += 10;
        }
        if (!Screen.hasShiftDown()) {
            font.draw(tips, pX, pY + 5 + this.components.size() * 10, 0x9e9e9e, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            yOffset += 10;
        } else {
            yOffset += (showGuns.size() - 1) / 16 * 18 + 32;
        }
        // 枪包名
        if (packInfo != null) {
            font.draw(this.packInfo, pX, yOffset + 8, 0xffffff, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        }
    }

    @Override
    public void drawItems(TextRenderer font, int mouseX, int mouseY, DrawContext gui) {
        if (!Screen.hasShiftDown()) {
            return;
        }
        int minY = components.size() * 10 + 3;
        int maxX = getWidth(font);
        gui.fill(mouseX, mouseY + minY, mouseX + maxX, mouseY + minY + 11, 0x8F00b0ff);
        gui.drawTextWithShadow(font, support, mouseX + 2, mouseY + minY + 2, 0xe3f2fd);

        for (int i = 0; i < showGuns.size(); i++) {
            ItemStack stack = showGuns.get(i);
            int x = i % 16 * 16 + 2;
            int y = i / 16 * 18 + minY + 15;
            gui.drawItem(stack, mouseX + x, mouseY + y);
        }
    }

    private void getShowGuns() {
        try {
            this.showGuns = CACHE.get(attachmentId, () -> getAllAllowGuns(Lists.newArrayList(), attachmentId));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void addText(AttachmentType type) {
        TimelessAPI.getClientAttachmentIndex(attachmentId).ifPresent(index -> {
            AttachmentData data = index.getData();

            @Nullable String tooltipKey = index.getTooltipKey();
            if (tooltipKey != null) {
                String text = I18n.translate(tooltipKey);
                String[] split = text.split("\n");
                Arrays.stream(split).forEach(s -> components.add(Text.literal(s).formatted(Formatting.GRAY)));
            }

            if (type == AttachmentType.SCOPE) {
                float[] zoom = index.getZoom();
                if (zoom != null) {
                    String[] zoomText = new String[zoom.length];
                    for (int i = 0; i < zoom.length; i++) {
                        zoomText[i] = "x" + zoom[i];
                    }
                    String zoomJoinText = StringUtils.join(zoomText, ", ");
                    components.add(Text.translatable("tooltip.tacz.attachment.zoom", zoomJoinText).formatted(Formatting.GOLD));
                }
            }

            if (type == AttachmentType.EXTENDED_MAG) {
                int magLevel = data.getExtendedMagLevel();
                if (magLevel == 1) {
                    components.add(Text.translatable("tooltip.tacz.attachment.extended_mag_level_1").formatted(Formatting.GRAY));
                } else if (magLevel == 2) {
                    components.add(Text.translatable("tooltip.tacz.attachment.extended_mag_level_2").formatted(Formatting.BLUE));
                } else if (magLevel == 3) {
                    components.add(Text.translatable("tooltip.tacz.attachment.extended_mag_level_3").formatted(Formatting.LIGHT_PURPLE));
                }
            }

            float adsAddendTime = data.getAdsAddendTime();
            if (adsAddendTime > 0) {
                components.add(Text.translatable("tooltip.tacz.attachment.ads.increase").formatted(Formatting.RED));
            } else if (adsAddendTime < 0) {
                components.add(Text.translatable("tooltip.tacz.attachment.ads.decrease").formatted(Formatting.GREEN));
            }

            float inaccuracyAddend = data.getInaccuracyAddend();
            if (inaccuracyAddend > 0) {
                components.add(Text.translatable("tooltip.tacz.attachment.inaccuracy.increase").formatted(Formatting.RED));
            } else if (inaccuracyAddend < 0) {
                components.add(Text.translatable("tooltip.tacz.attachment.inaccuracy.decrease").formatted(Formatting.GREEN));
            }

            RecoilModifier recoilModifier = data.getRecoilModifier();
            if (recoilModifier != null) {
                float pitch = recoilModifier.getPitch();
                if (pitch > 0) {
                    components.add(Text.translatable("tooltip.tacz.attachment.pitch.increase").formatted(Formatting.RED));
                } else if (pitch < 0) {
                    components.add(Text.translatable("tooltip.tacz.attachment.pitch.decrease").formatted(Formatting.GREEN));
                }

                float yaw = recoilModifier.getYaw();
                if (yaw > 0) {
                    components.add(Text.translatable("tooltip.tacz.attachment.yaw.increase").formatted(Formatting.RED));
                } else if (yaw < 0) {
                    components.add(Text.translatable("tooltip.tacz.attachment.yaw.decrease").formatted(Formatting.GREEN));
                }
            }
        });
    }
}
