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

import org.slf4j.LoggerFactory;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generation.facets.SurfaceTemperatureFacet;

/**
 */
@Produces(SurfaceTemperatureFacet.class)
public class SimplexSurfaceTemperatureProvider implements FacetProvider {
    private static final int SAMPLE_RATE = 4;
    private static final float TEMPERATURE_BASE = .25f;

    private SubSampledNoise temperatureNoise;

    @Override
    public void setSeed(long seed) {
        temperatureNoise = new SubSampledNoise(new BrownianNoise(new SimplexNoise(seed + 5), 8), new Vector2f(0.0005f, 0.0005f), SAMPLE_RATE);
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceTemperatureFacet facet = new SurfaceTemperatureFacet(region.getRegion(), region.getBorderForFacet(SurfaceTemperatureFacet.class));
        Rect2i processRegion = facet.getWorldRegion();

        for (BaseVector2i position : processRegion.contents()) {
            // modify initial noise
            float noiseAdjusted = this.temperatureNoise.noise(position.x(), position.y()) / 5 + TEMPERATURE_BASE;

            // clamp to more reasonable base values, just in case
            noiseAdjusted = TeraMath.clamp(noiseAdjusted, -.1f, .45f);
            facet.setWorld(position, noiseAdjusted);
        }

        region.setRegionFacet(SurfaceTemperatureFacet.class, facet);
    }
}
