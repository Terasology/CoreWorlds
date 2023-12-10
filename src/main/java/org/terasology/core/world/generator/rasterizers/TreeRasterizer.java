// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.rasterizers;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.core.world.generator.facets.TreeFacet;
import org.terasology.core.world.generator.trees.TreeGenerator;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.engine.world.generation.facets.base.SparseFacet3D;

import java.util.Map;

/**
 * Creates trees based on the {@link TreeGenerator} that is
 * defined by the {@link TreeFacet}.
 *
 */
public class TreeRasterizer implements WorldRasterizer {

    private BlockManager blockManager;

    @Override
    public void initialize() {
        blockManager = CoreRegistry.get(BlockManager.class);
        //TODO: Remove these lines when lazy block registration is fixed
        //Currently they are required to ensure that the blocks are all registered before worldgen
        blockManager.getBlock("CoreAssets:OakTrunk");
        blockManager.getBlock("CoreAssets:PineTrunk");
        blockManager.getBlock("CoreAssets:BirchTrunk");
        blockManager.getBlock("CoreAssets:GreenLeaf");
        blockManager.getBlock("CoreAssets:DarkLeaf");
        blockManager.getBlock("CoreAssets:RedLeaf");
        blockManager.getBlock("CoreAssets:Cactus");
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
        TreeFacet facet = chunkRegion.getFacet(TreeFacet.class);

        for (Map.Entry<Vector3ic, TreeGenerator> entry : facet.getRelativeEntries().entrySet()) {
            Vector3ic pos = entry.getKey();
            TreeGenerator treeGen = entry.getValue();
            int seed = relativeToWorld(facet, pos).hashCode();
            Random random = new FastRandom(seed);
            treeGen.generate(blockManager, chunk, random, pos.x(), pos.y(), pos.z());
        }
    }

    // TODO: JAVA8 - move the two conversion methods from SparseFacet3D to default methods in WorldFacet3D
    protected final Vector3i relativeToWorld(SparseFacet3D facet, Vector3ic pos) {

        BlockRegion worldRegion = facet.getWorldRegion();
        BlockRegion relativeRegion = facet.getRelativeRegion();

        return new Vector3i(
                pos.x() - relativeRegion.minX() + worldRegion.minX(),
                pos.y() - relativeRegion.minY() + worldRegion.minY(),
                pos.z() - relativeRegion.minZ() + worldRegion.minZ());
    }
}
