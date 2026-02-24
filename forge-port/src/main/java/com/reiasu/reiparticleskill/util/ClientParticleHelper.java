package com.reiasu.reiparticleskill.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public final class ClientParticleHelper {

    private static final RandomSource RANDOM = RandomSource.create();

    private ClientParticleHelper() {
    }

    public static <T extends ParticleOptions> void addForce(
            Level level, T type,
            double x, double y, double z,
            int count,
            double xDist, double yDist, double zDist,
            double speed
    ) {
        if (level == null || !level.isClientSide()) return;

        if (count == 0) {
            // Exact velocity mode: 1 particle, dist params = velocity
            level.addAlwaysVisibleParticle(type, true,
                    x, y, z, xDist, yDist, zDist);
            return;
        }

        for (int i = 0; i < count; i++) {
            double ox = RANDOM.nextGaussian() * xDist;
            double oy = RANDOM.nextGaussian() * yDist;
            double oz = RANDOM.nextGaussian() * zDist;
            double sx = RANDOM.nextGaussian() * speed;
            double sy = RANDOM.nextGaussian() * speed;
            double sz = RANDOM.nextGaussian() * speed;
            level.addAlwaysVisibleParticle(type, true,
                    x + ox, y + oy, z + oz, sx, sy, sz);
        }
    }
}
