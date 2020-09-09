// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.coreworlds.viewer.layers;

import org.terasology.coreworlds.generator.facets.FloraFacet;
import org.terasology.coreworlds.generator.rasterizers.FloraType;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.viewer.color.ColorBlender;
import org.terasology.engine.world.viewer.color.ColorBlenders;
import org.terasology.engine.world.viewer.color.ColorModels;
import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.nui.Color;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * Renders the flora coverage based on {@link FloraFacet}.
 */
@Renders(value = FloraFacet.class, order = ZOrder.FLORA)
public class FloraFacetLayer extends AbstractFacetLayer {

    private final Function<FloraType, Color> colorFunc = new CoreFloraColors();
    private final Function<FloraType, String> labelFunc = Object::toString;

    @Override
    public void render(BufferedImage img, Region region) {
        FloraFacet treeFacet = region.getFacet(FloraFacet.class);

        Graphics2D g = img.createGraphics();
        int width = img.getWidth();
        ColorModel colorModel = img.getColorModel();
        ColorBlender blender = ColorBlenders.forColorModel(ColorModels.RGBA, colorModel);
        DataBufferInt dataBuffer = (DataBufferInt) img.getRaster().getDataBuffer();

        for (Entry<BaseVector3i, FloraType> entry : treeFacet.getRelativeEntries().entrySet()) {
            FloraType treeGen = entry.getValue();
            int wx = entry.getKey().getX();
            int wz = entry.getKey().getZ();
            Color color = colorFunc.apply(treeGen);

            int src = color.rgba();
            int dst = dataBuffer.getElem(wz * width + wx);

            int mix = blender.blend(src, dst);
            dataBuffer.setElem(wz * width + wx, mix);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        FloraFacet floraFacet = region.getFacet(FloraFacet.class);

        Region3i worldRegion = floraFacet.getWorldRegion();
        Region3i relativeRegion = floraFacet.getRelativeRegion();

        int rx = wx - worldRegion.minX() + relativeRegion.minX();
        int rz = wy - worldRegion.minZ() + relativeRegion.minZ();

        for (Entry<BaseVector3i, FloraType> entry : floraFacet.getRelativeEntries().entrySet()) {
            BaseVector3i treePos = entry.getKey();

            if (treePos.getX() == rx && treePos.getZ() == rz) {
                FloraType flora = entry.getValue();
                return labelFunc.apply(flora);
            }
        }

        return "-no vegetation-";
    }

}
