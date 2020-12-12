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
public class SimplexRoughnessProvider implements FacetProvider {
    private static final int SAMPLE_RATE = 4;

    private Noise noise;

    @Override
    public void setSeed(long seed) {
        noise = new BrownianNoise(new SimplexNoise(seed + 92658), 3);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet elevationFacet = region.getRegionFacet(ElevationFacet.class);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        SurfaceRoughnessFacet facet = new SurfaceRoughnessFacet(region.getRegion(), region.getBorderForFacet(SurfaceRoughnessFacet.class));

        for (BaseVector2i pos : facet.getWorldRegion().contents()) {
            float height = elevationFacet.getWorld(pos) - seaLevelFacet.getSeaLevel();
            float value = 0.25f + height * 0.007f + noise.noise(pos.x() / 500f, pos.y() / 500f) * 1.5f;
            facet.setWorld(pos, value);
        }

        region.setRegionFacet(SurfaceRoughnessFacet.class, facet);
    }
}
