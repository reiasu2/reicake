// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.sounds;

import com.reiasu.reiparticleskill.ReiParticleSkillForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SkillSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, ReiParticleSkillForge.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SWORD_FORMATION =
            SOUND_EVENTS.register("sword_formation",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReiParticleSkillForge.MOD_ID, "sword_formation")));

    private SkillSoundEvents() {
    }

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
