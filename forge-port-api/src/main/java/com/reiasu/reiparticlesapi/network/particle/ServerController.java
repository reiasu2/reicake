// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle;

public interface ServerController<T> {
    default void tick() {
    }

    default boolean getCanceled() {
        return false;
    }

    default void teleportTo(net.minecraft.world.phys.Vec3 pos) {
    }

    default void cancel() {
    }
}