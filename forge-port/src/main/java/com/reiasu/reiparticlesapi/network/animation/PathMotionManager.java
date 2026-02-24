// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.animation;

import com.reiasu.reiparticlesapi.network.animation.api.PathMotion;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class PathMotionManager {
    public static final PathMotionManager INSTANCE = new PathMotionManager();
    private static final Set<PathMotion> motions = new HashSet<>();

    private PathMotionManager() {
    }

    public Set<PathMotion> getMotions() {
        return motions;
    }

        public void applyMotion(PathMotion motion) {
        motions.add(motion);
    }

        public void tick() {
        Iterator<PathMotion> iterator = motions.iterator();
        while (iterator.hasNext()) {
            PathMotion motion = iterator.next();
            if (!motion.checkValid()) {
                iterator.remove();
                continue;
            }
            Vec3 offset = motion.next();
            Vec3 pos = offset.add(motion.getOrigin());
            motion.apply(pos);
        }
    }
}
