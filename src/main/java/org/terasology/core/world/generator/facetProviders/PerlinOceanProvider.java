// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2f;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.math.TeraMath;
import org.terasology.nui.properties.Range;

/**
 * Applies an amount of the max depth for regions that are oceans
 */
@Deprecated
@Updates(@Facet(ElevationFacet.class))
public class PerlinOceanProvider implements ConfigurableFacetProvider {
    private static final int SAMPLE_RATE = 4;

    private SubSampledNoise oceanNoise;
    private PerlinOceanConfiguration configuration = new PerlinOceanConfiguration();

    @Override
    public void setSeed(long seed) {
        oceanNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 1), 8), new Vector2f(0.0009f, 0.0009f), SAMPLE_RATE);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        float[] noise = oceanNoise.noise(facet.getWorldArea());

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

    private static class PerlinOceanConfiguration implements Component<PerlinOceanConfiguration> {
        @Range(min = 0, max = 128f, increment = 1f, precision = 0, description = "Ocean Depth")
        public float maxDepth = 32;

        @Override
        public void copy(PerlinOceanConfiguration other) {
            this.maxDepth = other.maxDepth;
        }
    }
}
