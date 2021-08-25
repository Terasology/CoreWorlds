// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.core.world.generator.facetProviders;

import org.joml.Vector2f;
import org.joml.Vector2ic;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.engine.world.generation.facets.SurfaceHumidityFacet;
import org.terasology.engine.world.generation.facets.SurfaceTemperatureFacet;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.math.TeraMath;
import org.terasology.nui.properties.Range;

import java.util.Iterator;

/**
 * Adds surface height for hill and mountain regions. Mountain and hill regions are based off of temperature and humidity.
 */
@Deprecated
@Requires({@Facet(SurfaceTemperatureFacet.class), @Facet(SurfaceHumidityFacet.class)})
@Updates(@Facet(ElevationFacet.class))
public class PerlinHillsAndMountainsProvider implements ConfigurableFacetProvider {

    private SubSampledNoise mountainNoise;
    private SubSampledNoise hillNoise;
    private PerlinHillsAndMountainsProviderConfiguration configuration = new PerlinHillsAndMountainsProviderConfiguration();

    @Override
    public void setSeed(long seed) {
        // TODO: reduce the number of octaves in BrownianNoise
        mountainNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 3)), new Vector2f(0.0002f, 0.0002f), 4);
        hillNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 4)), new Vector2f(0.0008f, 0.0008f), 4);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);

        float[] mountainData = mountainNoise.noise(facet.getWorldArea());
        float[] hillData = hillNoise.noise(facet.getWorldArea());
        SurfaceTemperatureFacet temperatureData = region.getRegionFacet(SurfaceTemperatureFacet.class);
        SurfaceHumidityFacet humidityData = region.getRegionFacet(SurfaceHumidityFacet.class);

        float[] heightData = facet.getInternal();
        Iterator<Vector2ic> positionIterator = facet.getRelativeArea().iterator();
        for (int i = 0; i < heightData.length; ++i) {
            Vector2ic pos = positionIterator.next();
            float temp = temperatureData.get(pos);
            float tempHumid = temp * humidityData.get(pos);
            Vector2f distanceToMountainBiome = new Vector2f(temp - 0.25f, tempHumid - 0.35f);
            float mIntens = TeraMath.clamp(1.0f - distanceToMountainBiome.length() * 3.0f);
            float densityMountains = Math.max(mountainData[i] * 2.12f, 0) * mIntens * configuration.mountainAmplitude;
            float densityHills = Math.max(hillData[i] * 2.12f - 0.1f, 0) * (1.0f - mIntens) * configuration.hillAmplitude;

            heightData[i] = heightData[i] + 1024 * densityMountains + 128 * densityHills;
        }
    }

    @Override
    public String getConfigurationName() {
        return "Hills and Mountains";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (PerlinHillsAndMountainsProviderConfiguration) configuration;
    }

    private static class PerlinHillsAndMountainsProviderConfiguration implements Component<PerlinHillsAndMountainsProviderConfiguration> {

        @Range(min = 0, max = 3f, increment = 0.01f, precision = 2, description = "Mountain Amplitude")
        public float mountainAmplitude = 1f;

        @Range(min = 0, max = 2f, increment = 0.01f, precision = 2, description = "Hill Amplitude")
        public float hillAmplitude = 1f;

        @Override
        public void copyFrom(PerlinHillsAndMountainsProviderConfiguration other) {
            this.mountainAmplitude = other.mountainAmplitude;
            this.hillAmplitude = other.hillAmplitude;
        }
    }
}
