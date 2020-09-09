// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.worldGenerators;

import org.terasology.coreworlds.generator.facetProviders.BiomeProvider;
import org.terasology.coreworlds.generator.facetProviders.DefaultFloraProvider;
import org.terasology.coreworlds.generator.facetProviders.DefaultTreeProvider;
import org.terasology.coreworlds.generator.facetProviders.HeightMapSurfaceHeightProvider;
import org.terasology.coreworlds.generator.facetProviders.PerlinHumidityProvider;
import org.terasology.coreworlds.generator.facetProviders.PerlinSurfaceTemperatureProvider;
import org.terasology.coreworlds.generator.facetProviders.SeaLevelProvider;
import org.terasology.coreworlds.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.coreworlds.generator.rasterizers.FloraRasterizer;
import org.terasology.coreworlds.generator.rasterizers.SolidRasterizer;
import org.terasology.coreworlds.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.generation.BaseFacetedWorldGenerator;
import org.terasology.engine.world.generation.WorldBuilder;
import org.terasology.engine.world.generator.RegisterWorldGenerator;
import org.terasology.engine.world.generator.plugin.WorldGeneratorPluginLibrary;

/**
 *
 */
@RegisterWorldGenerator(id = "heightMap", displayName = "Height Map", description = "Generates the world using a " +
        "height map")
public class HeightMapWorldGenerator extends BaseFacetedWorldGenerator {

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public HeightMapWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    protected WorldBuilder createWorld() {
        return new WorldBuilder(worldGeneratorPluginLibrary)
                .setSeaLevel(16)
                .addProvider(new SeaLevelProvider(16))
                .addProvider(new HeightMapSurfaceHeightProvider())
                .addProvider(new PerlinHumidityProvider())
                .addProvider(new PerlinSurfaceTemperatureProvider())
                .addProvider(new BiomeProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new DefaultFloraProvider())
                .addProvider(new DefaultTreeProvider())
                .addRasterizer(new FloraRasterizer())
                .addRasterizer(new TreeRasterizer())
                .addRasterizer(new SolidRasterizer())
                .addPlugins();
    }
}
