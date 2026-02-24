package com.reiasu.reiparticlesapi.network.particle.data;

import com.reiasu.reiparticlesapi.particles.ParticleDisplayer;

public interface SerializableData {

        SerializableData clone();

        ParticleDisplayer createDisplayer();
}
