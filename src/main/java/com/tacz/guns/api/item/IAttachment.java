package com.tacz.guns.api.item;

import com.tacz.guns.api.item.attachment.AttachmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IAttachment {
    /**
     * @return 如果物品类型为 IAttachment 则返回显式转换后的实例，否则返回 null。
     */
    @Nullable
    static IAttachment getIAttachmentOrNull(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.getItem() instanceof IAttachment iAttachment) {
            return iAttachment;
        }
        return null;
    }

    /**
     * 获取配件 ID
     */
    @NotNull
    Identifier getAttachmentId(ItemStack attachmentStack);

    /**
     * 设置配件 ID
     */
    void setAttachmentId(ItemStack attachmentStack, @Nullable Identifier attachmentId);

    /**
     * 获取配件的皮肤 ID
     */
    @Nullable
    Identifier getSkinId(ItemStack attachmentStack);

    /**
     * 设置配件的皮肤 ID
     */
    void setSkinId(ItemStack attachmentStack, @Nullable Identifier skinId);

    /**
     * 获取瞄具配件的缩放倍率的数字索引，仅瞄具配件可用
     */
    int getZoomNumber(ItemStack attachmentStack);

    /**
     * 设置瞄具配件的缩放倍率的数字索引
     */
    void setZoomNumber(ItemStack attachmentStack, int zoomNumber);

    /**
     * 配件类型
     */
    @NotNull
    AttachmentType getType(ItemStack attachmentStack);
}
