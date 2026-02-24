package com.reiasu.reiparticlesapi.network.particle.emitters;

import com.reiasu.reiparticlesapi.network.particle.data.SerializableData;
import com.reiasu.reiparticlesapi.particles.ParticleDisplayer;

public final class DisplayableEmitterData implements SerializableData {

    @Override
    public SerializableData clone() {
        throw new UnsupportedOperationException("DisplayableEmitterData.clone() not yet implemented");
    }

    @Override
    public ParticleDisplayer createDisplayer() {
        throw new UnsupportedOperationException("DisplayableEmitterData.createDisplayer() not yet implemented");
    }
}
