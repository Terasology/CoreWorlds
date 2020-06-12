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
import org.terasology.core.world.generator.facets.FloraFacet;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Places {@link FloraType} blocks based on the {@link FloraFacet}.
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

        facet.getRelativeEntries().entrySet().stream()
                .filter(isPlacementAllowed(chunk))
                .forEach(setBlock(chunk, noise));
    }

    /**
     * A predicate that checks whether - some other rasterizer has already placed something here - block is "supported"
     * by block below
     */
    private Predicate<Map.Entry<BaseVector3i, FloraType>> isPlacementAllowed(CoreChunk chunk) {
        return entry -> {
            BaseVector3i pos = entry.getKey();
            return hasSupport(chunk, pos) && isFree(chunk, pos);
        };
    }

    private boolean isFree(CoreChunk chunk, BaseVector3i pos) {
        return chunk.getBlock(pos).equals(air);
    }

    private boolean hasSupport(CoreChunk chunk, BaseVector3i pos) {
        // cannot check block below if on lower chunk bound, assume pos to be supported
        return pos.y() < 1 || chunk.getBlock(pos.x(), pos.y() - 1, pos.z()).isAttachmentAllowed();
    }

    private Consumer<Map.Entry<BaseVector3i, FloraType>> setBlock(CoreChunk chunk, WhiteNoise noise) {
        return entry -> {
            List<Block> list = flora.get(entry.getValue());
            BaseVector3i pos = entry.getKey();
            int blockIdx = Math.abs(noise.intNoise(pos.x(), pos.y(), pos.z())) % list.size();
            Block block = list.get(blockIdx);
            chunk.setBlock(pos, block);
        };
    }
}
