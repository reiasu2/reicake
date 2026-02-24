package com.reiasu.reiparticlesapi.particles;

import net.minecraft.core.particles.ParticleOptions;

import java.util.UUID;

public interface ControllableParticleEffect extends ParticleOptions {
    UUID getControlUUID();
    void setControlUUID(UUID uuid);
    boolean getFaceToPlayer();
    ControllableParticleEffect clone();
}