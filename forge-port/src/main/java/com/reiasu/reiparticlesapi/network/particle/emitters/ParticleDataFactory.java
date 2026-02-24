// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.emitters;

@FunctionalInterface
public interface ParticleDataFactory {

        ControllableParticleData create();
}
