// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.extend;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public final class Vec3iExtendsKt {

    private Vec3iExtendsKt() {
    }

        public static Vec3 asVec3(Vec3i v) {
        return new Vec3(v.getX(), v.getY(), v.getZ());
    }
}
