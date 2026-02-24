package com.reiasu.reiparticlesapi.network.particle.emitters.environment.wind;

import com.reiasu.reiparticlesapi.network.particle.emitters.ParticleEmitters;
import net.minecraft.world.phys.Vec3;

public interface WindDirection {

        Vec3 getDirection();

    void setDirection(Vec3 direction);

        boolean getRelative();

    void setRelative(boolean relative);

        String getWindSpeedExpress();

    void setWindSpeedExpress(String express);

        WindDirection loadEmitters(ParticleEmitters emitters);

        boolean hasLoadedEmitters();

        String getID();

        Vec3 getWind(Vec3 particlePos);

        boolean inRange(Vec3 pos);
}
