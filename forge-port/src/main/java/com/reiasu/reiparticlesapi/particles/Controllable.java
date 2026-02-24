package com.reiasu.reiparticlesapi.particles;

import com.reiasu.reiparticlesapi.network.buffer.ParticleControllerDataBuffer;
import com.reiasu.reiparticlesapi.utils.RelativeLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

public interface Controllable<T> {

        UUID controlUUID();

        void rotateToPoint(RelativeLocation to);

        void rotateToWithAngle(RelativeLocation to, double radian);

        void rotateAsAxis(double radian);

        void teleportTo(Vec3 pos);

        void teleportTo(double x, double y, double z);

        void remove();

        T getControlObject();

        @SuppressWarnings("unchecked")
    default <S> S getControlCasted() {
        return (S) getControlObject();
    }

        @SuppressWarnings("unchecked")
    default <S> S getControlCastedOrNull() {
        try {
            return (S) getControlObject();
        } catch (Exception e) {
            return null;
        }
    }
    default void load(Map<String, ? extends ParticleControllerDataBuffer<?>> args) {
    }

    default Map<String, ParticleControllerDataBuffer<?>> toArgs() {
        return Map.of();
    }

    default void change(Map<String, ? extends ParticleControllerDataBuffer<?>> args) {
        load(args);
    }
}
