package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.network.packet.client.listener.ClientParticleCompositionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public final class PacketParticleCompositionS2C implements CustomPacketPayload {
    public static final Type<PacketParticleCompositionS2C> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "packet_particle_composition_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleCompositionS2C> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), PacketParticleCompositionS2C::decode);

    private final UUID uuid;
    private final String type;
    private final byte[] data;
    private boolean distanceRemove;

    public PacketParticleCompositionS2C(UUID uuid, String type, byte[] data) {
        this.uuid = uuid;
        this.type = type;
        this.data = data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public boolean getDistanceRemove() {
        return distanceRemove;
    }

    public void setDistanceRemove(boolean distanceRemove) {
        this.distanceRemove = distanceRemove;
    }

    public static void encode(PacketParticleCompositionS2C packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.type);
        buf.writeUUID(packet.uuid);
        buf.writeBoolean(packet.distanceRemove);
        buf.writeInt(packet.data.length);
        buf.writeBytes(packet.data);
    }

    public static PacketParticleCompositionS2C decode(FriendlyByteBuf buf) {
        String type = buf.readUtf();
        UUID uuid = buf.readUUID();
        boolean distanceRemove = buf.readBoolean();
        int size = buf.readInt();
        byte[] data = new byte[size];
        buf.readBytes(data);
        PacketParticleCompositionS2C packet = new PacketParticleCompositionS2C(uuid, type, data);
        packet.setDistanceRemove(distanceRemove);
        return packet;
    }

    public static void handle(PacketParticleCompositionS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientParticleCompositionHandler.receive(packet));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
