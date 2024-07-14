package com.tacz.guns.api.client.animation.gltf.accessor;

import java.nio.ByteBuffer;

/**
 * Interface for classes that provide typed access to raw accessor data.
 * The exact type of the data (and thus, the implementing class) is
 * defined by the {@link #getComponentType() component type}:<br>
 * <ul>
 *   <li>For <code>byte.class</code>, the implementation is an
 *   {@link AccessorByteData}</li>
 *   <li>For <code>short.class</code>, the implementation is an
 *   {@link AccessorShortData}</li>
 *   <li>For <code>int.class</code>, the implementation is an
 *   {@link AccessorIntData}</li>
 *   <li>For <code>float.class</code>, the implementation is an
 *   {@link AccessorFloatData}</li>
 * </ul>
 */
public interface AccessorData {
    /**
     * Returns the type of the components that this class provides access to.
     * This will usually be a primitive type, like <code>float.class</code>
     * or <code>short.class</code>.
     *
     * @return The component type
     */
    Class<?> getComponentType();

    /**
     * Returns the number of elements in this data (for example, the number
     * of 3D vectors)
     *
     * @return The number of elements
     */
    int getNumElements();

    /**
     * Returns the number of components per element (for example, 3 if the
     * elements are 3D vectors)
     *
     * @return The number of components per element
     */
    int getNumComponentsPerElement();

    /**
     * Returns the total number of components (that is, the number of elements
     * multiplied with the number of components per element)
     *
     * @return The total number of components
     */
    int getTotalNumComponents();

    /**
     * Creates a new, direct byte buffer (with native byte order) that
     * contains the data for the accessor, in a compact form,
     * without any offset, and without any additional stride (that is,
     * all elements will be tightly packed).
     *
     * @return The byte buffer
     */
    ByteBuffer createByteBuffer();

}
