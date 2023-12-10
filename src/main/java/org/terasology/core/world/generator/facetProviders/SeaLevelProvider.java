// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.facetProviders;

import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.ScalableFacetProvider;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;

/**
 */
@Produces(SeaLevelFacet.class)
public class SeaLevelProvider implements ScalableFacetProvider {

    private int seaLevel;

    public SeaLevelProvider() {
        seaLevel = 32;
    }

    public SeaLevelProvider(int seaLevel) {
        this.seaLevel = seaLevel;
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region, float scale) {
        Border3D border = region.getBorderForFacet(SeaLevelFacet.class);
        SeaLevelFacet facet = new SeaLevelFacet(region.getRegion(), border);
        facet.setSeaLevel(seaLevel);
        region.setRegionFacet(SeaLevelFacet.class, facet);
    }
}
