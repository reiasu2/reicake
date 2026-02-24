// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.particles;

import com.reiasu.reiparticlesapi.particles.impl.*;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.BiFunction;

public final class ReiModParticles {
    public static final ReiModParticles INSTANCE = new ReiModParticles();

    private static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, "reiparticlesapi");

    public static final DeferredHolder<ParticleType<?>, ParticleType<ControllableEndRodEffect>> CONTROLLABLE_END_ROD =
            PARTICLES.register("controllable_end_rod",
                    () -> createType(ControllableEndRodEffect::new));

    public static final DeferredHolder<ParticleType<?>, ParticleType<ControllableEnchantmentEffect>> CONTROLLABLE_ENCHANTMENT =
            PARTICLES.register("controllable_enchantment",
                    () -> createType(ControllableEnchantmentEffect::new));

    public static final DeferredHolder<ParticleType<?>, ParticleType<ControllableCloudEffect>> CONTROLLABLE_CLOUD =
            PARTICLES.register("controllable_cloud",
                    () -> createType(ControllableCloudEffect::new));

    public static final DeferredHolder<ParticleType<?>, ParticleType<ControllableFlashEffect>> CONTROLLABLE_FLASH =
            PARTICLES.register("controllable_flash",
                    () -> createType(ControllableFlashEffect::new));

    public static final DeferredHolder<ParticleType<?>, ParticleType<ControllableFireworkEffect>> CONTROLLABLE_FIREWORK =
            PARTICLES.register("controllable_firework",
                    () -> createType(ControllableFireworkEffect::new));

    public static final DeferredHolder<ParticleType<?>, ParticleType<ControllableFallingDustEffect>> CONTROLLABLE_FALLING_DUST =
            PARTICLES.register("controllable_falling_dust",
                    () -> createType((uuid, face) -> new ControllableFallingDustEffect(
                            uuid, net.minecraft.world.level.block.Blocks.SAND.defaultBlockState(), face)));

    public static final DeferredHolder<ParticleType<?>, ParticleType<ControllableSplashEffect>> CONTROLLABLE_SPLASH =
            PARTICLES.register("controllable_splash",
                    () -> createType(ControllableSplashEffect::new));

    private ReiModParticles() {}

        private static <T extends ControllableParticleEffect> ParticleType<T> createType(
            BiFunction<UUID, Boolean, T> factory) {
        MapCodec<T> mapCodec = MapCodec.unit(() -> factory.apply(UUID.randomUUID(), false));
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return factory.apply(buf.readUUID(), buf.readBoolean());
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T value) {
                buf.writeUUID(value.getControlUUID());
                buf.writeBoolean(value.getFaceToPlayer());
            }
        };
        return new ParticleType<T>(true) {
            @Override
            public MapCodec<T> codec() {
                return mapCodec;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    }

        public static void register(IEventBus modBus) {
        PARTICLES.register(modBus);
    }

    public void reg() {
        // No-op, registration is handled by DeferredRegister
    }
}
