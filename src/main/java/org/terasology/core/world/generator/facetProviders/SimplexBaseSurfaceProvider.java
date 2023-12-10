// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2f;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.ScalableFacetProvider;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;

@Produces(ElevationFacet.class)
@Requires(@Facet(SeaLevelFacet.class))
public class SimplexBaseSurfaceProvider implements ScalableFacetProvider {
    private static final int SAMPLE_RATE = 4;
    private static final float BEACH_STEEPNESS = 0.05f;
    private static final float OCEAN_FLOOR_CUTOFF = 0.1f;

    private SubSampledNoise surfaceNoise;

    @Override
    public void setSeed(long seed) {
        float spawnHeight = -1;
        long currentSeed = (seed % 2 == 0) ? seed - 1 : seed;
        while (spawnHeight < 0 || spawnHeight > 0.2) {
            BrownianNoise source = new BrownianNoise(new SimplexNoise(currentSeed), 8);
            surfaceNoise = new SubSampledNoise(source, new Vector2f(0.0002f, 0.0002f), SAMPLE_RATE);
            spawnHeight = surfaceNoise.noise(0, 0);
            currentSeed *= 3;
        }
    }

    @Override
    public void process(GeneratingRegion region, float scale) {
        Border3D border = region.getBorderForFacet(ElevationFacet.class);
        ElevationFacet facet = new ElevationFacet(region.getRegion(), border);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        float seaLevel = seaLevelFacet.getSeaLevel();
        float[] noise = surfaceNoise.noise(facet.getWorldArea(), scale);

        for (int i = 0; i < noise.length; ++i) {
            if (noise[i] > 0) {
                noise[i] = seaLevel + noise[i] * (noise[i] + BEACH_STEEPNESS) * 1000;
            } else if (noise[i] > -OCEAN_FLOOR_CUTOFF) {
                noise[i] /= OCEAN_FLOOR_CUTOFF;
                noise[i] = (noise[i] + 1) * (noise[i] + 1) * seaLevel;
            } else {
                noise[i] = 0;
            }
        }

        facet.set(noise);
        region.setRegionFacet(ElevationFacet.class, facet);
    }
}
