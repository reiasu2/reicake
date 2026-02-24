// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.util.geom;

public final class GraphMathHelper {
    private GraphMathHelper() {
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
}
