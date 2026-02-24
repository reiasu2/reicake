package com.reiasu.reiparticlesapi.network.particle.emitters;

import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class AutoParticleEmitters extends ParticleEmitters {

    protected AutoParticleEmitters() {
        ResourceLocation id = resolveCodecId(getClass());
        if (id != null) {
            setEmittersID(id);
        }
    }

    private static ResourceLocation resolveCodecId(Class<?> clazz) {
        try {
            Field f = clazz.getDeclaredField("CODEC_ID");
            if (Modifier.isStatic(f.getModifiers())
                    && ResourceLocation.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                return (ResourceLocation) f.get(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return null;
    }
}