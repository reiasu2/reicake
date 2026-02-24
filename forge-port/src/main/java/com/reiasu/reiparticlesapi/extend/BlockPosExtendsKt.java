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
