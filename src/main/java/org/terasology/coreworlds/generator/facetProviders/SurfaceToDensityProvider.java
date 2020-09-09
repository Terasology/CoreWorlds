// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.facetProviders;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.DensityFacet;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;

/**
 * Sets density based on its distance from the surface
 */
@Requires(@Facet(SurfaceHeightFacet.class))
@Produces(DensityFacet.class)
public class SurfaceToDensityProvider implements FacetProvider {

    @Override
    public void setSeed(long seed) {

    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceHeightFacet surfaceHeight = region.getRegionFacet(SurfaceHeightFacet.class);
        DensityFacet facet = new DensityFacet(region.getRegion(), region.getBorderForFacet(DensityFacet.class));

        Region3i area = region.getRegion();
        Rect2i rect = Rect2i.createFromMinAndMax(facet.getRelativeRegion().minX(), facet.getRelativeRegion().minZ(),
                facet.getRelativeRegion().maxX(), facet.getRelativeRegion().maxZ());
        for (BaseVector2i pos : rect.contents()) {
            float height = surfaceHeight.get(pos);
            for (int y = facet.getRelativeRegion().minY(); y <= facet.getRelativeRegion().maxY(); ++y) {
                facet.set(pos.x(), y, pos.y(), height - area.minY() - y);
            }
        }
        region.setRegionFacet(DensityFacet.class, facet);
    }
}
