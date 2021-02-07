// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.trees;

import org.terasology.world.block.Block;
import org.terasology.world.chunks.Chunks;
import org.terasology.world.chunks.CoreChunk;

/**
 * Object generators are used to generate objects like trees etc.
 *
 */
public abstract class AbstractTreeGenerator implements TreeGenerator {

    protected void safelySetBlock(CoreChunk chunk, int x, int y, int z, Block block) {
        if (Chunks.CHUNK_REGION.contains(x, y, z)) {
            chunk.setBlock(x, y, z, block);
        }
    }
}
