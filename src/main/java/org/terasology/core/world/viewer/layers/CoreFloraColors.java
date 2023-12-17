// Copyright 2014 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.viewer.layers;

import com.google.common.collect.Maps;
import org.terasology.core.world.generator.rasterizers.FloraType;
import org.terasology.nui.Color;

import java.util.Map;
import java.util.function.Function;

/**
 * Maps {@link FloraType} to color.
 */
public class CoreFloraColors implements Function<FloraType, Color> {

    private final Map<FloraType, Color> floraColors = Maps.newHashMap();

    public CoreFloraColors() {
        floraColors.put(FloraType.GRASS, new Color(0x0c907780));
        floraColors.put(FloraType.FLOWER, new Color(0xddda1180));
        floraColors.put(FloraType.MUSHROOM, new Color(0x88991180));
    }

    @Override
    public Color apply(FloraType biome) {
        return floraColors.get(biome);
    }
}
