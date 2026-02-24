package com.reiasu.reiparticlesapi.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

public final class PhysicsUtil {
    private PhysicsUtil() {
    }
        public static BlockHitResult collide(Vec3 currentPos, Vec3 velocity, Level world) {
        ClipContext context = new ClipContext(
                currentPos,
                currentPos.add(velocity),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                (net.minecraft.world.entity.Entity) null
        );
        return world.clip(context);
    }

        public static Vec3 fixBeforeCollidePosition(BlockHitResult res) {
        Direction dir = res.getDirection();
        Vec3 normal = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
        Vec3 location = res.getLocation();
        Vec3 normalizedOffset = normal.normalize();
        return location.add(normalizedOffset.scale(0.07));
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
