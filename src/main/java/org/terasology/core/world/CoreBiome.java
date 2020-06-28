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
    MOUNTAINS("Mountains", .3f, .09f),
    SNOW("Snow", .85f, .05f),
    DESERT("Desert", .15f, .26f),
    FOREST("Forest", .65f, .22f),
    OCEAN("Ocean", .9f, .13f),
    BEACH("Beach", .8f, .28f),
    PLAINS("Plains", .55f, .22f);

    private final Name id;
    private final String displayName;
    private final float humidity;
    private final float temperature;

    CoreBiome(String displayName, float humidity, float temperature) {
        this.id = new Name("CoreWorlds:" + name());
        this.displayName = displayName;
        this.humidity = humidity;
        this.temperature = temperature;
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
        return this.humidity;
    }

    @Override
    public float getTemperature() {
        return this.temperature;
    }
}
