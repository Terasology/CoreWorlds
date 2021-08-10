// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.utilities.procedural;

import org.terasology.engine.utilities.random.Random;
import org.terasology.math.TeraMath;

import java.util.Deque;
import java.util.LinkedList;

public class ChanceRandomizer<T> {
    private int granularity;

    private float chanceSum;
    private Deque<ObjectChance<T>> objectChances = new LinkedList<>();

    private Object[] lookupArray;

    public ChanceRandomizer(int granularity) {
        this.granularity = granularity;
        lookupArray = new Object[granularity];
    }

    public void addChance(float chance, T object) {
        objectChances.add(new ObjectChance<T>(chance, object));
        chanceSum += chance;
    }

    public void initialize() {
        int index = 0;
        for (ObjectChance<T> objectChance : objectChances) {
            int maxIndex = index + ((int) ((objectChance.chance / chanceSum) * granularity));
            for (int i = index; i < maxIndex; i++) {
                lookupArray[i] = objectChance.object;
            }
            index = maxIndex;
        }
        if (index < granularity && !objectChances.isEmpty()) {
            for (int i = index; i < granularity; i++) {
                lookupArray[i] = objectChances.getLast().object;
            }
        }
    }

    public T randomizeObject(Random random) {
        return (T) lookupArray[random.nextInt(granularity)];
    }

    public T getObject(float value) {
        return (T) lookupArray[TeraMath.floorToInt(value * lookupArray.length)];
    }

    private static final class ObjectChance<T> {
        private float chance;
        private T object;

        private ObjectChance(float chance, T object) {
            this.chance = chance;
            this.object = object;
        }
    }
}
