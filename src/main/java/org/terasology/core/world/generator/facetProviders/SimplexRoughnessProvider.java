// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2ic;
import org.terasology.core.world.generator.facets.SurfaceRoughnessFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.ScalableFacetProvider;
import org.terasology.world.generation.facets.ElevationFacet;
import org.terasology.world.generation.facets.SeaLevelFacet;

/**
 * Determines the surface roughness. Generally, higher areas are rougher, but there's some noise too.
 */
@Produces(SurfaceRoughnessFacet.class)
@Requires({
    @Facet(ElevationFacet.class),
    @Facet(SeaLevelFacet.class)
})
public class SimplexRoughnessProvider implements ScalableFacetProvider {
    private static final int SAMPLE_RATE = 4;

    private Noise noise;

    @Override
    public void setSeed(long seed) {
        noise = new BrownianNoise(new SimplexNoise(seed + 92658), 3);
    }

    @Override
    public void process(GeneratingRegion region, float scale) {
        ElevationFacet elevationFacet = region.getRegionFacet(ElevationFacet.class);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        SurfaceRoughnessFacet facet = new SurfaceRoughnessFacet(region.getRegion(), region.getBorderForFacet(SurfaceRoughnessFacet.class));

        for (Vector2ic pos : facet.getWorldArea()) {
            float height = elevationFacet.getWorld(pos) - seaLevelFacet.getSeaLevel();
            float value = 0.25f + height * 0.007f + noise.noise(pos.x() * scale / 500f, pos.y() * scale / 500f) * 1.5f;
            facet.setWorld(pos, value);
        }

        region.setRegionFacet(SurfaceRoughnessFacet.class, facet);
    }
}
