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

import org.joml.Vector2f;
import org.terasology.math.geom.Rect2i;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.block.BlockAreac;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.ElevationFacet;
import org.terasology.world.generation.facets.SeaLevelFacet;

/**
 */
@Produces(ElevationFacet.class)
@Requires(@Facet(SeaLevelFacet.class))
public class SimplexBaseSurfaceProvider implements FacetProvider {
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
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ElevationFacet.class);
        ElevationFacet facet = new ElevationFacet(region.getRegion(), border);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        float seaLevel = seaLevelFacet.getSeaLevel();
        BlockAreac processRegion = facet.getWorldRegion();
        float[] noise = surfaceNoise.noise(processRegion);

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
