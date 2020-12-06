// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facetProviders;

import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.ElevationFacet;

import com.google.common.base.Preconditions;
import org.terasology.world.generation.facets.SeaLevelFacet;


/**
 * Flattens the surface in a circular area around a given coordinate. The area outside this area will be adjusted up to
 * a certain radius to generate a smooth embedding with the {@link ElevationFacet}. It is guaranteed that the plateau is
 * above the sea level as defined by {@link SeaLevelFacet}.
 * <pre>
 *           inner rad.
 *           __________
 *          /          \
 *         /            \
 *    ~~~~~  outer rad.  ~~~~~
 * </pre>
 */
@Updates(@Facet(ElevationFacet.class))
@Requires(@Facet(SeaLevelFacet.class))
public class PlateauProvider implements FacetProvider {

    private final ImmutableVector2i centerPos;
    private final float innerRadius;
    private final float outerRadius;

    /**
     * @param center the center of the circle-shaped plateau
     * @param innerRadius the radius of the flat plateau
     * @param outerRadius the radius of the affected (smoothened) area
     */
    public PlateauProvider(BaseVector2i center, float innerRadius, float outerRadius) {
        Preconditions.checkArgument(innerRadius >= 0, "innerRadius must be >= 0");
        Preconditions.checkArgument(outerRadius > innerRadius, "outerRadius must be larger than innerRadius");

        this.centerPos = ImmutableVector2i.createOrUse(center);
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
    }

    /**
     * @param center the center of the circle-shaped plateau
     * @param targetHeight ignored
     * @param innerRadius the radius of the flat plateau
     * @param outerRadius the radius of the affected (smoothened) area
     * @deprecated the targetHeight is ignored, use {@link #PlateauProvider(BaseVector2i, float, float)} instead
     */
    @Deprecated
    public PlateauProvider(BaseVector2i center, float targetHeight, float innerRadius, float outerRadius) {
        Preconditions.checkArgument(innerRadius >= 0, "innerRadius must be >= 0");
        Preconditions.checkArgument(outerRadius > innerRadius, "outerRadius must be larger than innerRadius");

        this.centerPos = ImmutableVector2i.createOrUse(center);
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
    }

    @Override
    public void process(GeneratingRegion region) {
        Region3i reg = region.getRegion();
        Rect2i rc = Rect2i.createFromMinAndMax(reg.minX(), reg.minZ(), reg.maxX(), reg.maxZ());

        if (rc.distanceSquared(centerPos.x(), centerPos.y()) <= outerRadius * outerRadius) {
            ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
            SeaLevelFacet seaLevel = region.getRegionFacet(SeaLevelFacet.class);

            float targetHeight = Math.max(facet.getWorld(centerPos), seaLevel.getSeaLevel() + 3);

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
