package com.reiasu.reiparticlesapi.network.particle.emitters.event;

public interface ParticleEventHandler extends Comparable<ParticleEventHandler> {

        void handle(ParticleEvent event);

        String getTargetEventID();

        String getHandlerID();

        int getPriority();

    @Override
    default int compareTo(ParticleEventHandler other) {
        return this.getPriority() - other.getPriority();
    }
}
