/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2ic;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.DensityFacet;
import org.terasology.world.generation.facets.ElevationFacet;
import org.terasology.world.generation.facets.SurfacesFacet;

/**
 * Sets density based on its distance from the surface.
 * Also sets the BlockHeightsFacet at the same time, because it should be kept synchronised with the DensityFacet.
 */
@Requires(@Facet(ElevationFacet.class))
@Produces({DensityFacet.class, SurfacesFacet.class})
public class SurfaceToDensityProvider implements FacetProvider {

    @Override
    public void setSeed(long seed) {

    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet elevation = region.getRegionFacet(ElevationFacet.class);
        DensityFacet densityFacet = new DensityFacet(region.getRegion(), region.getBorderForFacet(DensityFacet.class));
        SurfacesFacet surfacesFacet = new SurfacesFacet(region.getRegion(), region.getBorderForFacet(SurfacesFacet.class));

        BlockRegion area = region.getRegion();
        BlockArea densityRect = new BlockArea(densityFacet.getRelativeRegion().minX(), densityFacet.getRelativeRegion().minZ(),
                densityFacet.getRelativeRegion().maxX(), densityFacet.getRelativeRegion().maxZ());
        for (Vector2ic pos : densityRect) {
            float height = elevation.get(pos);
            for (int y = densityFacet.getRelativeRegion().minY(); y <= densityFacet.getRelativeRegion().maxY(); ++y) {
                densityFacet.set(pos.x(), y, pos.y(), height - area.minY() - y);
            }
        }
        region.setRegionFacet(DensityFacet.class, densityFacet);

        BlockArea surfaceRect = new BlockArea(surfacesFacet.getWorldRegion().minX(), surfacesFacet.getWorldRegion().minZ(),
                surfacesFacet.getWorldRegion().maxX(), surfacesFacet.getWorldRegion().maxZ());
        for (Vector2ic pos : surfaceRect) {
            // Round in this odd way because if the elevation is precisely an integer, the block at that level has density 0, so it's air.
            int height = (int) Math.ceil(elevation.getWorld(pos)) - 1;
            if (height >= surfacesFacet.getWorldRegion().minY() && height <= surfacesFacet.getWorldRegion().maxY()) {
                surfacesFacet.setWorld(pos.x(), height, pos.y(), true);
            }
        }
        region.setRegionFacet(SurfacesFacet.class, surfacesFacet);
    }
}
