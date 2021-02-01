// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.rasterizers;

import org.terasology.world.block.Block;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.chunks.Chunks;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.ScalableWorldRasterizer;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.ElevationFacet;

@Requires(@Facet(ElevationFacet.class))
public class SunlightRasterizer implements ScalableWorldRasterizer {
    @Override
    public void initialize() {
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion, float scale) {
        ElevationFacet elevationFacet = chunkRegion.getFacet(ElevationFacet.class);
        int topHeight = chunk.getChunkWorldOffsetY() + Chunks.SIZE_Y - 1;
        for (int x = 0; x < Chunks.SIZE_X; x++) {
            for (int z = 0; z < Chunks.SIZE_Z; z++) {
                if (elevationFacet.get(x, z) - 20 < topHeight * scale) {
                    Block block = chunk.getBlock(x, Chunks.SIZE_Y - 1, z);
                    if (block.isTranslucent() && !block.isLiquid()) {
                        ((Chunk) chunk).setSunlightRegen(x, Chunks.SIZE_Y - 1, z, Chunks.MAX_SUNLIGHT_REGEN);
                    }
                }
            }
        }
    }
}
