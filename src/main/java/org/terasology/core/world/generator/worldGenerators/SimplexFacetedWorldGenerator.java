// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.worldGenerators;

import org.terasology.core.world.generator.facetProviders.BiomeProvider;
import org.terasology.core.world.generator.facetProviders.DefaultFloraProvider;
import org.terasology.core.world.generator.facetProviders.DefaultTreeProvider;
import org.terasology.core.world.generator.facetProviders.PlateauProvider;
import org.terasology.core.world.generator.facetProviders.SimplexBaseSurfaceProvider;
import org.terasology.core.world.generator.facetProviders.SimplexHillsAndMountainsProvider;
import org.terasology.core.world.generator.facetProviders.SimplexHumidityProvider;
import org.terasology.core.world.generator.facetProviders.SimplexOceanProvider;
import org.terasology.core.world.generator.facetProviders.SimplexRiverProvider;
import org.terasology.core.world.generator.facetProviders.SimplexSurfaceTemperatureProvider;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.facetProviders.SpawnPlateauProvider;
import org.terasology.core.world.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.core.world.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.spawner.FixedSpawner;
import org.terasology.math.JomlUtil;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.SurfaceHeightCompatibilityProvider;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

/**
 */
@RegisterWorldGenerator(id = "facetedsimplex", displayName = "Simplex", description = "Experimental world generator based on Simplex noise")
public class SimplexFacetedWorldGenerator extends BaseFacetedWorldGenerator {

    private static final ImmutableVector2i SPAWN_POS = new ImmutableVector2i(0,0);

    private final FixedSpawner spawner = new FixedSpawner(SPAWN_POS.x(), SPAWN_POS.y());

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public SimplexFacetedWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public Vector3f getSpawnPosition(EntityRef entity) {
        return JomlUtil.from(spawner.getSpawnPosition(getWorld(), entity));
    }

    @Override
    protected WorldBuilder createWorld() {
        int seaLevel = 32;

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
                .addProvider(new SpawnPlateauProvider(SPAWN_POS))
                .addProvider(new SurfaceHeightCompatibilityProvider())
                .addRasterizer(new SolidRasterizer())
                .addPlugins()
                .addRasterizer(new FloraRasterizer())
                .addRasterizer(new TreeRasterizer());
    }
}
