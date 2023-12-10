// Copyright 2015 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.trees;

import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.Chunk;

/**
 * Object generators are used to generate objects like trees etc.
 *
 */
public interface TreeGenerator {

    /**
     * Generates a tree at the given position.
     *
     * @param blockManager the block manager to resolve the block uris
     * @param view Chunk view
     * @param rand The random number generator
     * @param posX Relative position on the x-axis (wrt. the chunk)
     * @param posY Relative position on the y-axis (wrt. the chunk)
     * @param posZ Relative position on the z-axis (wrt. the chunk)
     */
    void generate(BlockManager blockManager, Chunk view, Random rand, int posX, int posY, int posZ);
}
