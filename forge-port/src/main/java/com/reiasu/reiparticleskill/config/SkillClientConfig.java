// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class SkillClientConfig {

    public static final ModConfigSpec SPEC;
    public static final SkillClientConfig INSTANCE;

    static {
        Pair<SkillClientConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(SkillClientConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private final ModConfigSpec.BooleanValue suppressCrystalBeam;

    private SkillClientConfig(ModConfigSpec.Builder builder) {
        builder.push("rendering");

        suppressCrystalBeam = builder
                .comment("Suppress the vanilla End Crystal beam so custom particle effects are visible.",
                         "Set to false to restore the vanilla beam (for modpack compatibility).")
                .define("suppressCrystalBeam", true);

        builder.pop();
    }

    public boolean isSuppressCrystalBeam() {
        try {
            return suppressCrystalBeam.get();
        } catch (IllegalStateException e) {
            return true;
        }
    }
}
