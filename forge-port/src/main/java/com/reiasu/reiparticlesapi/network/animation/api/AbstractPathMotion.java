package com.reiasu.reiparticlesapi.network.animation.api;

import net.minecraft.world.phys.Vec3;

public abstract class AbstractPathMotion implements PathMotion {
    private Vec3 origin;
    private int currentTick;

    protected AbstractPathMotion(Vec3 origin) {
        this.origin = origin;
    }

    @Override
    public Vec3 getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(Vec3 origin) {
        this.origin = origin;
    }

    @Override
    public int getCurrentTick() {
        return currentTick;
    }

    @Override
    public void setCurrentTick(int tick) {
        this.currentTick = tick;
    }

    @Override
    public Vec3 next() {
        Vec3 value = pathFunction();
        currentTick++;
        return value;
    }

        public abstract Vec3 pathFunction();
}
