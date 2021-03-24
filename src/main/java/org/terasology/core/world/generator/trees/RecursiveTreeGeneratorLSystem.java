// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.core.world.generator.trees;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.engine.math.LSystemRule;
import org.terasology.math.TeraMath;
import org.terasology.engine.utilities.collection.CharSequenceIterator;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.chunks.Chunk;

import java.util.Map;

/**
 * Encapsulates the recursive algorithm for the generation of trees
 */

public class RecursiveTreeGeneratorLSystem {
    private final int maxDepth;
    private final float angle;
    private final float thickness;
    private final Map<Character, LSystemRule> ruleSet;

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

    public void recurse(Chunk view, Random rand,
                        int posX, int posY, int posZ,
                        float angleOffset,
                        CharSequenceIterator axiomIterator,
                        Vector3f position,
                        Quaternionf rotation,
                        Block bark,
                        Block leaf,
                        int depth,
                        AbstractTreeGenerator treeGenerator) {
        Quaternionf tempRotation = new Quaternionf();
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

                    Vector3f dir = new Vector3f(1, 0, 0);
                    rotation.transform(dir);

                    position.add(dir);
                    break;
                case '[':
                    recurse(view, rand, posX, posY, posZ, angleOffset, axiomIterator, new Vector3f(position),
                            new Quaternionf(rotation), bark, leaf, depth, treeGenerator);
                    break;
                case ']':
                    return;
                case '+':
                    tempRotation.setAngleAxis(angle + angleOffset, 0, 0, 1);
                    rotation.mul(tempRotation);
                    break;
                case '-':
                    tempRotation.setAngleAxis(angle + angleOffset, 0, 0, -1);
                    rotation.mul(tempRotation);
                    break;
                case '&':
                    tempRotation.setAngleAxis(angle + angleOffset, 0, 1, 0);
                    rotation.mul(tempRotation);
                    break;
                case '^':
                    tempRotation.setAngleAxis(angle + angleOffset, 0, -1, 0);

                    rotation.mul(tempRotation);
                    break;
                case '*':
                    tempRotation.setAngleAxis(angle + angleOffset, 1, 0, 0);
                    rotation.mul(tempRotation);
                    break;
                case '/':
                    tempRotation.setAngleAxis(angle + angleOffset, -1, 0, 0);
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
