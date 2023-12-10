// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.generation.EntityBuffer;
import org.terasology.engine.world.generation.World;
import org.terasology.engine.world.generator.ChunkGenerationPass;
import org.terasology.engine.world.generator.WorldConfigurator;
import org.terasology.engine.world.generator.WorldConfiguratorAdapter;
import org.terasology.engine.world.generator.WorldGenerator;

import java.util.List;

/**
 */
public abstract class AbstractBaseWorldGenerator implements WorldGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBaseWorldGenerator.class);

    private String worldSeed;
    private final List<ChunkGenerationPass> generationPasses = Lists.newArrayList();
    private final SimpleUri uri;

    public AbstractBaseWorldGenerator(SimpleUri uri) {
        this.uri = uri;
    }

    @Override
    public void initialize() {
        // do nothing
    }

    @Override
    public WorldConfigurator getConfigurator() {
        return new WorldConfiguratorAdapter();
    }

    @Override
    public final SimpleUri getUri() {
        return uri;
    }

    @Override
    public String getWorldSeed() {
        return worldSeed;
    }

    @Override
    public void setWorldSeed(final String seed) {
        worldSeed = seed;
        for (final ChunkGenerationPass generator : generationPasses) {
            generator.setWorldSeed(seed);
        }
    }

    protected final void register(final ChunkGenerationPass generator) {
        registerPass(generator);
        generationPasses.add(generator);
    }

    private void registerPass(final ChunkGenerationPass generator) {
        generator.setWorldSeed(worldSeed);
    }

    @Override
    public void createChunk(final Chunk chunk, EntityBuffer buffer) {
        for (final ChunkGenerationPass generator : generationPasses) {
            try {
                generator.generateChunk(chunk);
            } catch (RuntimeException e) {
                logger.error("Error during generation pass {}", generator, e);
            }
        }
    }

    @Override
    public World getWorld() {
        return null;
    }
}
