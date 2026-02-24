package com.reiasu.reiparticlesapi.particles;

import net.minecraft.world.phys.Vec3;

public interface ParticleLerpInterpolator {
        Vec3 consume(Vec3 prev, Vec3 current, float delta);
}
