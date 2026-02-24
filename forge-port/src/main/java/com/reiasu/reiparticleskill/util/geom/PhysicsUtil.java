package com.reiasu.reiparticleskill.util.geom;

import net.minecraft.util.Mth;

public final class PhysicsUtil {
    private PhysicsUtil() {
    }

    public static RelativeLocation applyDrag(RelativeLocation velocity, double dragFactor) {
        return velocity.scale(dragFactor);
    }

    public static RelativeLocation steer(RelativeLocation velocity, RelativeLocation desired, double blend) {
        double clampedBlend = Math.clamp(blend, 0.0, 1.0);
        velocity.setX(Mth.lerp(clampedBlend, velocity.getX(), desired.getX()));
        velocity.setY(Mth.lerp(clampedBlend, velocity.getY(), desired.getY()));
        velocity.setZ(Mth.lerp(clampedBlend, velocity.getZ(), desired.getZ()));
        return velocity;
    }

    public static RelativeLocation capSpeed(RelativeLocation velocity, double maxSpeed) {
        double len = velocity.length();
        if (len > maxSpeed && len > 0.0) {
            velocity.scale(maxSpeed / len);
        }
        return velocity;
    }
}
