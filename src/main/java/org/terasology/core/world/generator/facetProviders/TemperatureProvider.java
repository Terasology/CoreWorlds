/*
 * Copyright 2020 MovingBlocks
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

import org.terasology.core.world.generator.facets.TreeFacet;
import org.terasology.entitySystem.Component;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.PerlinNoise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.ConfigurableFacetProvider;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generation.facets.SurfaceHumidityFacet;
import org.terasology.world.generation.facets.SurfaceTemperatureFacet;

@Updates(@Facet(SurfaceTemperatureFacet.class))

public class TemperatureProvider implements ConfigurableFacetProvider {
    private TemperatureConfiguration configuration = new TemperatureConfiguration();
    private Noise temperatureNoise;

    @Override
    public void setSeed(long seed) {
        temperatureNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 2), 8), new Vector2f(0.001f, 0.001f), 1);
    }

    @Override
    public String getConfigurationName() {
        return "Temperature";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (TemperatureConfiguration)configuration;
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceTemperatureFacet facet = region.getRegionFacet(SurfaceTemperatureFacet.class);
        SurfaceHeightFacet heightFacet = region.getRegionFacet(SurfaceHeightFacet.class);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        float temperatureBase = configuration.temperatureBase;

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            // modify initial noise so that it falls (almost always) in range and its average is approximately temperatureBase
            float noiseAdjusted = temperatureNoise.noise(position.x(), position.y()) / 4 + temperatureBase;
            noiseAdjusted += -(heightFacet.getWorld(position) - seaLevelFacet.getSeaLevel()) * .00006f + .07f;

            // clamp to reasonable values
            noiseAdjusted = TeraMath.clamp(noiseAdjusted, -.6f, .5f);
            facet.setWorld(position, noiseAdjusted);
        }
    }

    private static class TemperatureConfiguration implements Component
    {
        @Range(min = -.4f, max = .4f, increment = .001f, precision = 1, description = "Mountain Height")
        private float temperatureBase = .22f;
    }

}
