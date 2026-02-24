// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.emitters.event;

import com.reiasu.reiparticlesapi.network.particle.emitters.ControllableParticleData;
import com.reiasu.reiparticlesapi.particles.ControllableParticle;

public interface ParticleEvent {

        String getEventID();

        ControllableParticle getParticle();

        void setParticle(ControllableParticle particle);

        ControllableParticleData getParticleData();

        void setParticleData(ControllableParticleData data);

        boolean getCanceled();

        void setCanceled(boolean canceled);
}
