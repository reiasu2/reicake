package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.network.packet.client.listener.ClientDisplayEntityPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record PacketDisplayEntityS2C(UUID uuid, String entityType, byte[] data) implements CustomPacketPayload {
    public static final Type<PacketDisplayEntityS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "packet_display_entity_s2_c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketDisplayEntityS2C> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), PacketDisplayEntityS2C::decode);

    public static void encode(PacketDisplayEntityS2C packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.entityType);
        buf.writeUUID(packet.uuid);
        buf.writeInt(packet.data.length);
        buf.writeBytes(packet.data);
    }

    public static PacketDisplayEntityS2C decode(FriendlyByteBuf buf) {
        String type = buf.readUtf();
        UUID uuid = buf.readUUID();
        int size = buf.readInt();
        byte[] data = new byte[size];
        buf.readBytes(data);
        return new PacketDisplayEntityS2C(uuid, type, data);
    }

    public static void handle(PacketDisplayEntityS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientDisplayEntityPacketHandler.receive(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
