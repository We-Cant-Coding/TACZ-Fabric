package com.tacz.guns.client.model.bedrock;

import com.tacz.guns.client.model.IFunctionalRenderer;
import com.tacz.guns.client.resource.pojo.model.*;
import com.tacz.guns.compat.iris.IrisCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BedrockModel {
    public static BedrockModel dummyModel = new BedrockModel();
    /**
     * HashMap storing ModelRender sub-models.
     */
    protected final HashMap<String, ModelRendererWrapper> modelMap = new HashMap<>();
    /**
     * HashMap storing Bones, mainly used for finding parent bones for coordinate transformation later.
     */
    protected final HashMap<String, BonesItem> indexBones = new HashMap<>();
    /**
     * List of models that need to be rendered. Sub-bones loaded into parent bones do not need to be rendered.
     */
    protected final List<BedrockPart> shouldRender = new LinkedList<>();
    /**
     * Renderers delegated to execute at the end of rendering, used for special parts rendering like arms.
     */
    protected List<IFunctionalRenderer> delegateRenderers = new ArrayList<>();
    /**
     * Model's center point.
     */
    protected @Nullable Vec3d offset = null;
    /**
     * Model's size.
     */
    protected @Nullable Vec2f size = null;

    public BedrockModel(BedrockModelPOJO pojo, BedrockVersion version) {
        if (version == BedrockVersion.LEGACY) {
            loadLegacyModel(pojo);
        }
        if (version == BedrockVersion.NEW) {
            loadNewModel(pojo);
        }
        // 应用发光
        for (ModelRendererWrapper rendererWrapper : modelMap.values()) {
            if (rendererWrapper.modelRenderer().name != null && rendererWrapper.modelRenderer().name.endsWith("_illuminated")) {
                rendererWrapper.modelRenderer().illuminated = true;
            }
        }
    }

    protected BedrockModel() {
    }

    public void delegateRender(IFunctionalRenderer renderer) {
        delegateRenderers.add(renderer);
    }

    private void setRotationAngle(BedrockPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
        modelRenderer.setInitRotationAngle(x, y, z);
    }

    protected void loadNewModel(BedrockModelPOJO pojo) {
        assert pojo.getGeometryModelNew() != null;
        pojo.getGeometryModelNew().deco();
        if (pojo.getGeometryModelNew().getBones() == null) {
            return;
        }
        Description description = pojo.getGeometryModelNew().getDescription();
        // Texture width and height
        int texWidth = description.getTextureWidth();
        int texHeight = description.getTextureHeight();

        List<Float> offset = description.getVisibleBoundsOffset();
        float offsetX = offset.get(0);
        float offsetY = offset.get(1);
        float offsetZ = offset.get(2);
        this.offset = new Vec3d(offsetX, offsetY, offsetZ);
        float width = description.getVisibleBoundsWidth() / 2.0f;
        float height = description.getVisibleBoundsHeight() / 2.0f;
        this.size = new Vec2f(width, height);

        // Inject data into indexBones for subsequent coordinate transformation
        for (BonesItem bones : pojo.getGeometryModelNew().getBones()) {
            // Put index for later coordinate transformation
            indexBones.putIfAbsent(bones.getName(), bones);
            // Put newly created empty BedrockPart instance
            // Empty object is inserted first because parent addition is needed later, then data storage is performed in a second traversal
            modelMap.putIfAbsent(bones.getName(), new ModelRendererWrapper(new BedrockPart(bones.getName())));
        }

        // Start inserting data into ModelRenderer instances
        for (BonesItem bones : pojo.getGeometryModelNew().getBones()) {
            // Bone name
            String name = bones.getName();
            // Rotation, can be null
            @Nullable List<Float> rotation = bones.getRotation();
            // Parent bone name, can be null
            @Nullable String parent = bones.getParent();
            // Model object inserted into HashMap
            BedrockPart model = modelMap.get(name).modelRenderer();

            // Mirror parameters
            model.mirror = bones.isMirror();

            // Rotation point
            model.setPos(convertPivot(bones, 0), convertPivot(bones, 1), convertPivot(bones, 2));

            // Nullable check, set rotation angle
            if (rotation != null) {
                setRotationAngle(model, convertRotation(rotation.get(0)), convertRotation(rotation.get(1)), convertRotation(rotation.get(2)));
            }

            // Null check, bind parent bone
            if (parent != null) {
                BedrockPart parentPart = modelMap.get(parent).modelRenderer();
                parentPart.addChild(model);
                model.parent = parentPart;
            } else {
                // Models without parent bones are rendered
                shouldRender.add(model);
                model.parent = null;
            }

            // Oh my, Cubes can also be null...
            if (bones.getCubes() == null) {
                continue;
            }

            // Insert Cube List
            for (CubesItem cube : bones.getCubes()) {
                List<Float> uv = cube.getUv();
                @Nullable FaceUVsItem faceUv = cube.getFaceUv();
                List<Float> size = cube.getSize();
                @Nullable List<Float> cubeRotation = cube.getRotation();
                boolean mirror = cube.isMirror();
                float inflate = cube.getInflate();

                // Insert as a normal cube
                if (cubeRotation == null) {
                    if (faceUv == null) {
                        model.cubes.add(new BedrockCubeBox(uv.get(0), uv.get(1),
                                convertOrigin(bones, cube, 0), convertOrigin(bones, cube, 1), convertOrigin(bones, cube, 2),
                                size.get(0), size.get(1), size.get(2), inflate, mirror,
                                texWidth, texHeight));
                    } else {
                        model.cubes.add(new BedrockCubePerFace(
                                convertOrigin(bones, cube, 0), convertOrigin(bones, cube, 1), convertOrigin(bones, cube, 2),
                                size.get(0), size.get(1), size.get(2), inflate,
                                texWidth, texHeight, faceUv));
                    }
                }
                // Create Cube ModelRenderer
                else {
                    BedrockPart cubeRenderer = new BedrockPart(null);
                    cubeRenderer.setPos(convertPivot(bones, cube, 0), convertPivot(bones, cube, 1), convertPivot(bones, cube, 2));
                    setRotationAngle(cubeRenderer, convertRotation(cubeRotation.get(0)), convertRotation(cubeRotation.get(1)), convertRotation(cubeRotation.get(2)));
                    if (faceUv == null) {
                        cubeRenderer.cubes.add(new BedrockCubeBox(uv.get(0), uv.get(1),
                                convertOrigin(cube, 0), convertOrigin(cube, 1), convertOrigin(cube, 2),
                                size.get(0), size.get(1), size.get(2), inflate, mirror,
                                texWidth, texHeight));
                    } else {
                        cubeRenderer.cubes.add(new BedrockCubePerFace(
                                convertOrigin(cube, 0), convertOrigin(cube, 1), convertOrigin(cube, 2),
                                size.get(0), size.get(1), size.get(2), inflate,
                                texWidth, texHeight, faceUv));
                    }

                    // Add to parent bone
                    model.addChild(cubeRenderer);
                }
            }
        }
    }

    protected void loadLegacyModel(BedrockModelPOJO pojo) {
        assert pojo.getGeometryModelLegacy() != null;
        pojo.getGeometryModelLegacy().deco();
        if (pojo.getGeometryModelLegacy().getBones() == null) {
            return;
        }

        // Texture width and height
        int texWidth = pojo.getGeometryModelLegacy().getTextureWidth();
        int texHeight = pojo.getGeometryModelLegacy().getTextureHeight();

        List<Float> offset = pojo.getGeometryModelLegacy().getVisibleBoundsOffset();
        float offsetX = offset.get(0);
        float offsetY = offset.get(1);
        float offsetZ = offset.get(2);
        this.offset = new Vec3d(offsetX, offsetY, offsetZ);
        float width = pojo.getGeometryModelLegacy().getVisibleBoundsWidth() / 2.0f;
        float height = pojo.getGeometryModelLegacy().getVisibleBoundsHeight() / 2.0f;
        this.size = new Vec2f(width, height);

        // Inject data into indexBones for subsequent coordinate transformation
        for (BonesItem bones : pojo.getGeometryModelLegacy().getBones()) {
            // Put index for later coordinate transformation
            indexBones.putIfAbsent(bones.getName(), bones);
            // Put newly created empty ModelRenderer instance
            // Empty object is inserted first because parent addition is needed later, then data storage is performed in a second traversal
            modelMap.putIfAbsent(bones.getName(), new ModelRendererWrapper(new BedrockPart(bones.getName())));
        }

        // Start inserting data into ModelRenderer instances
        for (BonesItem bones : pojo.getGeometryModelLegacy().getBones()) {
            // Bone name, note that for animation purposes, certain bone names like head, arms, legs, etc., must be fixed
            String name = bones.getName();
            // Rotation point, can be null
            @Nullable List<Float> rotation = bones.getRotation();
            // Parent bone name, can be null
            @Nullable String parent = bones.getParent();
            // Model object inserted into HashMap
            BedrockPart model = modelMap.get(name).modelRenderer();

            // Mirror parameters
            model.mirror = bones.isMirror();

            // Rotation point
            model.setPos(convertPivot(bones, 0), convertPivot(bones, 1), convertPivot(bones, 2));

            // Nullable check, set rotation angle
            if (rotation != null) {
                setRotationAngle(model, convertRotation(rotation.get(0)), convertRotation(rotation.get(1)), convertRotation(rotation.get(2)));
            }

            // Null check, bind parent bone
            if (parent != null) {
                modelMap.get(parent).modelRenderer().addChild(model);
            } else {
                // Models without parent bones are rendered
                shouldRender.add(model);
            }

            // Oh my, Cubes can also be null...
            if (bones.getCubes() == null) {
                continue;
            }

            // Insert Cube List
            for (CubesItem cube : bones.getCubes()) {
                List<Float> uv = cube.getUv();
                List<Float> size = cube.getSize();
                boolean mirror = cube.isMirror();
                float inflate = cube.getInflate();

                model.cubes.add(new BedrockCubeBox(uv.get(0), uv.get(1),
                        convertOrigin(bones, cube, 0), convertOrigin(bones, cube, 1), convertOrigin(bones, cube, 2),
                        size.get(0), size.get(1), size.get(2), inflate, mirror,
                        texWidth, texHeight));
            }
        }
    }


    /**
     * Calculates the pivot point for rotation in Bedrock Edition, which differs from Java Edition and requires conversion.
     * <p>
     * If there is a parent model:
     * <li>x, z direction: Model's coordinate - Parent model's coordinate
     * <li>y direction: Parent model's coordinate - Model's coordinate
     * <p>
     * If there is no parent model:
     * <li>x, z direction remains unchanged
     * <li>y direction: 24 - Model's coordinate
     *
     * @param bones the BonesItem containing pivot information
     * @param index which axis to calculate: x is 0, y is 1, z is 2
     * @return the converted pivot value based on the given index
     */
    protected float convertPivot(BonesItem bones, int index) {
        if (bones.getParent() != null) {
            if (index == 1) {
                return indexBones.get(bones.getParent()).getPivot().get(index) - bones.getPivot().get(index);
            } else {
                return bones.getPivot().get(index) - indexBones.get(bones.getParent()).getPivot().get(index);
            }
        } else {
            if (index == 1) {
                return 24 - bones.getPivot().get(index);
            } else {
                return bones.getPivot().get(index);
            }
        }
    }

    protected float convertPivot(BonesItem parent, CubesItem cube, int index) {
        assert cube.getPivot() != null;
        if (index == 1) {
            return parent.getPivot().get(index) - cube.getPivot().get(index);
        } else {
            return cube.getPivot().get(index) - parent.getPivot().get(index);
        }
    }

    /**
     * The starting coordinates of blocks differ between Bedrock Edition and Java Edition as well.
     * Java Edition uses relative coordinates, and the y-axis direction is different.
     * Bedrock Edition uses absolute coordinates, and the y-axis direction is upwards.
     * Actually, the rules for both are very simple, but it took me an entire afternoon to understand what was going on.
     * <li>If it is on the x or z axis, only subtract the starting coordinates of the block from the pivot point coordinates.
     * <li>If it is on the y axis, subtract the pivot point coordinates, then subtract the length of the block's y.
     *
     * @param index Which one of xyz it is, x is 0, y is 1, z is 2
     */
    protected float convertOrigin(BonesItem bone, CubesItem cube, int index) {
        if (index == 1) {
            return bone.getPivot().get(index) - cube.getOrigin().get(index) - cube.getSize().get(index);
        } else {
            return cube.getOrigin().get(index) - bone.getPivot().get(index);
        }
    }


    protected float convertOrigin(CubesItem cube, int index) {
        assert cube.getPivot() != null;
        if (index == 1) {
            return cube.getPivot().get(index) - cube.getOrigin().get(index) - cube.getSize().get(index);
        } else {
            return cube.getOrigin().get(index) - cube.getPivot().get(index);
        }
    }

    /**
     * Bedrock Edition uses degrees, while Java Edition uses radians.
     * This method is used to convert degrees to radians.
     */
    protected float convertRotation(float degree) {
        return (float) (degree * Math.PI / 180);
    }

    public BedrockPart getNode(String nodeName) {
        ModelRendererWrapper rendererWrapper = modelMap.get(nodeName);
        if (rendererWrapper != null) {
            return rendererWrapper.modelRenderer();
        } else {
            return null;
        }
    }

    public BonesItem getBone(String name) {
        return indexBones.get(name);
    }

    public void render(MatrixStack matrixStack, ModelTransformationMode transformType, RenderLayer renderType, int light, int overlay) {
        render(matrixStack, transformType, renderType, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(MatrixStack matrixStack, ModelTransformationMode transformType, RenderLayer renderType, int light, int overlay, float red, float green, float blue, float alpha) {
        VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer builder = bufferSource.getBuffer(renderType);

        matrixStack.push();
        for (BedrockPart model : shouldRender) {
            model.render(matrixStack, transformType, builder, light, overlay, red, green, blue, alpha);
        }
        matrixStack.pop();
        if (!IrisCompat.endBatch(bufferSource)) {
            bufferSource.draw(renderType);
        }

        for (IFunctionalRenderer renderer : delegateRenderers) {
            renderer.render(matrixStack, builder, transformType, light, overlay);
        }
        delegateRenderers = new ArrayList<>();
    }

    protected List<BedrockPart> getPath(@Nullable ModelRendererWrapper rendererWrapper) {
        if (rendererWrapper == null) {
            return null;
        }
        BedrockPart part = rendererWrapper.modelRenderer();
        List<BedrockPart> path = new ArrayList<>();
        Stack<BedrockPart> stack = new Stack<>();
        do {
            stack.push(part);
            part = part.getParent();
        } while (part != null);
        while (!stack.isEmpty()) {
            part = stack.pop();
            path.add(part);
        }
        return path;
    }

    @Nullable
    public Vec3d getOffset() {
        return offset;
    }

    @Nullable
    public Vec2f getSize() {
        return size;
    }

    public List<BedrockPart> getShouldRender() {
        return shouldRender;
    }

    public HashMap<String, BonesItem> getIndexBones() {
        return indexBones;
    }
}
