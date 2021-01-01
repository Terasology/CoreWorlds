// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.worldGenerators;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.core.world.generator.facetProviders.BiomeProvider;
import org.terasology.core.world.generator.facetProviders.DefaultFloraProvider;
import org.terasology.core.world.generator.facetProviders.DefaultTreeProvider;
import org.terasology.core.world.generator.facetProviders.PerlinBaseSurfaceProvider;
import org.terasology.core.world.generator.facetProviders.PerlinHillsAndMountainsProvider;
import org.terasology.core.world.generator.facetProviders.PerlinHumidityProvider;
import org.terasology.core.world.generator.facetProviders.PerlinOceanProvider;
import org.terasology.core.world.generator.facetProviders.PerlinRiverProvider;
import org.terasology.core.world.generator.facetProviders.PerlinSurfaceTemperatureProvider;
import org.terasology.core.world.generator.facetProviders.PlateauProvider;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.facetProviders.SimplexRoughnessProvider;
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
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

/**
 *
 */
@RegisterWorldGenerator(id = "facetedperlin", displayName = "Perlin", description = "Faceted world generator using " +
        "perlin")
public class PerlinFacetedWorldGenerator extends BaseFacetedWorldGenerator {

    private static final Vector2ic SPAWN_POS = new Vector2i(0, 0);
    private final FixedSpawner spawner = new FixedSpawner(SPAWN_POS.x(), SPAWN_POS.y());

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public PerlinFacetedWorldGenerator(SimpleUri uri) {
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
                .addProvider(new PerlinHumidityProvider())
                .addProvider(new PerlinSurfaceTemperatureProvider())
                .addProvider(new PerlinBaseSurfaceProvider())
                .addProvider(new PerlinRiverProvider())
                .addProvider(new PerlinOceanProvider())
                .addProvider(new PerlinHillsAndMountainsProvider())
                .addProvider(new SimplexRoughnessProvider())
                .addProvider(new BiomeProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new DefaultFloraProvider())
                .addProvider(new DefaultTreeProvider())
                .addProvider(new SpawnPlateauProvider(SPAWN_POS))
                .addRasterizer(new SolidRasterizer())
                .addPlugins()
                .addRasterizer(new FloraRasterizer())
                .addRasterizer(new TreeRasterizer());
    }
}
