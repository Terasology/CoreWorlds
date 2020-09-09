// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.rasterizers;

import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.geom.Vector3i;

/**
 *
 */
public class GroundRasterizer implements WorldRasterizer {

    private Block stone;
    private Block water;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        stone = blockManager.getBlock("CoreAssets:Stone");
        water = blockManager.getBlock("CoreAssets:Water");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        SurfaceHeightFacet surfaceHeightData = chunkRegion.getFacet(SurfaceHeightFacet.class);
        Vector3i chunkOffset = chunk.getChunkWorldOffset();
        for (int x = 0; x < chunk.getChunkSizeX(); ++x) {
            for (int z = 0; z < chunk.getChunkSizeZ(); ++z) {
                float surfaceHeight = surfaceHeightData.get(x, z);
                int y;
                for (y = 0; y < chunk.getChunkSizeY() && y + chunkOffset.y < surfaceHeight; ++y) {
                    chunk.setBlock(x, y, z, stone);
                }
                for (; y < chunk.getChunkSizeY() && y + chunkOffset.y <= 32; ++y) {
                    chunk.setBlock(x, y, z, water);
                }
            }
        }
    }
}
