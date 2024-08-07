package com.tacz.guns.client.resource.index;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.tacz.guns.client.model.BedrockAttachmentModel;
import com.tacz.guns.client.resource.ClientAssetManager;
import com.tacz.guns.client.resource.pojo.display.attachment.AttachmentDisplay;
import com.tacz.guns.client.resource.pojo.display.attachment.AttachmentLod;
import com.tacz.guns.client.resource.pojo.model.BedrockModelPOJO;
import com.tacz.guns.client.resource.pojo.model.BedrockVersion;
import com.tacz.guns.client.resource.pojo.skin.attachment.AttachmentSkin;
import com.tacz.guns.resource.CommonAssetManager;
import com.tacz.guns.resource.pojo.AttachmentIndexPOJO;
import com.tacz.guns.resource.pojo.data.attachment.AttachmentData;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class ClientAttachmentIndex {
    private final Map<Identifier, ClientAttachmentSkinIndex> skinIndexMap = Maps.newHashMap();
    private String name;
    private @Nullable BedrockAttachmentModel attachmentModel;
    private @Nullable Identifier modelTexture;
    private @Nullable Pair<BedrockAttachmentModel, Identifier> lodModel;
    private Identifier slotTexture;
    private AttachmentData data;
    private float fov = 70.0f;
    private float @Nullable [] zoom;
    private boolean isScope;
    private boolean isSight;
    private boolean showMuzzle;
    private @Nullable String adapterNodeName;
    private @Nullable String tooltipKey;
    private Map<String, Identifier> sounds;

    private ClientAttachmentIndex() {
    }

    public static ClientAttachmentIndex getInstance(Identifier registryName, AttachmentIndexPOJO indexPOJO) throws IllegalArgumentException {
        ClientAttachmentIndex index = new ClientAttachmentIndex();
        checkIndex(indexPOJO, index);
        AttachmentDisplay display = checkDisplay(indexPOJO, index);
        checkData(indexPOJO, index);
        checkName(indexPOJO, index);
        checkSlotTexture(display, index);
        checkTextureAndModel(display, index);
        checkLod(display, index);
        checkSkins(registryName, index);
        checkSounds(display, index);
        return index;
    }

    private static void checkIndex(AttachmentIndexPOJO attachmentIndexPOJO, ClientAttachmentIndex index) {
        Preconditions.checkArgument(attachmentIndexPOJO != null, "index object file is empty");
        index.tooltipKey = attachmentIndexPOJO.getTooltip();
    }

    @NotNull
    private static AttachmentDisplay checkDisplay(AttachmentIndexPOJO indexPOJO, ClientAttachmentIndex index) {
        Identifier pojoDisplay = indexPOJO.getDisplay();
        Preconditions.checkArgument(pojoDisplay != null, "index object missing display field");
        AttachmentDisplay display = ClientAssetManager.INSTANCE.getAttachmentDisplay(pojoDisplay);
        Preconditions.checkArgument(display != null, "there is no corresponding display file");
        Preconditions.checkArgument(display.getFov() > 0, "fov must > 0");
        index.fov = display.getFov();
        index.zoom = display.getZoom();
        if (index.zoom != null) {
            for (int i = 0; i < index.zoom.length; i++) {
                if (index.zoom[i] < 1) {
                    throw new IllegalArgumentException("zoom must >= 1");
                }
            }
        }
        index.isScope = display.isScope();
        index.isSight = display.isSight();
        index.adapterNodeName = display.getAdapterNodeName();
        index.showMuzzle = display.isShowMuzzle();
        return display;
    }

    private static void checkData(AttachmentIndexPOJO indexPOJO, ClientAttachmentIndex index) {
        Identifier dataId = indexPOJO.getData();
        Preconditions.checkArgument(dataId != null, "index object missing pojoData field");
        AttachmentData data = CommonAssetManager.INSTANCE.getAttachmentData(dataId);
        Preconditions.checkArgument(data != null, "there is no corresponding data file");
        // 剩下的不需要校验了，Common的读取逻辑中已经校验过了
        index.data = data;
    }

    private static void checkName(AttachmentIndexPOJO indexPOJO, ClientAttachmentIndex index) {
        index.name = indexPOJO.getName();
        if (StringUtils.isBlank(index.name)) {
            index.name = "custom.tacz.error.no_name";
        }
    }

    private static void checkSlotTexture(AttachmentDisplay display, ClientAttachmentIndex index) {
        index.slotTexture = Objects.requireNonNullElseGet(display.getSlotTextureLocation(), MissingSprite::getMissingSpriteId);
    }

    private static void checkTextureAndModel(AttachmentDisplay display, ClientAttachmentIndex index) {
        // 不检查模型/材质是否为 null，模型/材质可以为 null
        index.attachmentModel = ClientAssetManager.INSTANCE.getOrLoadAttachmentModel(display.getModel());
        if (index.attachmentModel != null) {
            index.attachmentModel.setIsScope(display.isScope());
            index.attachmentModel.setIsSight(display.isSight());
        }
        index.modelTexture = display.getTexture();
    }

    private static void checkLod(AttachmentDisplay display, ClientAttachmentIndex index) {
        AttachmentLod gunLod = display.getAttachmentLod();
        if (gunLod != null) {
            Identifier texture = gunLod.getModelTexture();
            if (gunLod.getModelLocation() == null) {
                return;
            }
            if (texture == null) {
                return;
            }
            BedrockModelPOJO modelPOJO = ClientAssetManager.INSTANCE.getModels(gunLod.getModelLocation());
            if (modelPOJO == null) {
                return;
            }
            // 先判断是不是 1.10.0 版本基岩版模型文件
            if (BedrockVersion.isLegacyVersion(modelPOJO) && modelPOJO.getGeometryModelLegacy() != null) {
                BedrockAttachmentModel model = new BedrockAttachmentModel(modelPOJO, BedrockVersion.LEGACY);
                index.lodModel = Pair.of(model, texture);
            }
            // 判定是不是 1.12.0 版本基岩版模型文件
            if (BedrockVersion.isNewVersion(modelPOJO) && modelPOJO.getGeometryModelNew() != null) {
                BedrockAttachmentModel model = new BedrockAttachmentModel(modelPOJO, BedrockVersion.NEW);
                index.lodModel = Pair.of(model, texture);
            }
        }
    }

    private static void checkSkins(Identifier registryName, ClientAttachmentIndex index) {
        Map<Identifier, AttachmentSkin> skins = ClientAssetManager.INSTANCE.getAttachmentSkins(registryName);
        if (skins != null) {
            for (Map.Entry<Identifier, AttachmentSkin> entry : skins.entrySet()) {
                ClientAttachmentSkinIndex skinIndex = ClientAttachmentSkinIndex.getInstance(entry.getValue());
                index.skinIndexMap.put(entry.getKey(), skinIndex);
            }
        }
    }

    private static void checkSounds(AttachmentDisplay display, ClientAttachmentIndex index) {
        Map<String, Identifier> displaySounds = display.getSounds();
        if (displaySounds == null) {
            index.sounds = Maps.newHashMap();
            return;
        }
        index.sounds = displaySounds;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getTooltipKey() {
        return tooltipKey;
    }

    @Nullable
    public BedrockAttachmentModel getAttachmentModel() {
        return attachmentModel;
    }

    @Nullable
    public Identifier getModelTexture() {
        return modelTexture;
    }

    @Nullable
    public Pair<BedrockAttachmentModel, Identifier> getLodModel() {
        return lodModel;
    }

    public Identifier getSlotTexture() {
        return slotTexture;
    }

    public float getFov() {
        return fov;
    }

    public float @Nullable [] getZoom() {
        return zoom;
    }

    public AttachmentData getData() {
        return data;
    }

    @Nullable
    public ClientAttachmentSkinIndex getSkinIndex(@Nullable Identifier skinName) {
        if (skinName == null) {
            return null;
        }
        return skinIndexMap.get(skinName);
    }

    public boolean isScope() {
        return isScope;
    }

    public boolean isSight() {
        return isSight;
    }

    @Nullable
    public String getAdapterNodeName() {
        return adapterNodeName;
    }

    public boolean isShowMuzzle() {
        return showMuzzle;
    }

    public Map<String, Identifier> getSounds() {
        return sounds;
    }
}
