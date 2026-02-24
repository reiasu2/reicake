package com.reiasu.reiparticlesapi.annotations.codec;

import net.minecraft.network.FriendlyByteBuf;

public interface BufferCodec<T> {

        static <T> BufferCodec<T> of(Encoder<T> encoder, Decoder<T> decoder) {
        return new BufferCodec<T>() {
            @Override
            public void encode(FriendlyByteBuf buf, T value) {
                encoder.encode(buf, value);
            }

            @Override
            public T decode(FriendlyByteBuf buf) {
                return decoder.decode(buf);
            }
        };
    }

        void encode(FriendlyByteBuf buf, T value);

        T decode(FriendlyByteBuf buf);

    @FunctionalInterface
    interface Encoder<T> {
        void encode(FriendlyByteBuf buf, T value);
    }

    @FunctionalInterface
    interface Decoder<T> {
        T decode(FriendlyByteBuf buf);
    }
}
