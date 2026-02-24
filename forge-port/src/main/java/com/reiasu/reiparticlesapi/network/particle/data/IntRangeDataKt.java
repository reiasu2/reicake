// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.data;

public final class IntRangeDataKt {

    private IntRangeDataKt() {
    }

        public static boolean isIn(int value, IntRangeData range) {
        return value >= range.getMin() && value <= range.getMax();
    }

        public static IntRangeData minRangeTo(int min, int max) {
        return new IntRangeData(min, max);
    }

        public static IntRangeData maxRangeTo(int max, int min) {
        return new IntRangeData(min, max);
    }
}
