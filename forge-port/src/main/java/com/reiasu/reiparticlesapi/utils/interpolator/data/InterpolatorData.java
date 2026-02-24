package com.reiasu.reiparticlesapi.utils.interpolator.data;

public interface InterpolatorData<T> {
    InterpolatorData<T> update(T current);

    T getWithInterpolator(Number progress);

    T getCurrent();
}
