package com.reiasu.reiparticlesapi.utils.interpolator.data;

import com.reiasu.reiparticlesapi.utils.GraphMathHelper;
import org.joml.Vector3f;
import net.minecraft.util.Mth;

public final class InterpolatorVector3f implements InterpolatorData<Vector3f> {
    private Vector3f value;
    private Vector3f last;

    public InterpolatorVector3f(Vector3f value) {
        this.value = value;
        this.last = new Vector3f(value);
    }

    public Vector3f getLast() {
        return last;
    }

    public void setLast(Vector3f last) {
        this.last = last;
    }

    @Override
    public InterpolatorVector3f update(Vector3f current) {
        this.last = this.value;
        this.value = current;
        return this;
    }

    @Override
    public Vector3f getWithInterpolator(Number progress) {
        double p = progress.doubleValue();
        return new Vector3f(
                (float) Mth.lerp(p, last.x, value.x),
                (float) Mth.lerp(p, last.y, value.y),
                (float) Mth.lerp(p, last.z, value.z)
        );
    }

    @Override
    public Vector3f getCurrent() {
        return value;
    }
}
