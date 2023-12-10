// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world;

import org.joml.Vector3ic;
import org.terasology.biomesAPI.Biome;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.naming.Name;

public enum CoreBiome implements Biome {
    MOUNTAINS("Mountains"),
    SNOW("Snow"),
    DESERT("Desert"),
    FOREST("Forest"),
    OCEAN("Ocean"),
    BEACH("Beach"),
    PLAINS("Plains");

    private final Name id;
    private final String displayName;

    private Block stone;
    private Block sand;
    private Block grass;
    private Block snow;
    private Block dirt;

    CoreBiome(String displayName) {
        this.id = new Name("CoreWorlds:" + name());
        this.displayName = displayName;
    }

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        stone = blockManager.getBlock("CoreAssets:stone");
        sand = blockManager.getBlock("CoreAssets:Sand");
        grass = blockManager.getBlock("CoreAssets:Grass");
        snow = blockManager.getBlock("CoreAssets:Snow");
        dirt = blockManager.getBlock("CoreAssets:Dirt");
    }

    @Override
    public float getHumidity() {
        switch (this) {
            case MOUNTAINS:
                return 0.3f;
            case SNOW:
                return 0.4f;
            case DESERT:
                return 0.1f;
            case FOREST:
                return 0.7f;
            case OCEAN:
                return 1;
            case BEACH:
                return 0.8f;
            case PLAINS:
                return 0.5f;
        }
        return 0.5f;
    }
    @Override
    public float getTemperature() {
        switch (this) {
            case MOUNTAINS:
                return 0.2f;
            case SNOW:
                return 0;
            case DESERT:
                return 0.9f;
            case FOREST:
                return 0.6f;
            case OCEAN:
                return 0.4f;
            case BEACH:
                return 0.7f;
            case PLAINS:
                return 0.5f;
        }
        return 0.5f;
    }

    @Override
    public Block getSurfaceBlock(Vector3ic pos, int seaLevel) {
        int height = pos.y() - seaLevel;
        switch (this) {
            case FOREST:
            case PLAINS:
            case MOUNTAINS:
                if (height > 96) {
                    return snow;
                } else if (height >= 0) {
                    return grass;
                } else {
                    return dirt;
                }
            case SNOW:
                if (height >= 0) {
                    return snow;
                } else {
                    return dirt;
                }
            case DESERT:
            case OCEAN:
            case BEACH:
                return sand;
            default:
                return dirt;
        }
    }

    @Override
    public Block getBelowSurfaceBlock(Vector3ic pos, float density) {
        switch (this) {
            case DESERT:
                if (density > 8) {
                    return stone;
                } else {
                    return sand;
                }
            case BEACH:
                if (density > 2) {
                    return stone;
                } else {
                    return sand;
                }
            case OCEAN:
                return stone;
            default:
                if (density > 32) {
                    return stone;
                } else {
                    return dirt;
                }
        }
    }

    @Override
    public Name getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

}
