package com.reiasu.reiparticlesapi.network.particle.emitters.type;

import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EmittersShootTypes {

    public static final EmittersShootTypes INSTANCE = new EmittersShootTypes();

    private final Map<String, Supplier<EmittersShootType>> factories = new HashMap<>();

    private EmittersShootTypes() {
    }

        public void register(String id, Supplier<EmittersShootType> factory) {
        factories.put(id, factory);
    }

        public Supplier<EmittersShootType> fromID(String id) {
        return factories.get(id);
    }
    public static EmittersShootType point() {
        return new PointEmittersShootType();
    }

    public static EmittersShootType line(Vec3 dir, double step) {
        return new LineEmittersShootType(dir, step);
    }

        public static EmittersShootType box(com.reiasu.reiparticlesapi.barrages.HitBox hitBox) {
        return new BoxEmittersShootType(hitBox);
    }

    public static EmittersShootType math() {
        return new MathEmittersShootType();
    }

        public void init() {
        register(PointEmittersShootType.ID, PointEmittersShootType::new);
        register(LineEmittersShootType.ID, () -> new LineEmittersShootType(Vec3.ZERO, 1.0));
        register(BoxEmittersShootType.ID, () -> new BoxEmittersShootType(
                com.reiasu.reiparticlesapi.barrages.HitBox.of(1.0, 1.0, 1.0)));
        register(MathEmittersShootType.ID, MathEmittersShootType::new);
    }
}
