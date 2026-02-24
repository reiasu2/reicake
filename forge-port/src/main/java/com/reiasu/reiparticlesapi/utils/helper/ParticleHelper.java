package com.reiasu.reiparticlesapi.utils.helper;

import com.reiasu.reiparticlesapi.particles.Controllable;

public interface ParticleHelper {
    default void loadController(Controllable<?> controller) {
    }

    default void initHelper() {
    }
}
