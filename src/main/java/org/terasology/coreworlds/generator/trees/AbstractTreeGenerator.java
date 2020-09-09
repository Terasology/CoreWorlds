// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.coreworlds.generator.trees;

import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.chunks.ChunkConstants;
import org.terasology.engine.world.chunks.CoreChunk;

/**
 * Object generators are used to generate objects like trees etc.
 */
public abstract class AbstractTreeGenerator implements TreeGenerator {

    protected void safelySetBlock(CoreChunk chunk, int x, int y, int z, Block block) {
        if (ChunkConstants.CHUNK_REGION.encompasses(x, y, z)) {
            chunk.setBlock(x, y, z, block);
        }
    }
}
