// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.compat.reiparticles;

import com.reiasu.reiparticleskill.compat.interop.ReiparticlesInterop;

public final class ReiparticlesFacadeProvider {
    private static final ReiparticlesFacade INSTANCE = create();

    private ReiparticlesFacadeProvider() {
    }

    public static ReiparticlesFacade get() {
        return INSTANCE;
    }

    private static ReiparticlesFacade create() {
        if (ReiparticlesInterop.isApiPresent()) {
            return new ReflectiveReiparticlesFacade();
        }
        return new NoopReiparticlesFacade();
    }
}
