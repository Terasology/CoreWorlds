// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facets;

import org.terasology.world.block.BlockRegion;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseFieldFacet2D;

/**
 * Modifies the amount of noise to be added to the surface shape, to determine the amount of lumps and cliffs and things.
 */
public class SurfaceRoughnessFacet extends BaseFieldFacet2D {
    public SurfaceRoughnessFacet(BlockRegion targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
