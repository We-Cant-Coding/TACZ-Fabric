package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAttachment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface AttachmentItemDataAccessor extends IAttachment {
    String ATTACHMENT_ID_TAG = "AttachmentId";
    String SKIN_ID_TAG = "Skin";
    String ZOOM_NUMBER_TAG = "ZoomNumber";

    @NotNull
    static Identifier getAttachmentIdFromTag(@Nullable NbtCompound nbt) {
        if (nbt == null) {
            return DefaultAssets.EMPTY_ATTACHMENT_ID;
        }
        if (nbt.contains(ATTACHMENT_ID_TAG, NbtElement.STRING_TYPE)) {
            Identifier attachmentId = Identifier.tryParse(nbt.getString(ATTACHMENT_ID_TAG));
            return Objects.requireNonNullElse(attachmentId, DefaultAssets.EMPTY_ATTACHMENT_ID);
        }
        return DefaultAssets.EMPTY_ATTACHMENT_ID;
    }

    static int getZoomNumberFromTag(@Nullable NbtCompound nbt) {
        if (nbt == null) {
            return 0;
        }
        if (nbt.contains(ZOOM_NUMBER_TAG, NbtElement.INT_TYPE)) {
            return nbt.getInt(ZOOM_NUMBER_TAG);
        }
        return 0;
    }

    static void setZoomNumberToTag(NbtCompound nbt, int zoomNumber) {
        nbt.putInt(ZOOM_NUMBER_TAG, zoomNumber);
    }

    @Override
    @NotNull
    default Identifier getAttachmentId(ItemStack attachmentStack) {
        NbtCompound nbt = attachmentStack.getOrCreateNbt();
        return getAttachmentIdFromTag(nbt);
    }

    @Override
    default void setAttachmentId(ItemStack attachmentStack, @Nullable Identifier attachmentId) {
        NbtCompound nbt = attachmentStack.getOrCreateNbt();
        if (attachmentId != null) {
            nbt.putString(ATTACHMENT_ID_TAG, attachmentId.toString());
        }
    }

    @Override
    @Nullable
    default Identifier getSkinId(ItemStack attachmentStack) {
        NbtCompound nbt = attachmentStack.getOrCreateNbt();
        if (nbt.contains(SKIN_ID_TAG, NbtElement.STRING_TYPE)) {
            return Identifier.tryParse(nbt.getString(SKIN_ID_TAG));
        }
        return null;
    }

    @Override
    default void setSkinId(ItemStack attachmentStack, @Nullable Identifier skinId) {
        NbtCompound nbt = attachmentStack.getOrCreateNbt();
        if (skinId != null) {
            nbt.putString(SKIN_ID_TAG, skinId.toString());
        } else {
            nbt.remove(SKIN_ID_TAG);
        }
    }

    @Override
    default int getZoomNumber(ItemStack attachmentStack) {
        NbtCompound nbt = attachmentStack.getOrCreateNbt();
        return getZoomNumberFromTag(nbt);
    }

    @Override
    default void setZoomNumber(ItemStack attachmentStack, int zoomNumber) {
        NbtCompound nbt = attachmentStack.getOrCreateNbt();
        setZoomNumberToTag(nbt, zoomNumber);
    }
}
