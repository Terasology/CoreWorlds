// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.coreworlds.utilities.procedural;

import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector2f;

/**
 * Uses Simplex Noise to select an amoeba-shaped(or perhaps a wobbly-circle-shaped) area The Noise value returned gives
 * a sense of distance from the center and can be used for further manipulation <br/> Guarantees that the noise value
 * will be zero for input point having distance greater than maxDistance and always > 0 for distance less than
 * minDistance Usages could be selecting regions for standalone terrain features like lakes, star shaped islands,
 * mountains etc. <br/><br/>
 * <p>
 * Examples-<br/> gridSize = 5, minDistance = 20, maxDistance = 30 and noise value is multiplied by 30 and set as
 * surface height<br/>
 * <img src="../../../../../../../doc-files/5.jpg" /><br/><br/>
 * gridSize = 15, rest same<br/>
 * <img src="../../../../../../../doc-files/15.jpg" />
 */
public class RegionSelectorNoise {
    private final Noise tileableNoise;
    private final float xCenter;
    private final float yCenter;
    private final float minDistance;
    private final float maxDistance;
    private final int gridSize;

    /**
     * Initialises the noise generator with seed and geometric constraints
     *
     * @param seed seed for underlying Simplex noise
     * @param gridSize grid size for Simplex noise, higher grid size results in more flanked borders
     * @param xCenter x-coord of center of the region
     * @param yCenter y-coord of center of the region
     * @param minDistance minimum distance of a peripheral point from center
     * @param maxDistance maximum distance of a peripheral point from center
     * @see SimplexNoise#SimplexNoise(long, int)
     */
    public RegionSelectorNoise(long seed, int gridSize, float xCenter, float yCenter, float minDistance,
                               float maxDistance) {
        tileableNoise = new SimplexNoise(seed, gridSize);
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.gridSize = gridSize;
    }

    /**
     * The noise implementation
     *
     * @param x x-coord for noise
     * @param y y-coord for noise
     * @return a value in [0, 1] which signifies the ratio of a point's distance from a peripheral point located
     *         radially outwards to it to the distance of that peripheral point from center
     */
    public float noise(int x, int y) {
        Vector2f relative = new Vector2f((float) x - xCenter, (float) y - yCenter);

        if (relative.equals(Vector2f.zero())) {
            return 1.0f;
        }

        float scaledAngle =
                (((float) Math.atan2(relative.y, relative.x) + (float) Math.PI) * ((float) gridSize * SimplexNoise.TILEABLE1DMAGICNUMBER)) / (2.0f * (float) Math.PI);

        float b = 1.0f / minDistance;
        float a = 1.0f / maxDistance - b;

        float adjustedNoise =
                (a * ((tileableNoise.noise(scaledAngle, scaledAngle) + 1.0f) / 2.0f) + b) * relative.length();

        return 1.0f - TeraMath.clamp(adjustedNoise);
    }
}
