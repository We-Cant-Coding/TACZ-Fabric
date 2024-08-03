package com.tacz.guns.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.components.smith.ResultButton;
import com.tacz.guns.client.gui.components.smith.TypeButton;
import com.tacz.guns.client.resource.ClientAssetManager;
import com.tacz.guns.client.resource.pojo.PackInfo;
import com.tacz.guns.crafting.GunSmithTableIngredient;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.crafting.GunSmithTableResult;
import com.tacz.guns.init.ModCreativeTabs;
import com.tacz.guns.inventory.GunSmithTableMenu;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.packets.c2s.CraftC2SPacket;
import com.tacz.guns.util.RenderDistance;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class GunSmithTableScreen extends HandledScreen<GunSmithTableMenu> {
    private static final Identifier TEXTURE = new Identifier(GunMod.MOD_ID, "textures/gui/gun_smith_table.png");
    private static final Identifier SIDE = new Identifier(GunMod.MOD_ID, "textures/gui/gun_smith_table_side.png");

    private final List<String> recipeKeys = Lists.newArrayList();
    private final Map<String, List<Identifier>> recipes = Maps.newHashMap();

    private int typePage;
    private String selectedType;
    private List<Identifier> selectedRecipeList;

    private int indexPage;
    private @Nullable GunSmithTableRecipe selectedRecipe;
    private @Nullable Int2IntArrayMap playerIngredientCount;

    private int scale = 70;

    public GunSmithTableScreen(GunSmithTableMenu menu, PlayerInventory inventory, Text title) {
        super(menu, inventory, title);
        this.backgroundWidth = 344;
        this.backgroundHeight = 186;
        this.classifyRecipes();

        this.typePage = 0;
        this.selectedType = GunSmithTableResult.AMMO;
        this.selectedRecipeList = recipes.get(selectedType);

        this.indexPage = 0;
        this.selectedRecipe = this.getSelectedRecipe(this.selectedRecipeList.get(0));
        this.getPlayerIngredientCount(this.selectedRecipe);
    }

    public static void drawModCenteredString(DrawContext gui, TextRenderer textRenderer, Text component, int pX, int pY, int color) {
        OrderedText text = component.asOrderedText();
        gui.drawText(textRenderer, text, pX - textRenderer.getWidth(text) / 2, pY, color, false);
    }

    private void classifyRecipes() {
        // 排序
        // 子弹
        putRecipeType(ModCreativeTabs.AMMO_TAB);
        // 配件
        putRecipeType(ModCreativeTabs.ATTACHMENT_EXTENDED_MAG_TAB);
        putRecipeType(ModCreativeTabs.ATTACHMENT_SCOPE_TAB);
        putRecipeType(ModCreativeTabs.ATTACHMENT_MUZZLE_TAB);
        putRecipeType(ModCreativeTabs.ATTACHMENT_STOCK_TAB);
        putRecipeType(ModCreativeTabs.ATTACHMENT_GRIP_TAB);
        // 枪械
        putRecipeType(ModCreativeTabs.GUN_PISTOL_TAB);
        putRecipeType(ModCreativeTabs.GUN_SNIPER_TAB);
        putRecipeType(ModCreativeTabs.GUN_RIFLE_TAB);
        putRecipeType(ModCreativeTabs.GUN_SHOTGUN_TAB);
        putRecipeType(ModCreativeTabs.GUN_SMG_TAB);
        putRecipeType(ModCreativeTabs.GUN_RPG_TAB);
        putRecipeType(ModCreativeTabs.GUN_MG_TAB);

        TimelessAPI.getAllRecipes().forEach((id, recipe) -> {
            String groupName = recipe.getResult().group();
            if (this.recipeKeys.contains(groupName)) {
                recipes.computeIfAbsent(groupName, g -> Lists.newArrayList()).add(id);
            }
        });
    }

    private void putRecipeType(ItemGroup tab) {
        var id = Registries.ITEM_GROUP.getId(tab);
        String name = id.getPath();
        this.recipeKeys.add(name);
    }

    @Nullable
    private GunSmithTableRecipe getSelectedRecipe(Identifier recipeId) {
        return TimelessAPI.getAllRecipes().get(recipeId);
    }

    private void getPlayerIngredientCount(GunSmithTableRecipe recipe) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        List<GunSmithTableIngredient> ingredients = recipe.getInputs();
        int size = ingredients.size();
        this.playerIngredientCount = new Int2IntArrayMap(size);
        for (int i = 0; i < size; i++) {
            GunSmithTableIngredient ingredient = ingredients.get(i);
            PlayerInventory inventory = player.getInventory();
            int count = 0;
            for (ItemStack stack : inventory.main) {
                if (!stack.isEmpty() && ingredient.ingredient().test(stack)) {
                    count = count + stack.getCount();
                }
            }
            playerIngredientCount.put(i, count);
        }
    }

    public void updateIngredientCount() {
        if (this.selectedRecipe != null) {
            this.getPlayerIngredientCount(selectedRecipe);
        }
        this.init();
    }

    @Override
    protected void init() {
        super.init();
        this.clearChildren();
        this.addTypePageButtons();
        this.addTypeButtons();
        this.addIndexPageButtons();
        this.addIndexButtons();
        this.addScaleButtons();
        this.addCraftButton();
        this.addUrlButton();
    }

    private void addCraftButton() {
        this.addDrawableChild(new TexturedButtonWidget(x + 289, y + 162, 48, 18, 138, 164, 18, TEXTURE, b -> {
            if (this.selectedRecipe != null && playerIngredientCount != null) {
                // 检查是否能合成，不能就不发包
                List<GunSmithTableIngredient> inputs = selectedRecipe.getInputs();
                int size = inputs.size();
                for (int i = 0; i < size; i++) {
                    if (i >= playerIngredientCount.size()) {
                        return;
                    }
                    int hasCount = playerIngredientCount.get(i);
                    int needCount = inputs.get(i).count();
                    // 拥有数量小于需求数量，不发包
                    if (hasCount < needCount) {
                        return;
                    }
                }
                NetworkHandler.sendToServer(new CraftC2SPacket(this.selectedRecipe.getId(), this.handler.syncId));
            }
        }));
    }

    private void addUrlButton() {
        this.addDrawableChild(new TexturedButtonWidget(x + 112, y + 164, 18, 18, 149, 211, 18, TEXTURE, b -> {
            if (this.selectedRecipe != null) {
                ItemStack output = selectedRecipe.getOutput();
                Item item = output.getItem();
                Identifier id;
                if (item instanceof IGun iGun) {
                    id = iGun.getGunId(output);
                } else if (item instanceof IAttachment iAttachment) {
                    id = iAttachment.getAttachmentId(output);
                } else if (item instanceof IAmmo iAmmo) {
                    id = iAmmo.getAmmoId(output);
                } else {
                    return;
                }

                PackInfo packInfo = ClientAssetManager.INSTANCE.getPackInfo(id);
                if (packInfo == null) {
                    return;
                }
                String url = packInfo.getUrl();
                if (StringUtils.isNotBlank(url) && client != null) {
                    client.setScreen(new ConfirmLinkScreen(yes -> {
                        if (yes) {
                            Util.getOperatingSystem().open(url);
                        }
                        client.setScreen(this);
                    }, url, false));
                }
            }
        }));
    }

    private void addIndexButtons() {
        if (selectedRecipeList == null || selectedRecipeList.isEmpty()) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            int finalIndex = i + indexPage * 6;
            if (finalIndex >= selectedRecipeList.size()) {
                break;
            }
            int yOffset = y + 66 + 17 * i;
            TimelessAPI.getRecipe(selectedRecipeList.get(finalIndex)).ifPresent(recipe -> {
                ResultButton button = addDrawableChild(new ResultButton(x + 144, yOffset, recipe.getOutput(), b -> {
                    this.selectedRecipe = recipe;
                    this.getPlayerIngredientCount(this.selectedRecipe);
                    this.init();
                }));
                if (this.selectedRecipe != null && recipe.getId().equals(this.selectedRecipe.getId())) {
                    button.setSelected(true);
                }
            });
        }
    }

    private void addTypeButtons() {
        for (int i = 0; i < 7; i++) {
            int typeIndex = typePage * 7 + i;
            if (typeIndex >= recipes.size()) {
                return;
            }
            String type = recipeKeys.get(typeIndex);
            int xOffset = x + 157 + 24 * i;
            List<Identifier> recipeIdGroups = recipes.get(type);
            if (recipeIdGroups.isEmpty()) {
                continue;
            }
            ItemStack icon = ItemStack.EMPTY;
            Identifier tabId = new Identifier(GunMod.MOD_ID, type);
            ItemGroup modTab = Registries.ITEM_GROUP.get(tabId);
            if (modTab != null) {
                icon = modTab.getIcon();
            }
            TypeButton typeButton = getTypeButton(xOffset, icon, type);
            this.addDrawableChild(typeButton);
        }
    }

    @NotNull
    private TypeButton getTypeButton(int xOffset, ItemStack icon, String type) {
        TypeButton typeButton = new TypeButton(xOffset, y + 2, icon, b -> {
            this.selectedType = type;
            this.selectedRecipeList = recipes.get(type);
            this.indexPage = 0;
            this.selectedRecipe = getSelectedRecipe(this.selectedRecipeList.get(0));
            this.getPlayerIngredientCount(this.selectedRecipe);
            this.init();
        });
        if (this.selectedType.equals(type)) {
            typeButton.setSelected(true);
        }
        return typeButton;
    }

    private void addIndexPageButtons() {
        this.addDrawableChild(new TexturedButtonWidget(x + 143, y + 56, 96, 6, 40, 166, 6, TEXTURE, b -> {
            if (this.indexPage > 0) {
                this.indexPage--;
                this.init();
            }
        }));
        this.addDrawableChild(new TexturedButtonWidget(x + 143, y + 171, 96, 6, 40, 186, 6, TEXTURE, b -> {
            if (selectedRecipeList != null && !selectedRecipeList.isEmpty()) {
                int maxIndexPage = (selectedRecipeList.size() - 1) / 6;
                if (this.indexPage < maxIndexPage) {
                    this.indexPage++;
                    this.init();
                }
            }
        }));
    }

    private void addTypePageButtons() {
        this.addDrawableChild(new TexturedButtonWidget(x + 136, y + 4, 18, 20, 0, 162, 20, TEXTURE, b -> {
            if (this.typePage > 0) {
                this.typePage--;
                this.init();
            }
        }));
        this.addDrawableChild(new TexturedButtonWidget(x + 327, y + 4, 18, 20, 20, 162, 20, TEXTURE, b -> {
            int maxIndexPage = (recipes.size() - 1) / 7;
            if (this.typePage < maxIndexPage) {
                this.typePage++;
                this.init();
            }
        }));
    }

    private void addScaleButtons() {
        this.addDrawableChild(new TexturedButtonWidget(x + 5, y + 5, 10, 10, 188, 173, 10, TEXTURE, b -> {
            this.scale = Math.min(this.scale + 20, 200);
        }));
        this.addDrawableChild(new TexturedButtonWidget(x + 17, y + 5, 10, 10, 200, 173, 10, TEXTURE, b -> {
            this.scale = Math.max(this.scale - 20, 10);
        }));
        this.addDrawableChild(new TexturedButtonWidget(x + 29, y + 5, 10, 10, 212, 173, 10, TEXTURE, b -> {
            this.scale = 70;
        }));
    }

    @Override
    public void render(@NotNull DrawContext graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        drawModCenteredString(graphics, textRenderer, Text.translatable("gui.tacz-fabric.gun_smith_table.preview"), x + 108, y + 5, 0x555555);
        graphics.drawText(textRenderer, Text.translatable(String.format("tacz.type.%s.name", selectedType)), x + 150, y + 32, 0x555555, false);
        graphics.drawText(textRenderer, Text.translatable("gui.tacz-fabric.gun_smith_table.ingredient"), x + 254, y + 50, 0x555555, false);
        drawModCenteredString(graphics, textRenderer, Text.translatable("gui.tacz-fabric.gun_smith_table.craft"), x + 312, y + 167, 0xFFFFFF);
        if (this.selectedRecipe != null) {
            this.renderLeftModel(this.selectedRecipe);
            this.renderPackInfo(graphics, this.selectedRecipe);
        }
        if (selectedRecipeList != null && !selectedRecipeList.isEmpty()) {
            renderIngredient(graphics, mouseX, mouseY);
        }

        this.drawables.stream().filter(w -> w instanceof ResultButton)
                .forEach(w -> ((ResultButton) w).renderTooltips(stack -> graphics.drawItemTooltip(textRenderer, stack, mouseX, mouseY)));
    }

    private void renderPackInfo(DrawContext gui, GunSmithTableRecipe recipe) {
        ItemStack output = recipe.getOutput();
        Item item = output.getItem();
        Identifier id;
        if (item instanceof IGun iGun) {
            id = iGun.getGunId(output);
        } else if (item instanceof IAttachment iAttachment) {
            id = iAttachment.getAttachmentId(output);
        } else if (item instanceof IAmmo iAmmo) {
            id = iAmmo.getAmmoId(output);
        } else {
            return;
        }

        PackInfo packInfo = ClientAssetManager.INSTANCE.getPackInfo(id);
        MatrixStack poseStack = gui.getMatrices();
        if (packInfo != null) {
            poseStack.push();
            poseStack.scale(0.75f, 0.75f, 1);
            Text nameText = Text.translatable(packInfo.getName());
            gui.drawText(textRenderer, nameText, (int) ((x + 6) / 0.75f), (int) ((y + 122) / 0.75f), Formatting.DARK_GRAY.getColorValue(), false);
            poseStack.pop();

            poseStack.push();
            poseStack.scale(0.5f, 0.5f, 1);

            int offsetX = (x + 6) * 2;
            int offsetY = (y + 123) * 2;
            int nameWidth = textRenderer.getWidth(nameText);
            Text ver = Text.literal("v" + packInfo.getVersion()).formatted(Formatting.UNDERLINE);
            gui.drawText(textRenderer, ver, (int) (offsetX + nameWidth * 0.75f / 0.5f + 5), offsetY, Formatting.DARK_GRAY.getColorValue(), false);
            offsetY += 14;

            String descKey = packInfo.getDescription();
            if (StringUtils.isNoneBlank(descKey)) {
                Text desc = Text.translatable(descKey);
                List<OrderedText> split = textRenderer.wrapLines(desc, 245);
                for (OrderedText charSequence : split) {
                    gui.drawText(textRenderer, charSequence, offsetX, offsetY, Formatting.DARK_GRAY.getColorValue(), false);
                    offsetY += textRenderer.fontHeight;
                }
                offsetY += 3;
            }

            gui.drawText(textRenderer, Text.translatable("gui.tacz-fabric.gun_smith_table.license")
                            .append(Text.literal(packInfo.getLicense()).formatted(Formatting.DARK_GRAY)),
                    offsetX, offsetY, Formatting.DARK_GRAY.getColorValue(), false);
            offsetY += 12;

            List<String> authors = packInfo.getAuthors();
            if (!authors.isEmpty()) {
                gui.drawText(textRenderer, Text.translatable("gui.tacz-fabric.gun_smith_table.authors")
                                .append(Text.literal(StringUtils.join(authors, ", ")).formatted(Formatting.DARK_GRAY)),
                        offsetX, offsetY, Formatting.DARK_GRAY.getColorValue(), false);
                offsetY += 12;
            }

            gui.drawText(textRenderer, Text.translatable("gui.tacz-fabric.gun_smith_table.date")
                            .append(Text.literal(packInfo.getDate()).formatted(Formatting.DARK_GRAY)),
                    offsetX, offsetY, Formatting.DARK_GRAY.getColorValue(), false);

            poseStack.pop();
        } else {
            Identifier recipeId = recipe.getId();
            gui.drawText(textRenderer, Text.translatable("gui.tacz-fabric.gun_smith_table.error").formatted(Formatting.DARK_RED), x + 6, y + 122, 0xAF0000, false);
            gui.drawText(textRenderer, Text.translatable("gui.tacz-fabric.gun_smith_table.error.id", recipeId.toString()).formatted(Formatting.DARK_RED), x + 6, y + 134, 0xFFFFFF, false);
            PackInfo errorPackInfo = ClientAssetManager.INSTANCE.getPackInfo(recipeId);
            if (errorPackInfo != null) {
                gui.drawText(textRenderer, Text.translatable(errorPackInfo.getName()).formatted(Formatting.DARK_RED), x + 6, y + 146, 0xAF0000, false);
            }
        }
    }

    private void renderIngredient(DrawContext gui, int mouseX, int mouseY) {
        if (this.selectedRecipe == null) {
            return;
        }
        List<GunSmithTableIngredient> inputs = this.selectedRecipe.getInputs();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++) {
                int index = i * 2 + j;
                if (index >= inputs.size()) {
                    return;
                }
                int offsetX = x + 254 + 45 * j;
                int offsetY = y + 62 + 17 * i;

                GunSmithTableIngredient smithTableIngredient = inputs.get(index);
                Ingredient ingredient = smithTableIngredient.ingredient();

                ItemStack[] items = ingredient.getMatchingStacks();
                ItemStack item = ItemStack.EMPTY;
                if (items.length > 0) {
                    int itemIndex = ((int) (System.currentTimeMillis() / 1_000)) % items.length;
                    item = items[itemIndex];
                }

                gui.drawItemWithoutEntity(item, offsetX, offsetY);

                MatrixStack poseStack = gui.getMatrices();
                poseStack.push();

                poseStack.translate(0, 0, 200);
                poseStack.scale(0.5f, 0.5f, 1);
                int count = smithTableIngredient.count();
                int hasCount = 0;
                if (playerIngredientCount != null && index < playerIngredientCount.size()) {
                    hasCount = playerIngredientCount.get(index);
                }
                int color = count <= hasCount ? 0xFFFFFF : 0xFF0000;
                gui.drawText(textRenderer, String.format("%d/%d", count, hasCount), (offsetX + 17) * 2, (offsetY + 10) * 2, color, false);

                poseStack.pop();

                // width = 16.0F, height = 16.0F
                boolean hovered = mouseX >= offsetX && mouseY >= offsetY && mouseX < offsetX + 16.0F && mouseY < offsetY + 16.0F;
                if (hovered) {
                    gui.drawItemTooltip(textRenderer, item, mouseX, mouseY);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void renderLeftModel(GunSmithTableRecipe recipe) {
        // 先标记一下，渲染高模
        RenderDistance.markGuiRenderTimestamp();

        float rotationPeriod = 8f;
        int xPos = x + 60;
        int yPos = y + 50;
        int startX = x + 3;
        int startY = y + 16;
        int width = 128;
        int height = 99;
        float rotPitch = 15;

        Window window = MinecraftClient.getInstance().getWindow();
        double windowGuiScale = window.getScaleFactor();
        int scissorX = (int) (startX * windowGuiScale);
        int scissorY = (int) (window.getHeight() - ((startY + height) * windowGuiScale));
        int scissorW = (int) (width * windowGuiScale);
        int scissorH = (int) (height * windowGuiScale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorW, scissorH);

        MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        MatrixStack posestack = RenderSystem.getModelViewStack();
        posestack.push();
        posestack.translate(xPos, yPos, 200);
        posestack.translate(8.0D, 8.0D, 0.0D);
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(scale, scale, scale);
        float rot = (System.currentTimeMillis() % (int) (rotationPeriod * 1000)) * (360f / (rotationPeriod * 1000));
        posestack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotPitch));
        posestack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot));
        RenderSystem.applyModelViewMatrix();
        MatrixStack tmpPose = new MatrixStack();
        VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        DiffuseLighting.disableGuiDepthLighting();

        MinecraftClient.getInstance().getItemRenderer().renderItem(recipe.getOutput(), ModelTransformationMode.FIXED, 0xf000f0, OverlayTexture.DEFAULT_UV, tmpPose, bufferSource, null, 0);

        bufferSource.draw();
        RenderSystem.enableDepthTest();
        DiffuseLighting.enableGuiDepthLighting();
        posestack.pop();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.disableScissor();
    }

    @Override
    protected void drawForeground(@NotNull DrawContext gui, int mouseX, int mouseY) {
    }

    @Override
    protected void drawBackground(@NotNull DrawContext gui, float partialTick, int mouseX, int mouseY) {
        this.renderBackground(gui);
        gui.drawTexture(SIDE, x, y, 0, 0, 134, 187);
        gui.drawTexture(TEXTURE, x + 136, y + 27, 0, 0, 208, 160);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
