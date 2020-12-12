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
package org.terasology.core.world.generator.facets;

import org.terasology.biomesAPI.Biome;
import org.terasology.math.Region3i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseFieldFacet2D;
import org.terasology.world.generation.facets.base.BaseObjectFacet2D;

/**
 * Modifies the amount of noise to be added to the surface shape, to determine the amount of lumps and cliffs and things.
 */
public class SurfaceRoughnessFacet extends BaseFieldFacet2D {
    public SurfaceRoughnessFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
