// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.compat.version;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public interface ModLifecycleVersionBridge {
    void registerClientSetup(Runnable callback);

    void registerCommandRegistration(Consumer<CommandDispatcher<CommandSourceStack>> callback);

    void registerServerEndTick(Consumer<MinecraftServer> callback);
}
