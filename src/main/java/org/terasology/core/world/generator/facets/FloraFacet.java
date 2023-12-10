// Copyright 2014 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.facets;

import org.terasology.core.world.generator.rasterizers.FloraType;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.SparseObjectFacet3D;

/**
 * Stores where plants can be placed
 */
public class FloraFacet extends SparseObjectFacet3D<FloraType> {

    public FloraFacet(BlockRegion targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
