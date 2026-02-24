package com.reiasu.reiparticlesapi.network.packet;

import com.reiasu.reiparticlesapi.event.events.key.KeyActionType;
import com.reiasu.reiparticlesapi.network.packet.server.listener.ServerKeyActionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;


public record PacketKeyActionC2S(ResourceLocation keyId, KeyActionType action, int pressTick, boolean isRelease) implements CustomPacketPayload {
    public static final Type<PacketKeyActionC2S> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "packet_key_action_c2_s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketKeyActionC2S> STREAM_CODEC = StreamCodec.of((buf, pkt) -> encode(pkt, buf), PacketKeyActionC2S::decode);

    public static void encode(PacketKeyActionC2S packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.keyId);
        buf.writeInt(packet.action.getId());
        buf.writeInt(packet.pressTick);
        buf.writeBoolean(packet.isRelease);
    }

    public static PacketKeyActionC2S decode(FriendlyByteBuf buf) {
        ResourceLocation keyId = buf.readResourceLocation();
        KeyActionType action = KeyActionType.fromId(buf.readInt());
        int pressTick = buf.readInt();
        boolean isRelease = buf.readBoolean();
        return new PacketKeyActionC2S(keyId, action, pressTick, isRelease);
    }

    public static void handle(PacketKeyActionC2S packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer sender) {
            context.enqueueWork(() -> ServerKeyActionHandler.receive(packet, sender));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
