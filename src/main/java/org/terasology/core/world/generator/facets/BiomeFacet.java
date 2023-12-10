// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.facets;

import org.terasology.biomesAPI.Biome;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseObjectFacet2D;

/**
 */
public class BiomeFacet extends BaseObjectFacet2D<Biome> {
    public BiomeFacet(BlockRegion targetRegion, Border3D border) {
        super(targetRegion, border, Biome.class);
    }
}
