// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.data;

import com.reiasu.reiparticlesapi.particles.ParticleDisplayer;

public interface SerializableData {

        SerializableData clone();

        ParticleDisplayer createDisplayer();
}
