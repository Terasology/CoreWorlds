/*
 * Copyright 2015 MovingBlocks
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

import java.util.Map;

import com.google.common.primitives.Floats;
import org.terasology.math.LSystemRule;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Matrix4f;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.utilities.collection.CharSequenceIterator;
import org.terasology.utilities.random.Random;
import org.terasology.world.block.Block;
import org.terasology.world.chunks.CoreChunk;

/**
 * Encapsulates the recursive algorithm for the generation of trees
 */

public class RecursiveTreeGeneratorLSystem {
    private int maxDepth;
    private float angle;
    private float thickness;
    private Map<Character, LSystemRule> ruleSet;

    public RecursiveTreeGeneratorLSystem(int maxDepth, float angle, Map<Character, LSystemRule> ruleSet,
                                         float thickness) {
        this.angle = angle;
        this.maxDepth = maxDepth;
        this.ruleSet = ruleSet;
        this.thickness = thickness;
    }

    public RecursiveTreeGeneratorLSystem(int maxDepth, float angle, Map<Character, LSystemRule> ruleSet) {
        this(maxDepth, angle, ruleSet, 3);
    }

    public void recurse(CoreChunk view, Random rand,
                        int posX, int posY, int posZ,
                        float angleOffset,
                        CharSequenceIterator axiomIterator,
                        Vector3f position,
                        Matrix4f rotation,
                        Block bark,
                        Block leaf,
                        int depth,
                        AbstractTreeGenerator treeGenerator) {
        Matrix4f tempRotation = new Matrix4f();
        while (axiomIterator.hasNext()) {
            char c = axiomIterator.nextChar();
            switch (c) {
                case 'G':
                case 'F':
                    // Tree trunk
                    float radius = TeraMath.fastFloor(thickness / 2);
                    Vector3i p =
                            new Vector3i(posX + (int) position.x, posY + (int) position.y, posZ + (int) position.z);

                    // placing a "cylinder" around 'p' with h=1 and r='radius'
                    for (int dx = 0; dx <= radius; dx++) {
                        for (int dz = 0; dz <= radius; dz++) {
                            if (dx * dx + dz * dz <= radius * radius) {
                                treeGenerator.safelySetBlock(view, p.x + dx, p.y, p.z + dz, bark);
                                treeGenerator.safelySetBlock(view, p.x + dx, p.y, p.z - dz, bark);
                                treeGenerator.safelySetBlock(view, p.x - dx, p.y, p.z + dz, bark);
                                treeGenerator.safelySetBlock(view, p.x - dx, p.y, p.z - dz, bark);
                            }
                        }
                    }

                    // Generate leaves
                    if (depth > 1) {
                        int size = 1;
                        // placing a "sphere" around 'p' with r=~1.5?
                        //
                        // □ □ ■ □ □
                        // □ ■ ■ ■ □
                        // ■ ■ ⛝ ■ ■
                        // □ ■ ■ ■ □
                        // □ □ ■ □ □
                        //
                        for (int x = -size; x <= size; x++) {
                            for (int y = -size; y <= size; y++) {
                                for (int z = -size; z <= size; z++) {
                                    if (Math.abs(x) == size && Math.abs(y) == size && Math.abs(z) == size) {
                                        continue;
                                    }
                                    treeGenerator.safelySetBlock(view, p.x + x + 1, p.y + y, p.z + z, leaf);
                                    treeGenerator.safelySetBlock(view, p.x + x - 1, p.y + y, p.z + z, leaf);
                                    treeGenerator.safelySetBlock(view, p.x + x, p.y + y, p.z + z + 1, leaf);
                                    treeGenerator.safelySetBlock(view, p.x + x, p.y + y, p.z + z - 1, leaf);
                                }
                            }
                        }
                    }

                    Vector3f dir = new Vector3f(1f, 0f, 0f);
                    rotation.transformVector(dir);

                    position.add(dir);
                    break;
                case '[':
                    recurse(view, rand, posX, posY, posZ, angleOffset, axiomIterator, new Vector3f(position),
                            new Matrix4f(rotation), bark, leaf, depth, treeGenerator);
                    break;
                case ']':
                    return;
                case '+':
                    tempRotation = new Matrix4f(new Quat4f(new Vector3f(0f, 0f, 1f), angle + angleOffset),
                            Vector3f.ZERO, 1.0f);
                    rotation.mul(tempRotation);
                    break;
                case '-':
                    tempRotation = new Matrix4f(new Quat4f(new Vector3f(0f, 0f, -1f), angle + angleOffset),
                            Vector3f.ZERO, 1.0f);
                    rotation.mul(tempRotation);
                    break;
                case '&':
                    tempRotation = new Matrix4f(new Quat4f(new Vector3f(0f, 1f, 0f), angle + angleOffset),
                            Vector3f.ZERO, 1.0f);
                    rotation.mul(tempRotation);
                    break;
                case '^':
                    tempRotation = new Matrix4f(new Quat4f(new Vector3f(0f, -1f, 0f), angle + angleOffset),
                            Vector3f.ZERO, 1.0f);
                    rotation.mul(tempRotation);
                    break;
                case '*':
                    tempRotation = new Matrix4f(new Quat4f(new Vector3f(1f, 0f, 0f), angle), Vector3f.ZERO, 1.0f);
                    rotation.mul(tempRotation);
                    break;
                case '/':
                    tempRotation = new Matrix4f(new Quat4f(new Vector3f(-1f, 0f, 0f), angle), Vector3f.ZERO, 1.0f);
                    rotation.mul(tempRotation);
                    break;
                default:
                    // If we have already reached the maximum depth, don't ever bother to lookup in the map
                    if (depth == maxDepth - 1) {
                        break;
                    }
                    LSystemRule rule = ruleSet.get(c);
                    if (rule == null) {
                        break;
                    }

                    float weightedFailureProbability = TeraMath.pow(1f - rule.getProbability(), maxDepth - depth);
                    if (rand.nextFloat() < weightedFailureProbability) {
                        break;
                    }

                    recurse(view, rand, posX, posY, posZ, angleOffset, new CharSequenceIterator(rule.getAxiom()),
                            position, rotation, bark, leaf, depth + 1, treeGenerator);
            }
        }
    }
}
