// Copyright 2015 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.viewer.layers;

import org.joml.Vector3ic;
import org.terasology.core.world.generator.facets.FloraFacet;
import org.terasology.core.world.generator.rasterizers.FloraType;
import org.terasology.nui.Color;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.viewer.color.ColorBlender;
import org.terasology.engine.world.viewer.color.ColorBlenders;
import org.terasology.engine.world.viewer.color.ColorModels;
import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;

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

    private Function<FloraType, Color> colorFunc = new CoreFloraColors();
    private Function<FloraType, String> labelFunc = Object::toString;

    @Override
    public void render(BufferedImage img, Region region) {
        FloraFacet treeFacet = region.getFacet(FloraFacet.class);

        Graphics2D g = img.createGraphics();
        int width = img.getWidth();
        ColorModel colorModel = img.getColorModel();
        ColorBlender blender = ColorBlenders.forColorModel(ColorModels.RGBA, colorModel);
        DataBufferInt dataBuffer = (DataBufferInt) img.getRaster().getDataBuffer();

        for (Entry<Vector3ic, FloraType> entry : treeFacet.getRelativeEntries().entrySet()) {
            FloraType treeGen = entry.getValue();
            int wx = entry.getKey().x();
            int wz = entry.getKey().z();
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

        BlockRegion worldRegion = floraFacet.getWorldRegion();
        BlockRegion relativeRegion = floraFacet.getRelativeRegion();

        int rx = wx - worldRegion.minX() + relativeRegion.minX();
        int rz = wy - worldRegion.minZ() + relativeRegion.minZ();

        for (Entry<Vector3ic, FloraType> entry : floraFacet.getRelativeEntries().entrySet()) {
            Vector3ic treePos = entry.getKey();

            if (treePos.x() == rx && treePos.z() == rz) {
                FloraType flora = entry.getValue();
                return labelFunc.apply(flora);
            }
        }

        return "-no vegetation-";
    }

}
