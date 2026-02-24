package com.reiasu.reiparticlesapi.particles.control.group;

import com.reiasu.reiparticlesapi.network.buffer.ParticleControllerDataBuffer;

import java.util.Map;
import java.util.UUID;

@Deprecated
public interface ControllableParticleGroupProvider {

        ControllableParticleGroup createGroup(UUID uuid, Map<String, ? extends ParticleControllerDataBuffer<?>> args);

        void changeGroup(ControllableParticleGroup group, Map<String, ? extends ParticleControllerDataBuffer<?>> args);
}
