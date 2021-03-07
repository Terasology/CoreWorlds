/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.core.world.generator.rasterizers;

import org.joml.Vector2i;
import org.joml.Vector3ic;
import org.terasology.biomesAPI.Biome;
import org.terasology.biomesAPI.BiomeRegistry;
import org.terasology.core.world.CoreBiome;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.ScalableWorldRasterizer;
import org.terasology.engine.world.generation.facets.DensityFacet;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceDepthFacet;
import org.terasology.engine.world.generation.facets.SurfacesFacet;

@Requires({
    @Facet(DensityFacet.class),
    @Facet(SurfacesFacet.class),
    @Facet(BiomeFacet.class),
    @Facet(SeaLevelFacet.class)
})
public class SolidRasterizer implements ScalableWorldRasterizer {

    private Block water;
    private Block ice;
    private Block stone;
    private Block sand;
    private Block grass;
    private Block snow;
    private Block dirt;
    private BiomeRegistry biomeRegistry;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        biomeRegistry = CoreRegistry.get(BiomeRegistry.class);
        stone = blockManager.getBlock("CoreAssets:Stone");
        water = blockManager.getBlock("CoreAssets:Water");
        ice = blockManager.getBlock("CoreAssets:Ice");
        sand = blockManager.getBlock("CoreAssets:Sand");
        grass = blockManager.getBlock("CoreAssets:Grass");
        snow = blockManager.getBlock("CoreAssets:Snow");
        dirt = blockManager.getBlock("CoreAssets:Dirt");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion, float scale) {
        DensityFacet solidityFacet = chunkRegion.getFacet(DensityFacet.class);
        SurfacesFacet surfacesFacet = chunkRegion.getFacet(SurfacesFacet.class);
        SurfaceDepthFacet surfaceDepthFacet = chunkRegion.getFacet(SurfaceDepthFacet.class);
        BiomeFacet biomeFacet = chunkRegion.getFacet(BiomeFacet.class);
        SeaLevelFacet seaLevelFacet = chunkRegion.getFacet(SeaLevelFacet.class);
        int seaLevel = seaLevelFacet.getSeaLevel();

        Vector2i pos2d = new Vector2i();
        for (Vector3ic pos : Chunks.CHUNK_REGION) {
            pos2d.set(pos.x(), pos.z());
            float density = solidityFacet.get(pos);
            float basePosY = (pos.y() + chunk.getChunkWorldOffsetY()) * scale;
            float posY = basePosY + Math.max(0, Math.min(scale, density));

            // Check for an optional depth for this layer - if defined stop generating below that level
            if (surfaceDepthFacet != null && posY < surfaceDepthFacet.get(pos2d)) {
                continue;
            }

            Biome biome = biomeFacet.get(pos2d);
            biomeRegistry.setBiome(biome, chunk, pos.x(), pos.y(), pos.z());

            if (posY < seaLevel && basePosY + scale > seaLevel && seaLevel < scale) {
                // ensure that the ocean is at least 1 block thick.
                chunk.setBlock(pos, water);
            } else if (density > 0 && surfacesFacet.get(pos)) {
                chunk.setBlock(pos, getSurfaceBlock(biome, posY - seaLevel));
            } else if (density > 0) {
                chunk.setBlock(pos, getBelowSurfaceBlock(density, biome));
            } else if (posY <= seaLevel) {         // either OCEAN or SNOW
                if (posY + scale > seaLevel && CoreBiome.SNOW == biome) {
                    chunk.setBlock(pos, ice);
                } else {
                    chunk.setBlock(pos, water);
                }
            }
        }
    }

    private Block getSurfaceBlock(Biome type, float heightAboveSea) {
        if (type instanceof CoreBiome) {
            switch ((CoreBiome) type) {
                case FOREST:
                case PLAINS:
                case MOUNTAINS:
                    if (heightAboveSea > 96) {
                        return snow;
                    } else if (heightAboveSea >= 0) {
                        return grass;
                    } else {
                        return dirt;
                    }
                case SNOW:
                    if (heightAboveSea >= 0) {
                        return snow;
                    } else {
                        return dirt;
                    }
                case DESERT:
                case OCEAN:
                case BEACH:
                    return sand;
            }
        }
        return dirt;
    }

    private Block getBelowSurfaceBlock(float density, Biome type) {
        if (type instanceof CoreBiome) {
            switch ((CoreBiome) type) {
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
            }
        }
        if (density > 32) {
            return stone;
        } else {
            return dirt;
        }
    }
}
