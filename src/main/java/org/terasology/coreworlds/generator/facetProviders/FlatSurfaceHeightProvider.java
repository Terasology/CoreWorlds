// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.facetProviders;

import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.geom.BaseVector2i;

@Produces(SurfaceHeightFacet.class)
@Requires(@Facet(SeaLevelFacet.class))
public class FlatSurfaceHeightProvider implements FacetProvider {
    private final int height;

    public FlatSurfaceHeightProvider(int height) {
        this.height = height;
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(),
                region.getBorderForFacet(SurfaceHeightFacet.class));

        for (BaseVector2i pos : facet.getRelativeRegion().contents()) {
            facet.set(pos, height);
        }

        region.setRegionFacet(SurfaceHeightFacet.class, facet);
    }
}
