// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.utils.helper.emitters;

import net.minecraft.world.phys.Vec3;

public final class LinearResistanceHelper {
        public static Vec3 setPercentageVelocity(Vec3 enter, double percent) {
        return enter.scale(percent);
    }
}
