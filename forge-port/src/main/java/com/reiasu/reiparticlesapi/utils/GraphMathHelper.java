// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class GraphMathHelper {
    private GraphMathHelper() {
    }

        public static Vec3 lerp(float alpha, Vec3 from, Vec3 to) {
        return new Vec3(
                Mth.lerp((double) alpha, from.x, to.x),
                Mth.lerp((double) alpha, from.y, to.y),
                Mth.lerp((double) alpha, from.z, to.z)
        );
    }

    public static double inverseLerp(double value, double min, double max) {
        if (max == min) {
            return 0.0;
        }
        return (value - min) / (max - min);
    }

    public static double smoothstep(double edge0, double edge1, double x) {
        double t = Math.clamp(inverseLerp(x, edge0, edge1), 0.0, 1.0);
        return t * t * (3.0 - 2.0 * t);
    }

        public static double inversePowerFalloff(double dist, double range, double power) {
        if (range <= 0.0) {
            return 0.0;
        }
        double ratio = dist / range;
        return 1.0 / (1.0 + Math.pow(ratio, power));
    }

        public static double expDampFactor(double damping, double dt) {
        double factor = Math.exp(-damping * dt);
        return Math.max(0.0, Math.min(1.0, factor));
    }
}
