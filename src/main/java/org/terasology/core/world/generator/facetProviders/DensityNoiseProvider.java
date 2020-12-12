// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.facetProviders;

import org.terasology.core.world.generator.facets.SurfaceRoughnessFacet;
import org.terasology.math.JomlUtil;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.DensityFacet;
import org.terasology.world.generation.facets.ElevationFacet;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfacesFacet;

/**
 * Adds some additional 3D noise to the DensityFacet, so as to introduce cliffs and overhangs and things.
 */
@Requires({
    @Facet(SurfaceRoughnessFacet.class)
})
@Updates({
    @Facet(value = DensityFacet.class, border = @FacetBorder(top = 1)),
    @Facet(SurfacesFacet.class)
})
public class DensityNoiseProvider implements FacetProvider {
    private SubSampledNoise largeNoise;
    private SubSampledNoise smallNoise;

    @Override
    public void setSeed(long seed) {
        BrownianNoise unscaled = new BrownianNoise(new SimplexNoise(seed), 4);
        unscaled.setPersistence(1);
        smallNoise = new SubSampledNoise(unscaled, new Vector3f(0.015f, 0.02f, 0.015f), 4);
        largeNoise = new SubSampledNoise(unscaled, new Vector3f(0.005f, 0.007f, 0.005f), 4);
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceRoughnessFacet surfaceRoughnessFacet = region.getRegionFacet(SurfaceRoughnessFacet.class);
        DensityFacet densityFacet = region.getRegionFacet(DensityFacet.class);
        SurfacesFacet surfacesFacet = region.getRegionFacet(SurfacesFacet.class);

        Region3i densityRegion = densityFacet.getWorldRegion();
        float[] smallNoiseValues = smallNoise.noise(densityRegion);
        float[] largeNoiseValues = largeNoise.noise(densityRegion);
        float[] densityValues = densityFacet.getInternal();

        int x = densityRegion.minX();
        int y = densityRegion.minY();
        int z = densityRegion.minZ();
        for (int i = 0; i < densityValues.length; i++) {
            float intensity = Math.max(0f, surfaceRoughnessFacet.getWorld(x, z));
            float smallIntensity = Math.min(intensity, 1f);
            float largeIntensity = intensity - smallIntensity;
            densityValues[i] += smallNoiseValues[i] * intensity * 20 + largeNoiseValues[i] * largeIntensity * 60;

            x++;
            if (x > densityRegion.maxX()) {
                x = densityRegion.minX();
                y++;
                if (y > densityRegion.maxY()) {
                    y = densityRegion.minY();
                    z++;
                }
            }
        }

        for (Vector3i pos : surfacesFacet.getWorldRegion()) {
            if (densityRegion.encompasses(pos) && densityRegion.encompasses(pos.x, pos.y + 1, pos.z)) {
                surfacesFacet.setWorld(JomlUtil.from(pos), densityFacet.getWorld(pos) > 0 && densityFacet.getWorld(pos.x, pos.y + 1, pos.z) <= 0);
            }
        }
    }
}
