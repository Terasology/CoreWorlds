// Copyright 2013 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.worldGenerators;

import org.terasology.core.world.generator.facetProviders.BiomeProvider;
import org.terasology.core.world.generator.facetProviders.DefaultFloraProvider;
import org.terasology.core.world.generator.facetProviders.DefaultTreeProvider;
import org.terasology.core.world.generator.facetProviders.HeightMapSurfaceHeightProvider;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.facetProviders.SimplexHumidityProvider;
import org.terasology.core.world.generator.facetProviders.SimplexRoughnessProvider;
import org.terasology.core.world.generator.facetProviders.SimplexSurfaceTemperatureProvider;
import org.terasology.core.world.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.core.world.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.generation.BaseFacetedWorldGenerator;
import org.terasology.engine.world.generation.WorldBuilder;
import org.terasology.engine.world.generator.RegisterWorldGenerator;
import org.terasology.engine.world.generator.plugin.WorldGeneratorPluginLibrary;

@RegisterWorldGenerator(id = "heightMap", displayName = "Height Map", description = "Generates the world using a height map")
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
                .addProvider(new SimplexHumidityProvider())
                .addProvider(new SimplexSurfaceTemperatureProvider())
                .addProvider(new SimplexRoughnessProvider())
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
