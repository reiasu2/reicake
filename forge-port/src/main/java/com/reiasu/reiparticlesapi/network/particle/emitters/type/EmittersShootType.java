package com.reiasu.reiparticlesapi.network.particle.emitters.type;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface EmittersShootType {

        String getID();

        List<Vec3> getPositions(Vec3 origin, int tick, int count);

        Vec3 getDefaultDirection(Vec3 enter, int tick, Vec3 pos, Vec3 origin);
}
