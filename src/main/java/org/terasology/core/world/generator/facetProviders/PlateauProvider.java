// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facetProviders;

import com.google.common.base.Preconditions;
import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.ElevationFacet;


/**
 * Flattens the surface in a circular area around a given coordinate.
 * <p>
 * The area outside this area will be adjusted up to a certain radius to generate a smooth embedding.
 * <pre>
 *           inner rad.
 *           __________
 *          /          \
 *         /            \
 *    ~~~~~  outer rad.  ~~~~~
 * </pre>
 *
 * @deprecated Use {@link SpawnPlateauProvider} instead. Deciding on the target height statically does not work well
 *         together with the natural elevation, leading to weird craters at the spawn point. To allow for taking the
 *         elevation into account a new provider with fixed spawn area size to utilize facet borders is provided.
 */
@Updates(@Facet(ElevationFacet.class))
@Deprecated
public class PlateauProvider implements FacetProvider {

    private final ImmutableVector2i centerPos;
    private final float targetHeight;
    private final float innerRadius;
    private final float outerRadius;

    /**
     * @param center the center of the circle-shaped plateau
     * @param targetHeight the height level of the plateau
     * @param innerRadius the radius of the flat plateau
     * @param outerRadius the radius of the affected (smoothened) area
     */
    public PlateauProvider(BaseVector2i center, float targetHeight, float innerRadius, float outerRadius) {
        Preconditions.checkArgument(innerRadius >= 0, "innerRadius must be >= 0");
        Preconditions.checkArgument(outerRadius > innerRadius, "outerRadius must be larger than innerRadius");

        this.centerPos = ImmutableVector2i.createOrUse(center);
        this.targetHeight = targetHeight;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
    }

    @Override
    public void process(GeneratingRegion region) {
        Region3i reg = region.getRegion();
        Rect2i rc = Rect2i.createFromMinAndMax(reg.minX(), reg.minZ(), reg.maxX(), reg.maxZ());

        if (rc.distanceSquared(centerPos.x(), centerPos.y()) <= outerRadius * outerRadius) {
            ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);

            // update the surface height
            for (BaseVector2i pos : facet.getWorldRegion().contents()) {
                float originalValue = facet.getWorld(pos);
                int distSq = pos.distanceSquared(centerPos);

                if (distSq <= innerRadius * innerRadius) {
                    facet.setWorld(pos, targetHeight);
                } else if (distSq <= outerRadius * outerRadius) {
                    double dist = pos.distance(centerPos) - innerRadius;
                    float norm = (float) dist / (outerRadius - innerRadius);
                    facet.setWorld(pos, TeraMath.lerp(targetHeight, originalValue, norm));
                }
            }
        }
    }
}
