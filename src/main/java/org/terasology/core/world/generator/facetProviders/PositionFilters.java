/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.core.world.generator.facetProviders;

import com.google.common.base.Predicate;
import org.joml.Vector3i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.world.generation.facets.DensityFacet;
import org.terasology.world.generation.facets.SurfacesFacet;

import java.util.Set;

/**
 * A collection of filters that restrict the placement of objects
 *
 */
public final class PositionFilters {

    private PositionFilters() {
        // no instances
    }

    /**
     * Filters based on the vector's y value
     *
     * @return a predicate that returns true only if (y &gt; height)
     */
    public static Predicate<Vector3i> minHeight(final int height) {
        return heightRange(height, Integer.MAX_VALUE);
    }

    /**
     * Filters based on the vector's y value
     *
     * @return a predicate that returns true only if (y &lt; height)
     */
    public static Predicate<Vector3i> maxHeight(final int height) {
        return heightRange(Integer.MIN_VALUE, height);
    }

    /**
     * Filters based on the vector's y value
     *
     * @return a predicate that returns true only if (y &gt; minHeight) and (y &lt; maxHeight)
     */
    public static Predicate<Vector3i> heightRange(final int minHeight, final int maxHeight) {
        return input -> {
            int y = input.y();
            return y > minHeight && y < maxHeight;
        };
    }

    /**
     * Filters based on the density
     *
     * @param density the density facet that contains all tested coords.
     * @return a predicate that returns true if (density <= 0) and (density > 0) for the block at (y - 1)
     */
    public static Predicate<Vector3i> density(final DensityFacet density) {
        return input -> {
            // pass if the block on the surface is dense enough
            float densBelow = density.getWorld(input.x(), input.y() - 1, input.z());
            float densThis = density.getWorld(input);
            return (densBelow > 0 && densThis <= 0);
        };
    }

    /**
     * Filters based on surface flatness
     *
     * @param surfaceFacet the surface facet that contains all tested coords.
     * @return a predicate that returns true only if there is a level surface in adjacent directions
     */
    public static Predicate<Vector3i> flatness(final SurfacesFacet surfaceFacet) {
        return flatness(surfaceFacet, 0, 0);
    }

    /**
     * Filters based on surface flatness
     *
     * @param surfaceFacet the surface facet that contains all tested coords.
     * @param divUp        surface can be higher up to <code>divUp</code>.
     * @param divDown      surface can be lower up to <code>divDown</code>.
     * @return a predicate that returns true only if there is a level surface in adjacent directions
     */
    public static Predicate<Vector3i> flatness(final SurfacesFacet surfaceFacet, final int divUp, final int divDown) {

        return new Predicate<Vector3i>() {

            @Override
            public boolean apply(Vector3i input) {
                int x = input.x();
                int z = input.z();
                int level = input.y() - 1;
                int min = level - divDown;
                int max = level + divUp;

                return inBounds(surfaceFacet.getWorldColumn(x - 1, z), min, max)
                        && inBounds(surfaceFacet.getWorldColumn(x + 1, z), min, max)
                        && inBounds(surfaceFacet.getWorldColumn(x, z - 1), min, max)
                        && inBounds(surfaceFacet.getWorldColumn(x, z + 1), min, max);
            }

            private boolean inBounds(Set<Integer> heights, int min, int max) {
                for (int height : heights) {
                    if (height >= min && height <= max) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filters based on a random noise
     *
     * @param noiseGen the noise generator that produces noise in [0..1]
     * @param density  the threshold in [0..1]
     * @return true if the noise value is <b>below</b> the threshold
     */
    public static Predicate<Vector3i> probability(final Noise noiseGen, final float density) {
        return input -> Math.abs(noiseGen.noise(input.x(), input.y(), input.z())) < density;
    }

}
