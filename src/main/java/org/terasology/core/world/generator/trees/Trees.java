/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.core.world.generator.trees;

import org.terasology.math.LSystemRule;
import org.terasology.world.block.BlockUri;

import com.google.common.collect.ImmutableMap;

/**
 * Creates trees based on the original
 */
public final class Trees {

    private Trees() {
        // no instances!
    }

    public static TreeGenerator oakTree() {
        return new TreeGeneratorLSystem(
                "FFFFFFA",
                ImmutableMap.<Character, LSystemRule>builder()
                        .put('A', new LSystemRule("[&FFBFA]////[&BFFFA]////[&FBFFA]", 1.0f))
                        .put('B', new LSystemRule("[&FFFA]////[&FFFA]////[&FFFA]", 0.8f))
                        .build(),
                4, (float) Math.toRadians(30))
                .setLeafType(new BlockUri("CoreAssets:GreenLeaf"))
                .setBarkType(new BlockUri("CoreAssets:OakTrunk"));
    }

    public static TreeGenerator oakVariationTree() {
        return new TreeGeneratorLSystem(
                "FFFFFFA",
                ImmutableMap.<Character, LSystemRule>builder()
                        .put('A', new LSystemRule("[&FFBFA]////[&BFFFA]////[&FBFFAFFA]", 1.0f))
                        .put('B', new LSystemRule("[&FFFAFFFF]////[&FFFAFFF]////[&FFFAFFAA]", 0.8f))
                        .build(),
                4, (float) Math.toRadians(35))
                .setLeafType(new BlockUri("CoreAssets:GreenLeaf"))
                .setBarkType(new BlockUri("CoreAssets:OakTrunk"));
    }

    public static TreeGenerator pineTree() {
        return new TreeGeneratorLSystem(
                "FBBBBFFFFFFFDFFDFFF[//FC]FFF[yyYYYA][yyYYYA]F",
                ImmutableMap.<Character, LSystemRule>builder()
                        .put('A', new LSystemRule("F[yyFFFA][YYFFFA][ZZYYFFFA][zzYYFFFA]", 0.8f))
                        .put('B', new LSystemRule("FF", 0.6f))
                        .put('C', new LSystemRule("FFA", 0.2f))
                        .put('D', new LSystemRule("[yyyC]", 0.2f))
                        .put('Z', new LSystemRule("+", 0.4f))   // rotate around Z-axis
                        .put('z', new LSystemRule("-", 0.4f))   // rotate around Z-axis
                        .put('y', new LSystemRule("&", 0.4f))   // rotate around Y-axis
                        .put('Y', new LSystemRule("^", 0.4f))   // rotate around Y-axis
                        .build(),
                3, (float) Math.toRadians(30), 1)
                .setLeafType(new BlockUri("CoreAssets:DarkLeaf"))
                .setBarkType(new BlockUri("CoreAssets:PineTrunk"));
    }

    public static TreeGenerator birchTree() {
        return new TreeGeneratorLSystem(
                "FEEFFFAFFFBFFFFAFFFFBFFFFAFFFFBFF",
                ImmutableMap.<Character, LSystemRule>builder()
                        .put('x', new LSystemRule("/", 0.33f))
                        .put('y', new LSystemRule("&", 0.33f))
                        .put('A', new LSystemRule("[yFFFAFE]xxxx[yFFAFE]xxxx[yFEAFE]", 1.0f))
                        .put('B', new LSystemRule("[yFAF]xxxx[yFAF]xxxx[yFAF]", 0.8f))
                        .put('E', new LSystemRule("FF", 0.5f))
                        .build(),
                4, (float) Math.toRadians(35), 1)
                .setLeafType(new BlockUri("CoreAssets:LightLeaf"))
                .setBarkType(new BlockUri("CoreAssets:BirchTrunk"));
    }

    public static TreeGenerator redTree() {
        return new TreeGeneratorLSystem(
                "FFFFFAFAFAF",
                ImmutableMap.<Character, LSystemRule>builder()
                        .put('A', new LSystemRule("[&FFAFF]////[&FFAFF]////[&FFAFF]", 1.0f))
                        .build(),
                4, (float) Math.toRadians(40))
                .setLeafType(new BlockUri("CoreAssets:RedLeaf"))
                .setBarkType(new BlockUri("CoreAssets:OakTrunk"));
    }

    public static TreeGenerator cactus() {
        return new TreeGeneratorCactus()
                .setTrunkType(new BlockUri("CoreAssets:Cactus"));
    }
}
