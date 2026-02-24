package com.reiasu.reiparticlesapi.network.buffer;

import net.minecraft.resources.ResourceLocation;

import java.nio.ByteBuffer;

public final class CharControllerBuffer extends AbstractControllerBuffer<Character> {
    public static final ParticleControllerDataBuffer.Id ID =
            new ParticleControllerDataBuffer.Id(ResourceLocation.fromNamespaceAndPath("reiparticlesapi", "char"));

    @Override
    public byte[] encode(Character value) {
        return ByteBuffer.allocate(2).putChar(value == null ? 0 : value).array();
    }

    @Override
    public Character decode(byte[] buf) {
        if (buf.length < 2) {
            return (char) 0;
        }
        return ByteBuffer.wrap(buf).getChar();
    }

    @Override
    public ParticleControllerDataBuffer.Id getBufferID() {
        return ID;
    }
}
