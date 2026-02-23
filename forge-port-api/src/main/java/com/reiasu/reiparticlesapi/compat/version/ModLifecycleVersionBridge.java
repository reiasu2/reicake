// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.compat.version;

import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public interface ModLifecycleVersionBridge {
    void registerClientSetup(Runnable callback);

    void registerClientStartTick(Runnable callback);

    void registerClientEndTick(Runnable callback);

    void registerServerStartTick(Consumer<MinecraftServer> callback);

    void registerServerEndTick(Consumer<MinecraftServer> callback);
}
