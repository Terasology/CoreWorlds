// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.trees;

import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockUri;
import org.terasology.engine.world.chunks.Chunk;

/**
 * Cactus generator.
 */
public class TreeGeneratorCactus extends AbstractTreeGenerator {

    private static final int MAX_HEIGHT = 4;

    private BlockUri cactusType;

    @Override
    public void generate(BlockManager blockManager, Chunk view, Random rand, int posX, int posY, int posZ) {
        for (int y = posY; y < posY + rand.nextInt(1, MAX_HEIGHT); y++) {
            safelySetBlock(view, posX, y, posZ, blockManager.getBlock(cactusType));
        }
    }

    public TreeGenerator setTrunkType(BlockUri b) {
        cactusType = b;
        return this;
    }
}
