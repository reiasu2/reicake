package com.reiasu.reiparticlesapi.utils.interpolator;

import com.reiasu.reiparticlesapi.utils.RelativeLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public interface Interpolator {
    double getRefinerCount();

    Interpolator insertPoint(Vec3 vec);

    Interpolator insertPoint(Vector3f vec);

    Interpolator insertPoint(RelativeLocation vec);

    Interpolator setLimit(double limit);

    Interpolator setRefiner(double refiner);

    List<RelativeLocation> getRefinedResult();
}
