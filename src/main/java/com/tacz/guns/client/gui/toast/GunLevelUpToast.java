package com.tacz.guns.client.gui.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GunLevelUpToast implements Toast {
    private final Text title;
    private final Text subTitle;
    private final ItemStack icon;

    public GunLevelUpToast(ItemStack icon, Text titleComponent, @Nullable Text subtitle) {
        this.icon = icon;
        this.title = titleComponent;
        this.subTitle = subtitle;
    }

    @NotNull
    @Override
    public Visibility draw(@NotNull DrawContext gui, ToastManager toastComponent, long timeSinceLastVisible) {
        // todo 这个类没有实际使用，先不管了
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderTexture(0, TEXTURE);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//
//        toastComponent.blit(gui, 0, 0, 0, 0, this.width(), this.height());
//
//        List<FormattedCharSequence> list = null;
//        if (this.subTitle != null) {
//            list = toastComponent.getMinecraft().font.split(this.subTitle, 125);
//        }
//        int i = 0xffff00;
//        if (list != null) {
//            if (list.size() == 1) {
//                toastComponent.getMinecraft().font.draw(gui, this.title, 30.0F, 7.0F, i | 0xff000000);
//                toastComponent.getMinecraft().font.draw(gui, list.get(0), 30.0F, 18.0F, -1);
//            } else {
//                if (timeSinceLastVisible < 1500L) {
//                    int k = Mth.floor(Mth.clamp((float) (1500L - timeSinceLastVisible) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 0x4000000;
//                    toastComponent.getMinecraft().font.draw(gui, this.title, 30.0F, 11.0F, i | k);
//                } else {
//                    int j = Mth.floor(Mth.clamp((float) (timeSinceLastVisible - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 0x4000000;
//                    int l = this.height() / 2 - list.size() * 9 / 2;
//
//                    for (FormattedCharSequence formattedCharSequence : list) {
//                        toastComponent.getMinecraft().font.draw(gui, formattedCharSequence, 30.0F, (float) l, 0xffffff | j);
//                        l += 9;
//                    }
//                }
//            }
//        }
//        toastComponent.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(this.icon, 8, 8);
        return timeSinceLastVisible >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
