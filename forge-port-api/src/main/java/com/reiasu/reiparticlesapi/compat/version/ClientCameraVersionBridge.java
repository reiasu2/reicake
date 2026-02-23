// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.compat.version;

import net.minecraft.client.player.LocalPlayer;

public interface ClientCameraVersionBridge {
    void applyShakeTurn(LocalPlayer player, float yawShake, float pitchShake);
}
