// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.world.generator;

import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.terasology.context.internal.ContextImpl;
import org.terasology.core.world.generator.trees.TreeGenerator;
import org.terasology.core.world.generator.trees.Trees;
import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.chunks.Chunks;
import org.terasology.world.chunks.blockdata.ExtraBlockDataManager;
import org.terasology.world.chunks.internal.ChunkImpl;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeTests {

    private BlockManager blockManager;
    private ExtraBlockDataManager extraDataManager;

    @BeforeEach
    public void setup() {
        ContextImpl context = new ContextImpl();
        CoreRegistry.setContext(context);

        blockManager = Mockito.mock(BlockManager.class);
        Block air = blockManager.getBlock(BlockManager.AIR_ID);

        extraDataManager = new ExtraBlockDataManager();

        Mockito.when(blockManager.getBlock(ArgumentMatchers.<BlockUri>any())).thenReturn(air);
        Mockito.when(blockManager.getBlock(ArgumentMatchers.<String>any())).thenReturn(air);

        context.put(BlockManager.class, blockManager);
    }

    @Test
    public void testBirchDims() {
        assertIsLessOrEqual(estimateExtent(Trees.birchTree()), new Vector3i(22, 35, 22));
    }

    @Test
    public void testOakDims() {
        assertIsLessOrEqual(estimateExtent(Trees.oakTree()), new Vector3i(15, 14, 15));
    }

    @Test
    public void testOakVariationDims() {
        assertIsLessOrEqual(estimateExtent(Trees.oakVariationTree()), new Vector3i(21, 19, 20));
    }

    @Test
    public void testPineDims() {
        assertIsLessOrEqual(estimateExtent(Trees.pineTree()), new Vector3i(25, 32, 26));
    }

    @Test
    public void testRedTreeDims() {
        assertIsLessOrEqual(estimateExtent(Trees.redTree()), new Vector3i(14, 14, 14));
    }

    private Vector3i estimateExtent(TreeGenerator treeGen) {
        return IntStream.range(0, 100)
                .mapToObj(i -> computeAABB(treeGen, i * 37))
                .reduce(new Vector3i(), Vector3i::max);
    }

    private Vector3i computeAABB(TreeGenerator treeGen, long seed) {
        Vector3i pos = new Vector3i(Chunks.SIZE_X / 2, 0, Chunks.SIZE_Z / 2);

        final Vector3i min = new Vector3i(pos);
        final Vector3i max = new Vector3i(pos);

        BlockArea chunks = new BlockArea(-1, -1, 1, 1);
        for (Vector2ic chunkPos : chunks) {
            Chunk chunk = new ChunkImpl(chunkPos.x(), 0, chunkPos.y(), blockManager, extraDataManager) {
                @Override
                public Block setBlock(int x, int y, int z, Block block) {
                    Vector3i world = chunkToWorldPosition(x, y, z, new Vector3i());
                    min.min(world);
                    max.max(world);

                    return null;
                }
            };

            Random random = new MersenneRandom(seed);
            BlockManager blockManagerLocal = CoreRegistry.get(BlockManager.class);
            Vector3i relPos = chunk.chunkToWorldPosition(0, 0, 0, new Vector3i()).sub(pos).negate();
            treeGen.generate(blockManagerLocal, chunk, random, relPos.x, relPos.y, relPos.z);
        }

        return new Vector3i(max).sub(min);
    }

    private void assertIsLessOrEqual(final Vector3i actual, final Vector3i maximum) {
        assertTrue(
                actual.x <= maximum.x && actual.y <= maximum.y && actual.z <= maximum.z,
                "Maximum extent " + actual + " should be less or equal to " + maximum);
    }

}
