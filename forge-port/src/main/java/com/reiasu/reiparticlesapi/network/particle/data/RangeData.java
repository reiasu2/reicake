package com.reiasu.reiparticlesapi.network.particle.data;

public abstract class RangeData<T extends Comparable<? super T>> {

    private T min;
    private T max;

    protected RangeData(T min, T max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min must be <= " + max);
        }
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }
}
