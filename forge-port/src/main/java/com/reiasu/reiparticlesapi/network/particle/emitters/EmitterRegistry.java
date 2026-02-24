// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.emitters;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class EmitterRegistry {
    public static final EmitterRegistry INSTANCE = new EmitterRegistry();

    private final List<ResourceLocation> idToKey = new ArrayList<>();
    private final Map<ResourceLocation, Integer> keyToId = new ConcurrentHashMap<>();
    private final Map<Integer, Function<FriendlyByteBuf, ParticleEmitters>> decoders = new ConcurrentHashMap<>();

    private EmitterRegistry() {
    }

        public synchronized int register(ResourceLocation key, Function<FriendlyByteBuf, ParticleEmitters> decoder) {
        if (key == null || decoder == null) {
            throw new IllegalArgumentException("key and decoder must not be null");
        }
        Integer existing = keyToId.get(key);
        if (existing != null) {
            decoders.put(existing, decoder);
            return existing;
        }
        int id = idToKey.size();
        idToKey.add(key);
        keyToId.put(key, id);
        decoders.put(id, decoder);
        return id;
    }

    public int getId(ResourceLocation key) {
        Integer id = keyToId.get(key);
        return id != null ? id : -1;
    }

    public ResourceLocation getKey(int id) {
        if (id < 0 || id >= idToKey.size()) {
            return null;
        }
        return idToKey.get(id);
    }

    public Function<FriendlyByteBuf, ParticleEmitters> getDecoder(int id) {
        return decoders.get(id);
    }

    public Function<FriendlyByteBuf, ParticleEmitters> getDecoder(ResourceLocation key) {
        Integer id = keyToId.get(key);
        return id != null ? decoders.get(id) : null;
    }

    public int size() {
        return idToKey.size();
    }

    public List<ResourceLocation> keys() {
        return Collections.unmodifiableList(idToKey);
    }
}
