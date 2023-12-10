// Copyright 2015 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.viewer.layers;

import org.terasology.biomesAPI.Biome;
import org.terasology.core.world.CoreBiome;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.engine.world.viewer.layers.NominalFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;

/**
 * Maps {@link CoreBiome} facet to corresponding colors.
 */
@Renders(value = BiomeFacet.class, order = ZOrder.BIOME)
public class CoreBiomeFacetLayer extends NominalFacetLayer<Biome> {

    public CoreBiomeFacetLayer() {
        super(BiomeFacet.class, new CoreBiomeColors());
    }
}
