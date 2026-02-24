package com.reiasu.reiparticlesapi.utils.interpolator.data;

import com.reiasu.reiparticlesapi.utils.GraphMathHelper;
import com.reiasu.reiparticlesapi.utils.RelativeLocation;
import net.minecraft.util.Mth;

public final class InterpolatorRelativeLocation implements InterpolatorData<RelativeLocation> {
    private RelativeLocation value;
    private RelativeLocation last;

    public InterpolatorRelativeLocation(RelativeLocation value) {
        this.value = value;
        this.last = value;
    }

    public RelativeLocation getLast() {
        return last;
    }

    public void setLast(RelativeLocation last) {
        this.last = last;
    }

    @Override
    public InterpolatorRelativeLocation update(RelativeLocation current) {
        this.last = this.value;
        this.value = current;
        return this;
    }

    @Override
    public RelativeLocation getWithInterpolator(Number progress) {
        double p = progress.doubleValue();
        return new RelativeLocation(
                Mth.lerp(p, last.getX(), value.getX()),
                Mth.lerp(p, last.getY(), value.getY()),
                Mth.lerp(p, last.getZ(), value.getZ())
        );
    }

    @Override
    public RelativeLocation getCurrent() {
        return value;
    }
}
