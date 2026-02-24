// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.network.packet.client.listener.ClientParticleEmittersPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record PacketParticleEmittersS2C(ResourceLocation emitterKey, byte[] emitterData, PacketType packetType) implements CustomPacketPayload {
    public static final Type<PacketParticleEmittersS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "packet_particle_emitters_s2_c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleEmittersS2C> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), PacketParticleEmittersS2C::decode);

    public enum PacketType {
        CHANGE_OR_CREATE(0),
        REMOVE(1);

        private final int id;

        PacketType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PacketType fromID(int id) {
            return switch (id) {
                case 0 -> CHANGE_OR_CREATE;
                case 1 -> REMOVE;
                default -> CHANGE_OR_CREATE;
            };
        }
    }

    public static void encode(PacketParticleEmittersS2C packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.packetType.getId());
        buf.writeResourceLocation(packet.emitterKey);
        buf.writeVarInt(packet.emitterData.length);
        buf.writeBytes(packet.emitterData);
    }

    public static PacketParticleEmittersS2C decode(FriendlyByteBuf buf) {
        PacketType packetType = PacketType.fromID(buf.readVarInt());
        ResourceLocation key = buf.readResourceLocation();
        int size = buf.readVarInt();
        byte[] data = new byte[size];
        buf.readBytes(data);
        return new PacketParticleEmittersS2C(key, data, packetType);
    }

    public static void handle(PacketParticleEmittersS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientParticleEmittersPacketHandler.receive(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
