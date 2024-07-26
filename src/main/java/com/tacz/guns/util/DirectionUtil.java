package com.tacz.guns.util;

import net.minecraft.util.math.Direction;

public class DirectionUtil {

    public static Direction getLeft(Direction facing) {
        Direction left = getLeftNull(facing);
        if (left != null) {
            return left;
        }

        return facing;
    }

    public static Direction getLeftNull(Direction facing) {
        if (facing == Direction.NORTH)  {
            return Direction.WEST;
        } else if (facing == Direction.WEST) {
            return Direction.SOUTH;
        } else if (facing == Direction.SOUTH) {
            return Direction.EAST;
        } else if (facing == Direction.EAST) {
            return Direction.NORTH;
        }

        return null;
    }

    public static Direction getRight(Direction facing) {
        Direction right = getRightNull(facing);
        if (right != null) {
            return right;
        }

        return facing;
    }

    public static Direction getRightNull(Direction facing) {
        if (facing == Direction.NORTH)  {
            return Direction.EAST;
        } else if (facing == Direction.WEST) {
            return Direction.NORTH;
        } else if (facing == Direction.SOUTH) {
            return Direction.WEST;
        } else if (facing == Direction.EAST) {
            return Direction.SOUTH;
        }

        return null;
    }
}
