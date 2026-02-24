// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;

public final class ParticleHelper {

    private ParticleHelper() {
    }

    public static <T extends ParticleOptions> void sendForce(
            ServerLevel level, T type,
            double x, double y, double z,
            int count,
            double xDist, double yDist, double zDist,
            double speed
    ) {
        level.sendParticles(
                type, x, y, z, count,
                xDist, yDist, zDist, speed
        );
    }
}
