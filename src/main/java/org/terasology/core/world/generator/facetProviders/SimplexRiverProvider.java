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
import org.terasology.engine.entitySystem.Component;
import org.terasology.nui.properties.Range;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.ScalableFacetProvider;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;

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

    private static class SimplexRiverProviderConfiguration implements Component {
        @Range(min = 0, max = 64f, increment = 1f, precision = 0, description = "River Depth")
        public float maxDepth = 16;
    }
}
