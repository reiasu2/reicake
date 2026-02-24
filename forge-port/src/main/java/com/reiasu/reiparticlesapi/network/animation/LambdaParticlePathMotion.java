// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.animation;

import com.reiasu.reiparticlesapi.particles.ControllableParticle;
import net.minecraft.world.phys.Vec3;

import java.util.function.IntFunction;

public final class LambdaParticlePathMotion extends ParticlePathMotion {
    private final IntFunction<Vec3> path;

        public LambdaParticlePathMotion(Vec3 origin, ControllableParticle particle, IntFunction<Vec3> path) {
        super(origin, particle);
        this.path = path;
    }

    public IntFunction<Vec3> getPath() {
        return path;
    }

    @Override
    public Vec3 pathFunction() {
        return path.apply(getCurrentTick());
    }
}
