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
package org.terasology.core.world;

import org.terasology.biomesAPI.Biome;
import org.terasology.naming.Name;

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

    @Override
    public float getHumidity() {
        if (displayName.equals("Ocean") || displayName.equals("Snow")) {
            return .9f;
        } else if (displayName.equals("Beach")) {
            return .8f;
        } else if (displayName.equals("Forest")) {
            return .7f;
        } else if (displayName.equals("Plains")) {
            return .55f;
        } else if (displayName.equals("Mountains")) {
            return .3f;
        } else if (displayName.equals("Desert")) {
            return .15f;
        } else {
            return .3f;
        }
    }

    @Override
    public float getTemperature() {
        if (displayName.equals("Ocean") || displayName.equals("Snow")) {
            return .13f;
        } else if (displayName.equals("Beach")) {
            return .28f;
        } else if (displayName.equals("Mountains")) {
            return .09f;
        } else if (displayName.equals("Desert")) {
            return .27f;
        } else {
            return .22f;
        }
    }
}
