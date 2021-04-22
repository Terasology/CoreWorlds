// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.rasterizers;

import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.ScalableWorldRasterizer;
import org.terasology.engine.world.generation.facets.ElevationFacet;

@Requires(@Facet(ElevationFacet.class))
public class SunlightRasterizer implements ScalableWorldRasterizer {
    private final float offset;

    /**
     * @param offset How high above the (ElevationFacet) surface the sunlight should be assumed to stop.
     */
    public SunlightRasterizer(float offset) {
        this.offset = offset;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion, float scale) {
        ElevationFacet elevationFacet = chunkRegion.getFacet(ElevationFacet.class);
        int topHeight = chunk.getChunkWorldOffsetY() + Chunks.SIZE_Y - 1;
        for (int x = 0; x < Chunks.SIZE_X; x++) {
            for (int z = 0; z < Chunks.SIZE_Z; z++) {
                if (elevationFacet.get(x, z) + offset < topHeight * scale) {
                    Block block = chunk.getBlock(x, Chunks.SIZE_Y - 1, z);
                    if (block.isTranslucent() && !block.isLiquid()) {
                        chunk.setSunlightRegen(x, Chunks.SIZE_Y - 1, z, Chunks.MAX_SUNLIGHT_REGEN);
                    }
                }
            }
        }
    }
}
