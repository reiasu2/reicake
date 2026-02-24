// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.extend;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public final class BlockPosExtendsKt {

    private BlockPosExtendsKt() {
    }

        public static BlockPos ofFloored(Vec3 vec) {
        return BlockPos.containing(vec.x, vec.y, vec.z);
    }
}
