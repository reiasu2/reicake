package com.reiasu.reiparticlesapi.network.animation.api;

import net.minecraft.world.phys.Vec3;

public interface PathMotion {
    int getCurrentTick();

    void setCurrentTick(int tick);

    Vec3 getOrigin();

    void setOrigin(Vec3 origin);

        void apply(Vec3 actualPos);

        Vec3 next();

        boolean checkValid();
}
