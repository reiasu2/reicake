// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
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
