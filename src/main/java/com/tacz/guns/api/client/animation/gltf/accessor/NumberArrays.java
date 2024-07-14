package com.tacz.guns.api.client.animation.gltf.accessor;

/**
 * Methods to convert primitive arrays to arrays of Number objects
 */
class NumberArrays {
    /**
     * Private constructor to prevent instantiation
     */
    private NumberArrays() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert the given array into a Number array
     *
     * @param array The array
     * @return The result
     */
    static Number[] asNumbers(int array[]) {
        Number result[] = new Number[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    /**
     * Convert the given array into a Number array
     *
     * @param array The array
     * @return The result
     */
    static Number[] asNumbers(long array[]) {
        Number result[] = new Number[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    /**
     * Convert the given array into a Number array
     *
     * @param array The array
     * @return The result
     */
    static Number[] asNumbers(float array[]) {
        Number result[] = new Number[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }
}
