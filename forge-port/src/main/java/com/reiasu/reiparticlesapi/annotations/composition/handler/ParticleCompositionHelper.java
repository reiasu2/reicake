// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.annotations.composition.handler;

import com.reiasu.reiparticlesapi.annotations.CodecField;
import com.reiasu.reiparticlesapi.annotations.codec.BufferCodec;
import com.reiasu.reiparticlesapi.annotations.codec.CodecHelper;
import com.reiasu.reiparticlesapi.network.particle.composition.ParticleComposition;
import com.reiasu.reiparticlesapi.network.particle.composition.SequencedParticleComposition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ParticleCompositionHelper {

    public static final ParticleCompositionHelper INSTANCE = new ParticleCompositionHelper();

    private ParticleCompositionHelper() {
    }

        public BufferCodec<ParticleComposition> generateCodec(ParticleComposition randomInstance) {
        Class<?> type = randomInstance.getClass();

        // Resolve constructor (Vec3, Level) for reflective instantiation
        Constructor<?> constructor;
        try {
            constructor = type.getConstructor(Vec3.class, Level.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Composition class " + type.getName() +
                    " must have a public constructor (Vec3, Level)", e);
        }

        return BufferCodec.of(
                (buf, composition) -> encodeComposition(type, buf, composition),
                buf -> decodeComposition(constructor, type, buf)
        );
    }

    // --”€--”€--”€ Encode --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

    private static void encodeComposition(Class<?> type, FriendlyByteBuf buf,
                                           ParticleComposition composition) {
        // Encode base fields
        if (composition instanceof SequencedParticleComposition seq) {
            SequencedParticleComposition.encodeBase(seq, buf);
        } else {
            ParticleComposition.encodeBase(composition, buf);
        }

        // Encode @CodecField annotated fields
        List<Field> fields = getCodecFields(type);
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            BufferCodec<Object> codec = getCodecOrThrow(fieldType);
            try {
                codec.encode(buf, field.get(composition));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to encode field " + field.getName(), e);
            }
        }
    }

    // --”€--”€--”€ Decode --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

    private static ParticleComposition decodeComposition(Constructor<?> constructor,
                                                          Class<?> type,
                                                          FriendlyByteBuf buf) {
        // Create instance via reflection
        ParticleComposition instance;
        try {
            instance = (ParticleComposition) constructor.newInstance(Vec3.ZERO, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + type.getName(), e);
        }

        // Decode base fields
        if (instance instanceof SequencedParticleComposition seq) {
            SequencedParticleComposition.decodeBase(seq, buf);
        } else {
            ParticleComposition.decodeBase(instance, buf);
        }

        // Decode @CodecField annotated fields
        List<Field> fields = getCodecFields(type);
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            BufferCodec<Object> codec = getCodecOrThrow(fieldType);
            try {
                Object value = codec.decode(buf);
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to decode field " + field.getName(), e);
            }
        }

        return instance;
    }

    // --”€--”€--”€ Utilities --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

        private static List<Field> getCodecFields(Class<?> type) {
        Field[] allFields = type.getDeclaredFields();
        List<Field> result = new ArrayList<>();
        for (Field f : allFields) {
            if (f.isAnnotationPresent(CodecField.class) && !Modifier.isFinal(f.getModifiers())) {
                result.add(f);
            }
        }
        result.sort(Comparator.comparing(Field::getName));
        return result;
    }

        @SuppressWarnings("unchecked")
    private static BufferCodec<Object> getCodecOrThrow(Class<?> fieldType) {
        BufferCodec<?> codec = CodecHelper.INSTANCE.getSupposedTypes().get(fieldType.getName());
        if (codec == null) {
            throw new IllegalArgumentException(
                    "Unsupported type: " + fieldType.getName() +
                    " --” register it via CodecHelper.INSTANCE.register()");
        }
        return (BufferCodec<Object>) codec;
    }
}
