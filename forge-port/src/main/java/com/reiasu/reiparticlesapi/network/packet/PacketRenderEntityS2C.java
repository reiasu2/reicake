// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.network.packet.client.listener.ClientRenderEntityPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public final class PacketRenderEntityS2C implements CustomPacketPayload {
    public static final Type<PacketRenderEntityS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "packet_render_entity_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketRenderEntityS2C> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), PacketRenderEntityS2C::decode);

    public enum Method {
        CREATE(0),
        TOGGLE(1),
        REMOVE(2);

        private final int id;

        Method(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Method idOf(int id) {
            return switch (id) {
                case 0 -> CREATE;
                case 1 -> TOGGLE;
                case 2 -> REMOVE;
                default -> CREATE;
            };
        }
    }

    private UUID uuid;
    private byte[] entityData;
    private ResourceLocation id;
    private Method method;

    public PacketRenderEntityS2C(UUID uuid, byte[] entityData, ResourceLocation id, Method method) {
        this.uuid = uuid;
        this.entityData = entityData;
        this.id = id;
        this.method = method;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public byte[] getEntityData() {
        return entityData;
    }

    public void setEntityData(byte[] entityData) {
        this.entityData = entityData;
    }

    public ResourceLocation getId() {
        return id;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public static PacketRenderEntityS2C ofSpawn(com.reiasu.reiparticlesapi.renderer.RenderEntity entity) {
        return new PacketRenderEntityS2C(entity.getUuid(), new byte[0],
                entity.getRenderID(), Method.CREATE);
    }

    public static PacketRenderEntityS2C ofSync(com.reiasu.reiparticlesapi.renderer.RenderEntity entity) {
        return new PacketRenderEntityS2C(entity.getUuid(), new byte[0],
                entity.getRenderID(), Method.TOGGLE);
    }

    public static PacketRenderEntityS2C ofRemove(com.reiasu.reiparticlesapi.renderer.RenderEntity entity) {
        return new PacketRenderEntityS2C(entity.getUuid(), new byte[0],
                entity.getRenderID(), Method.REMOVE);
    }

    public static void encode(PacketRenderEntityS2C packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.method.getId());
        buf.writeUUID(packet.uuid);
        buf.writeResourceLocation(packet.id);
        buf.writeInt(packet.entityData.length);
        buf.writeBytes(packet.entityData);
    }

    public static PacketRenderEntityS2C decode(FriendlyByteBuf buf) {
        Method method = Method.idOf(buf.readInt());
        UUID uuid = buf.readUUID();
        ResourceLocation id = buf.readResourceLocation();
        int size = buf.readInt();
        byte[] entity = new byte[size];
        buf.readBytes(entity);
        return new PacketRenderEntityS2C(uuid, entity, id, method);
    }

    public static void handle(PacketRenderEntityS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientRenderEntityPacketHandler.receive(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
