// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.compat.version;

import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationVersionBridge {
    ResourceLocation modLocation(String modId, String path);
}
