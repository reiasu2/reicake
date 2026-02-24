// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class APIConfig {

    public static final ModConfigSpec SPEC;
    public static final APIConfig INSTANCE;

    static {
        Pair<APIConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(APIConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private final ModConfigSpec.BooleanValue enabledParticleCountInject;
    private final ModConfigSpec.BooleanValue enabledParticleAsync;
    private final ModConfigSpec.IntValue particleCountLimit;
    private final ModConfigSpec.IntValue calculateThreadCount;
    private final ModConfigSpec.IntValue packetsPerTickLimit;
    private final ModConfigSpec.IntValue maxEmitterVisibleRange;

    private APIConfig(ModConfigSpec.Builder builder) {
        builder.push("particles");

        enabledParticleCountInject = builder
                .comment("Enable particle count injection (overrides vanilla limits)")
                .define("enabledParticleCountInject", true);

        enabledParticleAsync = builder
                .comment("Enable async particle calculations")
                .define("enabledParticleAsync", true);

        particleCountLimit = builder
                .comment("Maximum number of particles allowed in the world")
                .defineInRange("particleCountLimit", 131072, 1, 1_000_000);

        calculateThreadCount = builder
                .comment("Number of threads for particle calculations")
                .defineInRange("calculateThreadCount", 4, 1, 64);

        packetsPerTickLimit = builder
                .comment("Maximum emitter sync packets sent per server tick (global)")
                .defineInRange("packetsPerTickLimit", 512, 16, 4096);

        maxEmitterVisibleRange = builder
                .comment("Maximum visible range (blocks) for emitter sync packets")
                .defineInRange("maxEmitterVisibleRange", 256, 32, 1024);

        builder.pop();
    }

    public boolean isEnabledParticleCountInject() { return safeGet(enabledParticleCountInject, true); }
    public void setEnabledParticleCountInject(boolean v) { enabledParticleCountInject.set(v); }

    public boolean isEnabledParticleAsync() { return safeGet(enabledParticleAsync, true); }
    public void setEnabledParticleAsync(boolean v) { enabledParticleAsync.set(v); }

    public int getParticleCountLimit() { return safeGet(particleCountLimit, 131072); }
    public void setParticleCountLimit(int v) { particleCountLimit.set(Math.max(1, v)); }

    public int getCalculateThreadCount() { return safeGet(calculateThreadCount, 4); }
    public void setCalculateThreadCount(int v) { calculateThreadCount.set(Math.max(1, v)); }

    public int getPacketsPerTickLimit() { return safeGet(packetsPerTickLimit, 512); }
    public int getMaxEmitterVisibleRange() { return safeGet(maxEmitterVisibleRange, 256); }

    private static <T> T safeGet(ModConfigSpec.ConfigValue<T> value, T fallback) {
        try {
            return value.get();
        } catch (IllegalStateException e) {
            return fallback;
        }
    }
}
