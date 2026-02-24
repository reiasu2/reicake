// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.client;

/**
 * Formerly suppressed End Crystal beam via brute-force event listeners
 * (ClientTickEvent + RenderLevelStageEvent) that iterated all entities.
 * <p>
 * Removed to reduce mod conflicts: the {@code @EventBusSubscriber} caused
 * this class to always participate in the event chain regardless of config,
 * and mutating entity state globally conflicted with other mods.
 * <p>
 * Beam suppression is now handled solely by
 * {@link com.reiasu.reiparticleskill.mixin.EndCrystalRendererMixin},
 * which uses a surgical {@code @Redirect} on {@code getBeamTarget()} and
 * is gated by {@link com.reiasu.reiparticleskill.config.SkillClientConfig#isSuppressCrystalBeam()}.
 *
 * @deprecated No longer used. Will be removed in a future version.
 */
@Deprecated
public final class EndCrystalBeamSuppressor {
    private EndCrystalBeamSuppressor() {
    }
}
