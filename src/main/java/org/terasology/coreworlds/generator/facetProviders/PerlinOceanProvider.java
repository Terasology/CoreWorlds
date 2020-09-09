// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.facetProviders;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector2f;
import org.terasology.nui.properties.Range;

/**
 * Applies an amount of the max depth for regions that are oceans
 *
 * @deprecated Prefer using {@link SimplexOceanProvider}.
 */
@Deprecated
@Updates(@Facet(SurfaceHeightFacet.class))
public class PerlinOceanProvider implements ConfigurableFacetProvider {
    private static final int SAMPLE_RATE = 4;

    private SubSampledNoise oceanNoise;
    private PerlinOceanConfiguration configuration = new PerlinOceanConfiguration();

    @Override
    public void setSeed(long seed) {
        oceanNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 1), 8), new Vector2f(0.0009f,
                0.0009f), SAMPLE_RATE);
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceHeightFacet facet = region.getRegionFacet(SurfaceHeightFacet.class);
        float[] noise = oceanNoise.noise(facet.getWorldRegion());

        float[] surfaceHeights = facet.getInternal();
        for (int i = 0; i < noise.length; ++i) {
            surfaceHeights[i] -= configuration.maxDepth * TeraMath.clamp(noise[i] * 8.0f * 2.11f + 0.25f);
        }
    }

    @Override
    public String getConfigurationName() {
        return "Oceans";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (PerlinOceanConfiguration) configuration;
    }

    private static class PerlinOceanConfiguration implements Component {
        @Range(min = 0, max = 128f, increment = 1f, precision = 0, description = "Ocean Depth")
        public float maxDepth = 32;
    }
}
