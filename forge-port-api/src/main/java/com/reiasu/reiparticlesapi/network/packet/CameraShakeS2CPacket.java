// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.client.CameraShakeClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.phys.Vec3;


public record CameraShakeS2CPacket(double range, Vec3 origin, double amplitude, int tick) implements CustomPacketPayload {
    public static final Type<CameraShakeS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "camera_shake_s2_c_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CameraShakeS2CPacket> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), CameraShakeS2CPacket::decode);

    public static void encode(CameraShakeS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.range);
        buf.writeDouble(packet.origin.x);
        buf.writeDouble(packet.origin.y);
        buf.writeDouble(packet.origin.z);
        buf.writeDouble(packet.amplitude);
        buf.writeInt(packet.tick);
    }

    public static CameraShakeS2CPacket decode(FriendlyByteBuf buf) {
        double range = buf.readDouble();
        Vec3 origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        double amplitude = buf.readDouble();
        int tick = buf.readInt();
        return new CameraShakeS2CPacket(range, origin, amplitude, tick);
    }

    public static void handle(CameraShakeS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> CameraShakeClientState.start(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
