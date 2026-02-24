// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.particles;

import net.minecraft.core.particles.ParticleOptions;

import java.util.UUID;

public interface ControllableParticleEffect extends ParticleOptions {
    UUID getControlUUID();
    void setControlUUID(UUID uuid);
    boolean getFaceToPlayer();
    ControllableParticleEffect clone();
}