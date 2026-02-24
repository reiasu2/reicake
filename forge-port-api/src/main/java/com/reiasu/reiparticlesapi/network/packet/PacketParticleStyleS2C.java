// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.network.buffer.ParticleControllerDataBuffer;
import com.reiasu.reiparticlesapi.network.buffer.ParticleControllerDataBuffers;
import com.reiasu.reiparticlesapi.network.packet.client.listener.ClientParticleStylePacketHandler;
import com.reiasu.reiparticlesapi.particles.control.ControlType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record PacketParticleStyleS2C(
        UUID uuid,
        ControlType controlType,
        Map<String, ParticleControllerDataBuffer<?>> args
) implements CustomPacketPayload {
    public static final Type<PacketParticleStyleS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "packet_particle_style_s2_c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleStyleS2C> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), PacketParticleStyleS2C::decode);

    public PacketParticleStyleS2C {
        args = Map.copyOf(args);
    }

    public static void encode(PacketParticleStyleS2C packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.uuid);
        buf.writeInt(packet.controlType.getId());
        buf.writeInt(packet.args.size());
        for (Map.Entry<String, ParticleControllerDataBuffer<?>> entry : packet.args.entrySet()) {
            byte[] encoded = ParticleControllerDataBuffers.INSTANCE.encode(entry.getValue());
            buf.writeInt(encoded.length);
            buf.writeUtf(entry.getKey());
            buf.writeBytes(encoded);
        }
    }

    public static PacketParticleStyleS2C decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        ControlType type = ControlType.getTypeById(buf.readInt());
        int argsCount = buf.readInt();
        Map<String, ParticleControllerDataBuffer<?>> args = new HashMap<>();
        for (int i = 0; i < argsCount; i++) {
            int len = buf.readInt();
            String key = buf.readUtf();
            byte[] raw = new byte[len];
            buf.readBytes(raw);
            args.put(key, ParticleControllerDataBuffers.INSTANCE.decodeToBuffer(raw));
        }
        return new PacketParticleStyleS2C(uuid, type, args);
    }

    public static void handle(PacketParticleStyleS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientParticleStylePacketHandler.receive(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
