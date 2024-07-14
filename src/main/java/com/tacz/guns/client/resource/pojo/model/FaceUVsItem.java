package com.tacz.guns.client.resource.pojo.model;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.math.Direction;

public class FaceUVsItem {
    @SerializedName("down")
    private FaceItem down;
    @SerializedName("east")
    private FaceItem east;
    @SerializedName("north")
    private FaceItem north;
    @SerializedName("south")
    private FaceItem south;
    @SerializedName("up")
    private FaceItem up;
    @SerializedName("west")
    private FaceItem west;

    public static FaceUVsItem singleSouthFace() {
        FaceUVsItem faces = new FaceUVsItem();
        faces.north = FaceItem.EMPTY;
        faces.east = FaceItem.EMPTY;
        faces.west = FaceItem.EMPTY;
        faces.south = FaceItem.single16X();
        faces.up = FaceItem.EMPTY;
        faces.down = FaceItem.EMPTY;
        return faces;
    }

    public FaceItem getFace(Direction direction) {
        return switch (direction) {
            case EAST -> west == null ? FaceItem.EMPTY : west;
            case WEST -> east == null ? FaceItem.EMPTY : east;
            case NORTH -> north == null ? FaceItem.EMPTY : north;
            case SOUTH -> south == null ? FaceItem.EMPTY : south;
            case UP -> down == null ? FaceItem.EMPTY : down;
            default -> up == null ? FaceItem.EMPTY : up;
        };
    }
}
