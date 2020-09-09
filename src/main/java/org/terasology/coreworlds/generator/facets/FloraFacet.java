// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.coreworlds.generator.facets;

import org.terasology.coreworlds.generator.rasterizers.FloraType;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.SparseObjectFacet3D;

/**
 * Stores where plants can be placed
 */
public class FloraFacet extends SparseObjectFacet3D<FloraType> {

    public FloraFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
