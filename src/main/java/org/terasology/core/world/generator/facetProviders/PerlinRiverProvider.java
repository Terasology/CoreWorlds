// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2f;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.math.TeraMath;
import org.terasology.nui.properties.Range;

/**
 * Applies an amount of the max depth for regions that are rivers
 * @deprecated Prefer using {@link SimplexRiverProvider}.
 */
@Deprecated
@Updates(@Facet(ElevationFacet.class))
public class PerlinRiverProvider implements FacetProvider, ConfigurableFacetProvider {
    private static final int SAMPLE_RATE = 4;

    private SubSampledNoise riverNoise;
    private PerlinRiverProviderConfiguration configuration = new PerlinRiverProviderConfiguration();

    @Override
    public void setSeed(long seed) {
        riverNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 2), 8), new Vector2f(0.0008f, 0.0008f), SAMPLE_RATE);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        float[] noise = riverNoise.noise(facet.getWorldArea());

        float[] surfaceHeights = facet.getInternal();
        for (int i = 0; i < noise.length; ++i) {
            surfaceHeights[i] += configuration.maxDepth * TeraMath.clamp(7f * (TeraMath.sqrt(Math.abs(noise[i] * 2.11f)) - 0.1f) + 0.25f);
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
        this.configuration = (PerlinRiverProviderConfiguration) configuration;
    }

    private static class PerlinRiverProviderConfiguration implements Component<PerlinRiverProviderConfiguration> {
        @Range(min = 0, max = 64f, increment = 1f, precision = 0, description = "River Depth")
        public float maxDepth = 16;

        @Override
        public void copy(PerlinRiverProviderConfiguration other) {
            this.maxDepth = other.maxDepth;
        }
    }
}
