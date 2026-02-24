package com.reiasu.reiparticlesapi.network.particle.emitters.event;

import java.util.HashMap;
import java.util.Map;

public final class ParticleEventHandlerManager {

    public static final ParticleEventHandlerManager INSTANCE = new ParticleEventHandlerManager();

    private final Map<String, ParticleEventHandler> registerHandlers = new HashMap<>();

    private ParticleEventHandlerManager() {
    }

        public ParticleEventHandler getHandlerById(String id) {
        return registerHandlers.get(id);
    }

        public void register(ParticleEventHandler handler) {
        registerHandlers.put(handler.getHandlerID(), handler);
    }

        public boolean hasRegister(String id) {
        return registerHandlers.containsKey(id);
    }

        public void init() {
        // Fabric version used ReiAPIScanner for auto-registration.
        // Forge port: register handlers explicitly or use Forge event bus.
    }
}
