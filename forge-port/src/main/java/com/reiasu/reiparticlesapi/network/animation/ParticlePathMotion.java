// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.animation;

import com.reiasu.reiparticlesapi.network.animation.api.AbstractPathMotion;
import com.reiasu.reiparticlesapi.particles.ControllableParticle;
import net.minecraft.world.phys.Vec3;

public abstract class ParticlePathMotion extends AbstractPathMotion {
    private final ControllableParticle particle;

    protected ParticlePathMotion(Vec3 origin, ControllableParticle particle) {
        super(origin);
        this.particle = particle;
    }

    public final ControllableParticle getParticle() {
        return particle;
    }

    @Override
    public void apply(Vec3 actualPos) {
        if (particle != null) {
            particle.teleportTo(actualPos);
        }
    }

    @Override
    public boolean checkValid() {
        return particle != null && !particle.getDeath();
    }
}
