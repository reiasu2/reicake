package com.reiasu.reiparticlesapi.network.particle.emitters.environment.wind;

import com.reiasu.reiparticlesapi.network.particle.emitters.PhysicConstant;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class WindDirections {

    public static final WindDirections INSTANCE = new WindDirections();

    private final Map<String, Supplier<WindDirection>> factories = new HashMap<>();

    private WindDirections() {
    }

        public void register(String id, Supplier<WindDirection> factory) {
        factories.put(id, factory);
    }

        public Supplier<WindDirection> getFactoryFromID(String id) {
        Supplier<WindDirection> factory = factories.get(id);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown WindDirection ID: " + id);
        }
        return factory;
    }

        public Vec3 handleWindForce(WindDirection wind, Vec3 pos, double airDensity,
                                double dragCoefficient, double crossSectionalArea, Vec3 v) {
        if (!wind.inRange(pos)) {
            return Vec3.ZERO;
        }

        Vec3 windVec = wind.getWind(pos);
        if (windVec.lengthSqr() > 0.0) {
            Vec3 relativeWind = windVec.subtract(v);
            double magnitude = 0.5 * airDensity * dragCoefficient * crossSectionalArea
                    * relativeWind.lengthSqr() * 0.05;
            return relativeWind.normalize().scale(magnitude);
        }
        return Vec3.ZERO;
    }

        public Vec3 handleWindForce(WindDirection wind, Vec3 pos, double airDensity,
                                double dragCoefficient, double crossSectionalArea) {
        return handleWindForce(wind, pos, airDensity, dragCoefficient, crossSectionalArea, Vec3.ZERO);
    }

        public void init() {
        register(GlobalWindDirection.ID, () -> new GlobalWindDirection(Vec3.ZERO));
        register(BallWindDirection.ID, () -> new BallWindDirection(Vec3.ZERO, 10.0,
                new com.reiasu.reiparticlesapi.utils.RelativeLocation()));
        register(BoxWindDirection.ID, () -> new BoxWindDirection(Vec3.ZERO,
                com.reiasu.reiparticlesapi.barrages.HitBox.of(1.0, 1.0, 1.0),
                new com.reiasu.reiparticlesapi.utils.RelativeLocation()));
    }
}
