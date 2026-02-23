// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.compat.version.forge120;

import com.reiasu.reiparticlesapi.compat.version.ClientCameraVersionBridge;
import net.minecraft.client.player.LocalPlayer;

public final class Forge120ClientCameraBridge implements ClientCameraVersionBridge {
    @Override
    public void applyShakeTurn(LocalPlayer player, float yawShake, float pitchShake) {
        player.turn(yawShake, pitchShake);
    }
}
