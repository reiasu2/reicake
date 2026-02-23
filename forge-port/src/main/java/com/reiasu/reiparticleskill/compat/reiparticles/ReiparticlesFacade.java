// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.compat.reiparticles;

import org.slf4j.Logger;

public interface ReiparticlesFacade {
    boolean isOperational();

    void bootstrap(Logger logger);

    void registerParticleStyles(Logger logger);

    void registerTestHooks(Logger logger);

    void registerKeyBindings(Logger logger);
}