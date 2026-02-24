// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.particles;

import net.minecraft.world.phys.Vec3;

public interface ParticleLerpInterpolator {
        Vec3 consume(Vec3 prev, Vec3 current, float delta);
}
