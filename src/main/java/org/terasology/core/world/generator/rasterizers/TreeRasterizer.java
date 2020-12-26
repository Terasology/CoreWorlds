/*
 * Copyright 2015 MovingBlocks
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

import org.joml.Vector3ic;
import org.terasology.core.world.generator.facets.TreeFacet;
import org.terasology.core.world.generator.trees.TreeGenerator;
import org.terasology.math.Region3i;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.base.SparseFacet3D;

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
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
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
