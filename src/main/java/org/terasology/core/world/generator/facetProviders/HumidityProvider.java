/*
 * Copyright 2020 MovingBlocks
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

package org.terasology.core.world.generator.facetProviders;

import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.SurfaceHumidityFacet;

@Updates(@Facet(SurfaceHumidityFacet.class))
public class HumidityProvider implements FacetProvider {

    private WorldProvider worldProvider;

    @Override
    public void initialize() {
        worldProvider = CoreRegistry.get(WorldProvider.class);
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceHumidityFacet facet = region.getRegionFacet(SurfaceHumidityFacet.class);

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            // modify initial noise so that it lies between 0 and 1
            facet.setWorld(position, TeraMath.clamp(facet.getWorld(position.x(), position.y()), 0, 1));
        }
    }
}
