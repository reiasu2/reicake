// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.network.packet.client.listener.ClientParticlePacketHandler;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.phys.Vec3;


public record PacketParticleS2C(ParticleOptions particleOptions, Vec3 pos, Vec3 velocity) implements CustomPacketPayload {
    public static final Type<PacketParticleS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "packet_particle_s2_c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleS2C> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), PacketParticleS2C::decode);

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void encode(PacketParticleS2C packet, RegistryFriendlyByteBuf buf) {
        ResourceLocation id = BuiltInRegistries.PARTICLE_TYPE.getKey(packet.particleOptions.getType());
        buf.writeResourceLocation(id);
        ParticleType type = packet.particleOptions.getType();
        ((StreamCodec) type.streamCodec()).encode(buf, packet.particleOptions);
        buf.writeDouble(packet.pos.x);
        buf.writeDouble(packet.pos.y);
        buf.writeDouble(packet.pos.z);
        buf.writeDouble(packet.velocity.x);
        buf.writeDouble(packet.velocity.y);
        buf.writeDouble(packet.velocity.z);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static PacketParticleS2C decode(RegistryFriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        ParticleType type = BuiltInRegistries.PARTICLE_TYPE.getOptional(id).orElse(null);
        ParticleOptions options;
        if (type != null) {
            options = (ParticleOptions) type.streamCodec().decode(buf);
        } else {
            options = ParticleTypes.END_ROD;
        }
        Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 velocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        return new PacketParticleS2C(options, pos, velocity);
    }

    public static void handle(PacketParticleS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientParticlePacketHandler.receive(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
