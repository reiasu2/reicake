// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.data;

import java.util.concurrent.ThreadLocalRandom;

public final class IntRangeData extends RangeData<Integer> {

    public IntRangeData(int min, int max) {
        super(min, max);
    }

        public int random() {
        return ThreadLocalRandom.current().nextInt(getMin(), getMax());
    }
}
