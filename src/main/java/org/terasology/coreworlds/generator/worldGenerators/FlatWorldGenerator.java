// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.worldGenerators;

import org.terasology.coreworlds.generator.facetProviders.BiomeProvider;
import org.terasology.coreworlds.generator.facetProviders.DefaultFloraProvider;
import org.terasology.coreworlds.generator.facetProviders.DefaultTreeProvider;
import org.terasology.coreworlds.generator.facetProviders.FlatSurfaceHeightProvider;
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

@RegisterWorldGenerator(id = "flat", displayName = "Flat")
public class FlatWorldGenerator extends BaseFacetedWorldGenerator {

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public FlatWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    protected WorldBuilder createWorld() {
        return new WorldBuilder(worldGeneratorPluginLibrary)
                .addProvider(new SeaLevelProvider(32))
                // height of 40 so that it is far enough from sea level so that it doesnt just create beachfront
                .addProvider(new FlatSurfaceHeightProvider(40))
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
