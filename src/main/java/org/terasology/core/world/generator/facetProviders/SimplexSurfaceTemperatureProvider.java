// Copyright 2014 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2f;
import org.terasology.math.TeraMath;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.ScalableFacetProvider;
import org.terasology.engine.world.generation.facets.SurfaceTemperatureFacet;

@Produces(SurfaceTemperatureFacet.class)
public class SimplexSurfaceTemperatureProvider implements ScalableFacetProvider {
    private static final int SAMPLE_RATE = 4;

    private SubSampledNoise temperatureNoise;

    @Override
    public void setSeed(long seed) {
        temperatureNoise = new SubSampledNoise(new BrownianNoise(new SimplexNoise(seed + 5), 8), new Vector2f(0.0005f, 0.0005f), SAMPLE_RATE);
    }

    @Override
    public void process(GeneratingRegion region, float scale) {
        SurfaceTemperatureFacet facet = new SurfaceTemperatureFacet(region.getRegion(), region.getBorderForFacet(SurfaceTemperatureFacet.class));
        float[] noise = this.temperatureNoise.noise(facet.getWorldArea(), scale);

        for (int i = 0; i < noise.length; ++i) {
            noise[i] = TeraMath.clamp((noise[i] * 2.11f + 1f) * 0.5f);
        }

        facet.set(noise);
        region.setRegionFacet(SurfaceTemperatureFacet.class, facet);
    }
}
