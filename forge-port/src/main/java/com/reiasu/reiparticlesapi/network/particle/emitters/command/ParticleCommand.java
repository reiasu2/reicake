package com.reiasu.reiparticlesapi.network.particle.emitters.command;

import com.reiasu.reiparticlesapi.network.particle.emitters.ControllableParticleData;
import com.reiasu.reiparticlesapi.particles.ControllableParticle;

@FunctionalInterface
public interface ParticleCommand {

        void execute(ControllableParticleData data, ControllableParticle particle);
}
