// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2f;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.ScalableFacetProvider;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.nui.properties.Range;

/**
 * Applies an amount of the max depth for regions that are rivers
 */
@Updates(@Facet(ElevationFacet.class))
public class SimplexRiverProvider implements ScalableFacetProvider, ConfigurableFacetProvider {
    private static final int SAMPLE_RATE = 4;

    private SubSampledNoise riverNoise;
    private SimplexRiverProviderConfiguration configuration = new SimplexRiverProviderConfiguration();

    @Override
    public void setSeed(long seed) {
        riverNoise = new SubSampledNoise(new BrownianNoise(new SimplexNoise(seed + 2), 8), new Vector2f(0.0008f, 0.0008f), SAMPLE_RATE);
    }

    @Override
    public void process(GeneratingRegion region, float scale) {
        if (scale > 20) {
            // The scale is so large that rivers wouldn't be visible anyway.
            return;
        }
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        float[] noise = riverNoise.noise(facet.getWorldArea(), scale);

        float[] surfaceHeights = facet.getInternal();
        for (int i = 0; i < noise.length; ++i) {
            surfaceHeights[i] += configuration.maxDepth * Math.min(0, Math.abs(noise[i]) * 20f - 1);
        }
    }

    @Override
    public String getConfigurationName() {
        return "Rivers";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (SimplexRiverProviderConfiguration) configuration;
    }

    public static class SimplexRiverProviderConfiguration implements Component<SimplexRiverProviderConfiguration> {
        @Range(min = 0, max = 64f, increment = 1f, precision = 0, description = "River Depth")
        public float maxDepth = 16;

        @Override
        public void copyFrom(SimplexRiverProviderConfiguration other) {
            this.maxDepth = other.maxDepth;
        }
    }
}
