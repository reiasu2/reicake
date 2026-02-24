// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.entities;

import com.reiasu.reiparticleskill.ReiParticleSkillForge;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SkillEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ReiParticleSkillForge.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<BarrageItemEntity>> BARRAGE_ITEM =
            ENTITIES.register("barrage_item", () ->
                    EntityType.Builder.<BarrageItemEntity>of(BarrageItemEntity::new, MobCategory.MISC)
                            .sized(0.4F, 0.4F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(ReiParticleSkillForge.MOD_ID + ":barrage_item"));

    private SkillEntityTypes() {
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
