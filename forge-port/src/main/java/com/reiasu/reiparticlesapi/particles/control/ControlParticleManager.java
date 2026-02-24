package com.reiasu.reiparticlesapi.particles.control;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ControlParticleManager {

    public static final ControlParticleManager INSTANCE = new ControlParticleManager();

    private final ConcurrentHashMap<UUID, ParticleController> controls = new ConcurrentHashMap<>();

    private ControlParticleManager() {
    }

        public ParticleController getControl(UUID uuid) {
        return controls.get(uuid);
    }

        public void removeControl(UUID uuid) {
        controls.remove(uuid);
    }

        public ParticleController createControl(UUID uuid) {
        ParticleController controller = new ParticleController(uuid);
        controls.put(uuid, controller);
        return controller;
    }
}
