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
import org.terasology.core.world.CoreBiome;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generation.facets.SurfaceHumidityFacet;
import org.terasology.world.generation.facets.SurfaceTemperatureFacet;

import java.awt.Component;

/**
 * Determines the biome based on temperature and humidity
 */
@Produces(BiomeFacet.class)
@Requires({
    @Facet(SeaLevelFacet.class),
    @Facet(SurfaceHeightFacet.class),
    @Facet(SurfaceTemperatureFacet.class),
    @Facet(SurfaceHumidityFacet.class)})
public class BiomeProvider implements FacetProvider {

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        SurfaceHeightFacet heightFacet = region.getRegionFacet(SurfaceHeightFacet.class);
        SurfaceTemperatureFacet temperatureFacet = region.getRegionFacet(SurfaceTemperatureFacet.class);
        SurfaceHumidityFacet humidityFacet = region.getRegionFacet(SurfaceHumidityFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        BiomeFacet biomeFacet = new BiomeFacet(region.getRegion(), border);

        int seaLevel = seaLevelFacet.getSeaLevel();

        for (BaseVector2i pos : biomeFacet.getRelativeRegion().contents()) {
            float temp = temperatureFacet.get(pos);
            float hum = temp * humidityFacet.get(pos);
            float height = heightFacet.get(pos);

            if (height <= seaLevel || hum >= .85) { // TODO: remove hum qualifier here and for ocean?
                 biomeFacet.set(pos, CoreBiome.OCEAN);
            } else if (height <= seaLevel + 2 || hum >= .8) {
                biomeFacet.set(pos, CoreBiome.BEACH);
            } else if (hum <= 0.15f) {
                biomeFacet.set(pos, CoreBiome.DESERT);
            } else if (hum <= 0.6f && temp >= 0.20f) {
                biomeFacet.set(pos, CoreBiome.PLAINS);
            } else if (hum >= .55f && temp <= 0.05f) {
                biomeFacet.set(pos, CoreBiome.SNOW);
            } else if (hum <= .45f && temp <= .15f) {
                biomeFacet.set(pos, CoreBiome.MOUNTAINS);
            } else {
                biomeFacet.set(pos, CoreBiome.FOREST);
            }
        }
        region.setRegionFacet(BiomeFacet.class, biomeFacet);
    }
}
