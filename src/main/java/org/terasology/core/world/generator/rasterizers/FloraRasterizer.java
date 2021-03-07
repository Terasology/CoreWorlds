/*
 * Copyright 2014 MovingBlocks
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.joml.Vector3ic;
import org.terasology.core.world.generator.facets.FloraFacet;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;

import java.util.List;
import java.util.Map;

/**
 */
public class FloraRasterizer implements WorldRasterizer {

    private final Map<FloraType, List<Block>> flora = Maps.newEnumMap(FloraType.class);
    private Block air;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        air = blockManager.getBlock(BlockManager.AIR_ID);

        flora.put(FloraType.GRASS, ImmutableList.of(
                blockManager.getBlock("CoreAssets:TallGrass1"),
                blockManager.getBlock("CoreAssets:TallGrass2"),
                blockManager.getBlock("CoreAssets:TallGrass3")));

        flora.put(FloraType.FLOWER, ImmutableList.of(
                blockManager.getBlock("CoreAssets:Dandelion"),
                blockManager.getBlock("CoreAssets:Glowbell"),
                blockManager.getBlock("CoreAssets:Iris"),
                blockManager.getBlock("CoreAssets:Lavender"),
                blockManager.getBlock("CoreAssets:RedClover"),
                blockManager.getBlock("CoreAssets:RedFlower"),
                blockManager.getBlock("CoreAssets:Tulip"),
                blockManager.getBlock("CoreAssets:YellowFlower")));

        flora.put(FloraType.MUSHROOM, ImmutableList.of(
                blockManager.getBlock("CoreAssets:BigBrownShroom"),
                blockManager.getBlock("CoreAssets:BrownShroom"),
                blockManager.getBlock("CoreAssets:RedShroom")));
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        FloraFacet facet = chunkRegion.getFacet(FloraFacet.class);

        WhiteNoise noise = new WhiteNoise(chunk.getPosition().hashCode());

        Map<Vector3ic, FloraType> entries = facet.getRelativeEntries();
        // check if some other rasterizer has already placed something here
        entries.keySet().stream().filter(pos -> chunk.getBlock(pos).equals(air)).forEach(pos -> {

            FloraType type = entries.get(pos);
            List<Block> list = flora.get(type);
            int blockIdx = Math.abs(noise.intNoise(pos.x(), pos.y(), pos.z())) % list.size();
            Block block = list.get(blockIdx);
            chunk.setBlock(pos, block);
        });
    }
}
