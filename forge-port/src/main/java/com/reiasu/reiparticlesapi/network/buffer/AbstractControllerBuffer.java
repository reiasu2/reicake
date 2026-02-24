package com.reiasu.reiparticlesapi.network.buffer;

public abstract class AbstractControllerBuffer<T> implements ParticleControllerDataBuffer<T> {
    private T loadedValue;

    @Override
    public T getLoadedValue() {
        return loadedValue;
    }

    @Override
    public void setLoadedValue(T value) {
        this.loadedValue = value;
    }
}

