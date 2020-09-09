// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.facetProviders;

import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;

/**
 * @deprecated Prefer using {@link SimplexBaseSurfaceProvider}.
 */
@Deprecated
@Produces(SurfaceHeightFacet.class)
@Requires(@Facet(SeaLevelFacet.class))
public class PerlinBaseSurfaceProvider implements FacetProvider {
    private static final int SAMPLE_RATE = 4;

    private SubSampledNoise surfaceNoise;

    @Override
    public void setSeed(long seed) {
        BrownianNoise source = new BrownianNoise(new PerlinNoise(seed), 8);
        surfaceNoise = new SubSampledNoise(source, new Vector2f(0.004f, 0.004f), SAMPLE_RATE);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        float seaLevel = seaLevelFacet.getSeaLevel();
        Rect2i processRegion = facet.getWorldRegion();
        float[] noise = surfaceNoise.noise(processRegion);

        for (int i = 0; i < noise.length; ++i) {
            noise[i] = seaLevel + seaLevel * ((noise[i] * 2.11f + 1f) / 2f);
        }

        facet.set(noise);
        region.setRegionFacet(SurfaceHeightFacet.class, facet);
    }
}
