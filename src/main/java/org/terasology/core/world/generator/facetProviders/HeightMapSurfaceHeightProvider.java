// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facetProviders;

import com.google.common.math.IntMath;
import org.joml.Vector2ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.math.TeraMath;
import org.terasology.nui.properties.OneOf.Enum;
import org.terasology.nui.properties.OneOf.List;
import org.terasology.nui.properties.Range;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

@Produces(ElevationFacet.class)
public class HeightMapSurfaceHeightProvider implements ConfigurableFacetProvider {

    public enum WrapMode {
        CLAMP,
        REPEAT
    }

    private static final Logger logger = LoggerFactory.getLogger(HeightMapSurfaceHeightProvider.class);

    private float[][] heightmap;

    private int mapWidth;
    private int mapHeight;

    private HeightMapConfiguration configuration = new HeightMapConfiguration();

    @Override
    public void setSeed(long seed) {
        initialize();
    }

    @Override
    public void initialize() {
        if (heightmap == null) {
            reloadHeightmap();
        }
    }

    private void reloadHeightmap() {
        logger.info("Reading height map '{}'", configuration.heightMap);

        ResourceUrn urn = new ResourceUrn("core", configuration.heightMap);
        Texture texture = Assets.getTexture(urn).get();
        ByteBuffer[] bb = texture.getData().getBuffers();
        IntBuffer intBuf = bb[0].asIntBuffer();

        mapWidth = texture.getWidth();
        mapHeight = texture.getHeight();

        heightmap = new float[mapWidth][mapHeight];
        while (intBuf.position() < intBuf.limit()) {
            int pos = intBuf.position();
            long val = intBuf.get() & 0xFFFFFFFFL;
            heightmap[pos % mapWidth][pos / mapWidth] = val / (256 * 256 * 256 * 256f);
        }
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ElevationFacet.class);
        ElevationFacet facet = new ElevationFacet(region.getRegion(), border);

        for (Vector2ic pos : facet.getWorldArea()) {
            int xzScale = configuration.terrainScale;

            int mapX0;
            int mapZ0;
            int mapX1;
            int mapZ1;
            switch (configuration.wrapMode) {
                case CLAMP:
                    mapX0 = TeraMath.clamp(pos.x(), 0, mapWidth * xzScale - 1) / xzScale;
                    mapZ0 = TeraMath.clamp(pos.y(), 0, mapHeight * xzScale - 1) / xzScale;
                    mapX1 = TeraMath.clamp(mapX0 + 1, 0, mapWidth - 1);
                    mapZ1 = TeraMath.clamp(mapZ0 + 1, 0, mapHeight - 1);
                    break;
                case REPEAT:
                    mapX0 = IntMath.mod(pos.x(), mapWidth * xzScale) / xzScale;
                    mapZ0 = IntMath.mod(pos.y(), mapHeight * xzScale) / xzScale;
                    mapX1 = IntMath.mod(mapX0 + 1, mapWidth);
                    mapZ1 = IntMath.mod(mapZ0 + 1, mapHeight);
                    break;
                default:
                    throw new UnsupportedOperationException("Not supported: " + configuration.wrapMode);
            }

            double p00 = heightmap[mapX0][mapZ0];
            double p10 = heightmap[mapX1][mapZ0];
            double p11 = heightmap[mapX1][mapZ1];
            double p01 = heightmap[mapX0][mapZ1];

            float relX = IntMath.mod(pos.x(), xzScale) / (float) xzScale;
            float relZ = IntMath.mod(pos.y(), xzScale) / (float) xzScale;

            float interpolatedHeight = (float) lerp(relX, lerp(relZ, p00, p01), lerp(relZ, p10, p11));
            float height = configuration.heightOffset + configuration.heightScale * interpolatedHeight;

            facet.setWorld(pos, height);
        }

        region.setRegionFacet(ElevationFacet.class, facet);

    }

    private static double lerp(double t, double a, double b) {
        return a + fade(t) * (b - a);  //not sure if i should fade t, needs a bit longer to generate chunks but is definately nicer
    }

    private static double fade(double t) {
        // This is Perlin
//        return t * t * t * (t * (t * 6 - 15) + 10);

        // This is Hermite
        return t * t * (3 - 2 * t);
    }

    @Override
    public String getConfigurationName() {
        return "Height Map";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        String prevHeightMap = this.configuration.heightMap;
        this.configuration = (HeightMapConfiguration) configuration;

        if (!Objects.equals(prevHeightMap, this.configuration.heightMap)) {
            reloadHeightmap();
        }
    }

    private static class HeightMapConfiguration implements Component<HeightMapConfiguration> {

        @Enum(description = "Wrap Mode")
        private WrapMode wrapMode = WrapMode.REPEAT;

        @List(items = { "platec_heightmap", "opposing_islands" }, description = "Height Map")
        private String heightMap = "platec_heightmap";

        @Range(min = 0, max = 50f, increment = 1f, precision = 0, description = "Height Offset")
        private float heightOffset = 12;

        @Range(min = 10, max = 200f, increment = 1f, precision = 0, description = "Height Scale Factor")
        private float heightScale = 70;

        @Range(min = 1, max = 32, increment = 1, precision = 0, description = "Terrain Scale Factor")
        private int terrainScale = 8;

        @Override
        public void copy(HeightMapConfiguration other) {
            this.wrapMode = other.wrapMode;
            this.heightMap = other.heightMap;
            this.heightOffset = other.heightOffset;
            this.heightScale = other.heightScale;
            this.terrainScale = other.terrainScale;
        }
    }
}
