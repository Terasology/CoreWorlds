// Copyright 2014 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2ic;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.facets.ElevationFacet;

@Produces(ElevationFacet.class)
public class FlatSurfaceHeightProvider implements FacetProvider {
    private int height;

    public FlatSurfaceHeightProvider(int height) {
        this.height = height;
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = new ElevationFacet(region.getRegion(), region.getBorderForFacet(ElevationFacet.class));

        for (Vector2ic pos : facet.getRelativeArea()) {
            facet.set(pos, height);
        }

        region.setRegionFacet(ElevationFacet.class, facet);
    }
}
