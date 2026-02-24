package com.reiasu.reiparticlesapi.network.particle.style;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StyleRegistry {
    public static final StyleRegistry INSTANCE = new StyleRegistry();

    private final List<ResourceLocation> idToKey = new ArrayList<>();
    private final Map<ResourceLocation, Integer> keyToId = new ConcurrentHashMap<>();
    private final Map<Integer, ParticleStyleProvider<?>> providers = new ConcurrentHashMap<>();

    private StyleRegistry() {
    }

        public synchronized int register(ResourceLocation key, ParticleStyleProvider<?> provider) {
        if (key == null || provider == null) {
            throw new IllegalArgumentException("key and provider must not be null");
        }
        Integer existing = keyToId.get(key);
        if (existing != null) {
            providers.put(existing, provider);
            return existing;
        }
        int id = idToKey.size();
        idToKey.add(key);
        keyToId.put(key, id);
        providers.put(id, provider);
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

    public ParticleStyleProvider<?> getProvider(int id) {
        return providers.get(id);
    }

    public ParticleStyleProvider<?> getProvider(ResourceLocation key) {
        Integer id = keyToId.get(key);
        return id != null ? providers.get(id) : null;
    }

    public int size() {
        return idToKey.size();
    }

    public List<ResourceLocation> keys() {
        return Collections.unmodifiableList(idToKey);
    }
}
