// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds;

import org.terasology.biomesAPI.Biome;
import org.terasology.gestalt.naming.Name;

public enum CoreBiome implements Biome {
    MOUNTAINS("Mountains"),
    SNOW("Snow"),
    DESERT("Desert"),
    FOREST("Forest"),
    OCEAN("Ocean"),
    BEACH("Beach"),
    PLAINS("Plains");

    private final Name id;
    private final String displayName;

    CoreBiome(String displayName) {
        this.id = new Name("CoreWorlds:" + name());
        this.displayName = displayName;
    }

    @Override
    public Name getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

}
