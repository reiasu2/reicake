package com.reiasu.reiparticlesapi.utils.helper.impl;

import com.reiasu.reiparticlesapi.network.particle.ServerParticleGroup;
import com.reiasu.reiparticlesapi.network.particle.style.ParticleGroupStyle;
import com.reiasu.reiparticlesapi.particles.Controllable;
import com.reiasu.reiparticlesapi.utils.RelativeLocation;
import com.reiasu.reiparticlesapi.utils.helper.BezierValueScaleHelper;

public final class GroupBezierValueScaleHelper extends BezierValueScaleHelper {
    private Controllable<?> group;

    public GroupBezierValueScaleHelper(
            int scaleTick,
            double minScale,
            double maxScale,
            RelativeLocation controlPoint1,
            RelativeLocation controlPoint2
    ) {
        super(scaleTick, minScale, maxScale, controlPoint1, controlPoint2);
    }

    public Controllable<?> getGroup() {
        return group;
    }

    public void setGroup(Controllable<?> group) {
        this.group = group;
    }

    @Override
    public void loadController(Controllable<?> controller) {
        if (controller instanceof ServerParticleGroup || controller instanceof ParticleGroupStyle) {
            this.group = controller;
        }
    }

    @Override
    public Controllable<?> getLoadedGroup() {
        return group;
    }

    @Override
    public double getGroupScale() {
        if (group instanceof ParticleGroupStyle pgs) {
            return pgs.getScale();
        }
        return getMinScale();
    }

    @Override
    public void scale(double scale) {
        if (group == null) {
            return;
        }
        double clamped = Math.max(getMinScale(), Math.min(getMaxScale(), scale));
        if (group instanceof ParticleGroupStyle pgs) {
            pgs.scale(clamped);
        }
    }
}
