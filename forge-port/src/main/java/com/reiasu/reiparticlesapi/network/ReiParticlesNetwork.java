// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network;

import com.reiasu.reiparticlesapi.network.packet.CameraShakeS2CPacket;
import com.reiasu.reiparticlesapi.network.packet.PacketCameraShakeS2C;
import com.reiasu.reiparticlesapi.network.packet.PacketDisplayEntityS2C;
import com.reiasu.reiparticlesapi.network.packet.PacketKeyActionC2S;
import com.reiasu.reiparticlesapi.network.packet.PacketParticleCompositionS2C;
import com.reiasu.reiparticlesapi.network.packet.PacketParticleEmittersS2C;
import com.reiasu.reiparticlesapi.network.packet.PacketParticleGroupS2C;
import com.reiasu.reiparticlesapi.network.packet.PacketParticleS2C;
import com.reiasu.reiparticlesapi.network.packet.PacketParticleStyleS2C;
import com.reiasu.reiparticlesapi.network.packet.PacketRenderEntityS2C;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

public final class ReiParticlesNetwork {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ReiParticlesNetwork() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(CameraShakeS2CPacket.TYPE, CameraShakeS2CPacket.STREAM_CODEC, CameraShakeS2CPacket::handle);
        registrar.playToClient(PacketCameraShakeS2C.TYPE, PacketCameraShakeS2C.STREAM_CODEC, PacketCameraShakeS2C::handle);
        registrar.playToClient(PacketParticleS2C.TYPE, PacketParticleS2C.STREAM_CODEC, PacketParticleS2C::handle);
        registrar.playToClient(PacketParticleEmittersS2C.TYPE, PacketParticleEmittersS2C.STREAM_CODEC, PacketParticleEmittersS2C::handle);
        registrar.playToClient(PacketParticleCompositionS2C.TYPE, PacketParticleCompositionS2C.STREAM_CODEC, PacketParticleCompositionS2C::handle);
        registrar.playToClient(PacketDisplayEntityS2C.TYPE, PacketDisplayEntityS2C.STREAM_CODEC, PacketDisplayEntityS2C::handle);
        registrar.playToClient(PacketParticleStyleS2C.TYPE, PacketParticleStyleS2C.STREAM_CODEC, PacketParticleStyleS2C::handle);
        registrar.playToClient(PacketParticleGroupS2C.TYPE, PacketParticleGroupS2C.STREAM_CODEC, PacketParticleGroupS2C::handle);
        registrar.playToClient(PacketRenderEntityS2C.TYPE, PacketRenderEntityS2C.STREAM_CODEC, PacketRenderEntityS2C::handle);
        registrar.playToServer(PacketKeyActionC2S.TYPE, PacketKeyActionC2S.STREAM_CODEC, PacketKeyActionC2S::handle);
    }

    public static void sendTo(ServerPlayer player, CustomPacketPayload packet) {
        try {
            PacketDistributor.sendToPlayer(player, packet);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Player {} lacks channel --” packet {} dropped",
                    player.getName().getString(), packet.type().id());
        } catch (RuntimeException e) {
            LOGGER.debug("Failed to send packet {} to {}: {}", packet.type().id(),
                    player.getName().getString(), e.getMessage());
        }
    }
}
