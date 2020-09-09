// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.facetProviders;

import org.terasology.coreworlds.CoreBiome;
import org.terasology.coreworlds.generator.facets.BiomeFacet;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.engine.world.generation.facets.SurfaceHumidityFacet;
import org.terasology.engine.world.generation.facets.SurfaceTemperatureFacet;
import org.terasology.math.geom.BaseVector2i;

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

            if (height <= seaLevel) {
                biomeFacet.set(pos, CoreBiome.OCEAN);
            } else if (height <= seaLevel + 2) {
                biomeFacet.set(pos, CoreBiome.BEACH);
            } else if (temp >= 0.5f && hum < 0.3f) {
                biomeFacet.set(pos, CoreBiome.DESERT);
            } else if (hum >= 0.3f && hum <= 0.6f && temp >= 0.5f) {
                biomeFacet.set(pos, CoreBiome.PLAINS);
            } else if (temp <= 0.3f && hum > 0.5f) {
                biomeFacet.set(pos, CoreBiome.SNOW);
            } else if (hum >= 0.2f && hum <= 0.6f && temp < 0.5f) {
                biomeFacet.set(pos, CoreBiome.MOUNTAINS);
            } else {
                biomeFacet.set(pos, CoreBiome.FOREST);
            }
        }
        region.setRegionFacet(BiomeFacet.class, biomeFacet);
    }
}
