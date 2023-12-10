// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2ic;
import org.terasology.core.world.CoreBiome;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.core.world.generator.facets.SurfaceRoughnessFacet;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.ScalableFacetProvider;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceHumidityFacet;
import org.terasology.engine.world.generation.facets.SurfaceTemperatureFacet;

/**
 * Determines the biome based on temperature and humidity
 */
@Produces(BiomeFacet.class)
@Requires({
    @Facet(SeaLevelFacet.class),
    @Facet(ElevationFacet.class),
    @Facet(SurfaceRoughnessFacet.class),
    @Facet(SurfaceTemperatureFacet.class),
    @Facet(SurfaceHumidityFacet.class)})
public class BiomeProvider implements ScalableFacetProvider {

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region, float scale) {
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        ElevationFacet elevationFacet = region.getRegionFacet(ElevationFacet.class);
        SurfaceRoughnessFacet roughnessFacet = region.getRegionFacet(SurfaceRoughnessFacet.class);
        SurfaceTemperatureFacet temperatureFacet = region.getRegionFacet(SurfaceTemperatureFacet.class);
        SurfaceHumidityFacet humidityFacet = region.getRegionFacet(SurfaceHumidityFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        BiomeFacet biomeFacet = new BiomeFacet(region.getRegion(), border);

        int seaLevel = seaLevelFacet.getSeaLevel();

        for (Vector2ic pos : biomeFacet.getRelativeArea()) {
            float temp = temperatureFacet.get(pos);
            float hum = temp * humidityFacet.get(pos);
            float height = elevationFacet.get(pos);
            float roughness = roughnessFacet.get(pos);

            if (height <= seaLevel) {
                 biomeFacet.set(pos, CoreBiome.OCEAN);
            } else if (height <= seaLevel + 2) {
                biomeFacet.set(pos, CoreBiome.BEACH);
            } else if (temp >= 0.5f && hum < 0.3f) {
                biomeFacet.set(pos, CoreBiome.DESERT);
            } else if (temp <= 0.3f) {
                biomeFacet.set(pos, CoreBiome.SNOW);
            } else if (roughness < 0.1 && hum < 0.5f) {
                biomeFacet.set(pos, CoreBiome.PLAINS);
            } else if ((height - seaLevel) / 60 + roughness >= 2) {
                biomeFacet.set(pos, CoreBiome.MOUNTAINS);
            } else {
                biomeFacet.set(pos, CoreBiome.FOREST);
            }
        }
        region.setRegionFacet(BiomeFacet.class, biomeFacet);
    }
}
