// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.network.buffer.ParticleControllerDataBuffer;
import com.reiasu.reiparticlesapi.network.buffer.ParticleControllerDataBuffers;
import com.reiasu.reiparticlesapi.network.packet.client.listener.ClientParticleGroupPacketHandler;
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

public record PacketParticleGroupS2C(
        UUID uuid,
        ControlType controlType,
        Map<String, ParticleControllerDataBuffer<?>> args
) implements CustomPacketPayload {
    public static final Type<PacketParticleGroupS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "packet_particle_group_s2_c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleGroupS2C> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), PacketParticleGroupS2C::decode);

    public enum PacketArgsType {
        POS("pos"),
        CURRENT_TICK("current_tick"),
        MAX_TICK("max_tick"),
        ROTATE_TO("rotate_to"),
        ROTATE_AXIS("rotate_axis"),
        INVOKE("invoke"),
        AXIS("axis"),
        SCALE("scale"),
        GROUP_TYPE("groupType");

        private final String ofArgs;

        PacketArgsType(String ofArgs) {
            this.ofArgs = ofArgs;
        }

        public String getOfArgs() {
            return ofArgs;
        }

        public static PacketArgsType fromArgsName(String value) {
            for (PacketArgsType type : values()) {
                if (type.ofArgs.equals(value)) {
                    return type;
                }
            }
            return INVOKE;
        }
    }

    public PacketParticleGroupS2C {
        args = Map.copyOf(args);
    }

    public static void encode(PacketParticleGroupS2C packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.uuid);
        buf.writeInt(packet.controlType.getId());
        for (Map.Entry<String, ParticleControllerDataBuffer<?>> entry : packet.args.entrySet()) {
            byte[] encoded = ParticleControllerDataBuffers.INSTANCE.encode(entry.getValue());
            buf.writeInt(encoded.length);
            buf.writeUtf(entry.getKey());
            buf.writeBytes(encoded);
        }
    }

    public static PacketParticleGroupS2C decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        ControlType type = ControlType.getTypeById(buf.readInt());
        Map<String, ParticleControllerDataBuffer<?>> args = new HashMap<>();
        while (buf.readableBytes() > 0) {
            int len = buf.readInt();
            String key = buf.readUtf();
            byte[] raw = new byte[len];
            buf.readBytes(raw);
            args.put(key, ParticleControllerDataBuffers.INSTANCE.decodeToBuffer(raw));
        }
        return new PacketParticleGroupS2C(uuid, type, args);
    }

    public static void handle(PacketParticleGroupS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientParticleGroupPacketHandler.receive(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
