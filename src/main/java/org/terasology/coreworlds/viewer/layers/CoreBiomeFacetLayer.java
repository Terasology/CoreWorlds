// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.coreworlds.viewer.layers;

import org.terasology.biomesAPI.Biome;
import org.terasology.coreworlds.CoreBiome;
import org.terasology.coreworlds.generator.facets.BiomeFacet;
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
