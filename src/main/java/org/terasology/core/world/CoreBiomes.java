// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world;

import org.terasology.biomesAPI.BiomeRegistry;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;

import java.util.stream.Stream;

/**
 * Registers all core biomes with the engine.
 */
@RegisterSystem
public class CoreBiomes extends BaseComponentSystem {
    @In
    private BiomeRegistry biomeRegistry;

    /**
     * Registration of systems must be done in preBegin to be early enough.
     */
    @Override
    public void preBegin() {
        Stream.of(CoreBiome.values()).forEach(biomeRegistry::registerBiome);
    }
}
