package com.tacz.guns.api.client.animation.gltf.accessor;

import com.tacz.guns.api.client.animation.gltf.GltfConstants;

public class Accessors {
    /**
     * Private constructor to prevent instantiation
     */
    private Accessors() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the number of components that one element has for the given
     * accessor type. Valid parameters are
     * <pre><code>
     * "SCALAR" :  1
     * "VEC2"   :  2
     * "VEC3"   :  3
     * "VEC4"   :  4
     * "MAT2"   :  4
     * "MAT3"   :  9
     * "MAT4"   : 16
     * </code></pre>
     *
     * @param accessorType The accessor type.
     * @return The number of components
     * @throws IllegalArgumentException If the given type is none of the
     *                                  valid parameters
     */
    public static int getNumComponentsForAccessorType(String accessorType) {
        return switch (accessorType) {
            case "SCALAR" -> 1;
            case "VEC2" -> 2;
            case "VEC3" -> 3;
            case "VEC4", "MAT2" -> 4;
            case "MAT3" -> 9;
            case "MAT4" -> 16;
            default -> throw new IllegalArgumentException(
                    "Invalid accessor type: " + accessorType);
        };
    }

    /**
     * Returns the number of bytes that one component with the given
     * accessor component type consists of.
     * Valid parameters are
     * <pre><code>
     * GL_BYTE           : 1
     * GL_UNSIGNED_BYTE  : 1
     * GL_SHORT          : 2
     * GL_UNSIGNED_SHORT : 2
     * GL_INT            : 4
     * GL_UNSIGNED_INT   : 4
     * GL_FLOAT          : 4
     * </code></pre>
     *
     * @param componentType The component type
     * @return The number of bytes
     * @throws IllegalArgumentException If the given type is none of the
     *                                  valid parameters
     */
    public static int getNumBytesForAccessorComponentType(int componentType) {
        return switch (componentType) {
            case GltfConstants.GL_BYTE, GltfConstants.GL_UNSIGNED_BYTE -> 1;
            case GltfConstants.GL_SHORT, GltfConstants.GL_UNSIGNED_SHORT -> 2;
            case GltfConstants.GL_INT, GltfConstants.GL_UNSIGNED_INT, GltfConstants.GL_FLOAT -> 4;
            default -> throw new IllegalArgumentException(
                    "Invalid accessor component type: " + componentType);
        };
    }

    /**
     * Returns the data type for the given accessor component type.
     * Valid parameters and their return values are
     * <pre><code>
     * GL_BYTE           : byte.class
     * GL_UNSIGNED_BYTE  : byte.class
     * GL_SHORT          : short.class
     * GL_UNSIGNED_SHORT : short.class
     * GL_INT            : int.class
     * GL_UNSIGNED_INT   : int.class
     * GL_FLOAT          : float.class
     * </code></pre>
     *
     * @param componentType The component type
     * @return The data type
     * @throws IllegalArgumentException If the given type is none of the
     *                                  valid parameters
     */
    public static Class<?> getDataTypeForAccessorComponentType(
            int componentType) {
        return switch (componentType) {
            case GltfConstants.GL_BYTE, GltfConstants.GL_UNSIGNED_BYTE -> byte.class;
            case GltfConstants.GL_SHORT, GltfConstants.GL_UNSIGNED_SHORT -> short.class;
            case GltfConstants.GL_INT, GltfConstants.GL_UNSIGNED_INT -> int.class;
            case GltfConstants.GL_FLOAT -> float.class;
            default -> throw new IllegalArgumentException(
                    "Invalid accessor component type: " + componentType);
        };
    }
}
