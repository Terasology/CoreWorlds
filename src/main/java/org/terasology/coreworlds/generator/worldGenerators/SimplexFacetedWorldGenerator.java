// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.worldGenerators;

import org.terasology.coreworlds.generator.facetProviders.BiomeProvider;
import org.terasology.coreworlds.generator.facetProviders.DefaultFloraProvider;
import org.terasology.coreworlds.generator.facetProviders.DefaultTreeProvider;
import org.terasology.coreworlds.generator.facetProviders.PlateauProvider;
import org.terasology.coreworlds.generator.facetProviders.SeaLevelProvider;
import org.terasology.coreworlds.generator.facetProviders.SimplexBaseSurfaceProvider;
import org.terasology.coreworlds.generator.facetProviders.SimplexHillsAndMountainsProvider;
import org.terasology.coreworlds.generator.facetProviders.SimplexHumidityProvider;
import org.terasology.coreworlds.generator.facetProviders.SimplexOceanProvider;
import org.terasology.coreworlds.generator.facetProviders.SimplexRiverProvider;
import org.terasology.coreworlds.generator.facetProviders.SimplexSurfaceTemperatureProvider;
import org.terasology.coreworlds.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.coreworlds.generator.rasterizers.FloraRasterizer;
import org.terasology.coreworlds.generator.rasterizers.SolidRasterizer;
import org.terasology.coreworlds.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.spawner.FixedSpawner;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.generation.BaseFacetedWorldGenerator;
import org.terasology.engine.world.generation.WorldBuilder;
import org.terasology.engine.world.generator.RegisterWorldGenerator;
import org.terasology.engine.world.generator.plugin.WorldGeneratorPluginLibrary;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Vector3f;

/**
 *
 */
@RegisterWorldGenerator(id = "facetedsimplex", displayName = "Simplex", description = "Experimental world generator " +
        "based on Simplex noise")
public class SimplexFacetedWorldGenerator extends BaseFacetedWorldGenerator {

    private final FixedSpawner spawner = new FixedSpawner(0, 0);

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public SimplexFacetedWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public Vector3f getSpawnPosition(EntityRef entity) {
        return spawner.getSpawnPosition(getWorld(), entity);
    }

    @Override
    protected WorldBuilder createWorld() {
        int seaLevel = 32;
        ImmutableVector2i spawnPos = new ImmutableVector2i(0, 0); // as used by the spawner

        return new WorldBuilder(worldGeneratorPluginLibrary)
                .setSeaLevel(seaLevel)
                .addProvider(new SeaLevelProvider(seaLevel))
                .addProvider(new SimplexHumidityProvider())
                .addProvider(new SimplexSurfaceTemperatureProvider())
                .addProvider(new SimplexBaseSurfaceProvider())
                .addProvider(new SimplexRiverProvider())
                .addProvider(new SimplexOceanProvider())
                .addProvider(new SimplexHillsAndMountainsProvider())
                .addProvider(new BiomeProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new DefaultFloraProvider())
                .addProvider(new DefaultTreeProvider())
                .addProvider(new PlateauProvider(spawnPos, seaLevel + 4, 10, 30))
                //.addRasterizer(new GroundRasterizer(blockManager))
                .addRasterizer(new SolidRasterizer())
                .addPlugins()
                .addRasterizer(new FloraRasterizer())
                .addRasterizer(new TreeRasterizer());
    }
}
