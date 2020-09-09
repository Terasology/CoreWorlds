// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.coreworlds.viewer.layers;

import com.google.common.collect.Maps;
import org.terasology.biomesAPI.Biome;
import org.terasology.coreworlds.CoreBiome;
import org.terasology.nui.Color;

import java.util.Map;
import java.util.function.Function;

/**
 * Maps the core biomes to colors
 */
public class CoreBiomeColors implements Function<Biome, Color> {

    private final Map<Biome, Color> biomeColors = Maps.newHashMap();

    public CoreBiomeColors() {
        biomeColors.put(CoreBiome.DESERT, new Color(0xb0a087ff));
        biomeColors.put(CoreBiome.MOUNTAINS, new Color(0x899a47ff));
        biomeColors.put(CoreBiome.PLAINS, new Color(0x80b068ff));
        biomeColors.put(CoreBiome.SNOW, new Color(0x99ffffff));
        biomeColors.put(CoreBiome.FOREST, new Color(0x439765ff));
        biomeColors.put(CoreBiome.OCEAN, new Color(0x44447aff));
        biomeColors.put(CoreBiome.BEACH, new Color(0xd0c087ff));
    }

    @Override
    public Color apply(Biome biome) {
        Color color = biomeColors.get(biome);
        return color;
    }

    /**
     * @param biome the biome
     * @param color the new color
     */
    public void setBiomeColor(Biome biome, Color color) {
        this.biomeColors.put(biome, color);
    }
}
