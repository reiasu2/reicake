// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.composition;

import com.reiasu.reiparticlesapi.particles.Controllable;
import com.reiasu.reiparticlesapi.particles.ControllableParticle;
import com.reiasu.reiparticlesapi.particles.ControllableParticleEffect;
import com.reiasu.reiparticlesapi.particles.ParticleDisplayer;
import com.reiasu.reiparticlesapi.particles.control.ParticleController;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CompositionData implements Comparable<CompositionData> {

    private final UUID uuid = UUID.randomUUID();
    private int order;

    private Supplier<ParticleDisplayer> displayerBuilder;
    private Consumer<ParticleController> particleInit;
    @Nullable
    private Controllable<?> controllable;

    public UUID getUuid() {
        return uuid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Supplier<ParticleDisplayer> getDisplayerBuilder() {
        return displayerBuilder;
    }

    public CompositionData setDisplayerBuilder(Supplier<ParticleDisplayer> displayerBuilder) {
        this.displayerBuilder = displayerBuilder;
        return this;
    }

    public CompositionData setDisplayerWithEffect(Supplier<ControllableParticleEffect> effectSupplier) {
        this.displayerBuilder = () -> ParticleDisplayer.withSingle(effectSupplier.get());
        return this;
    }

    public Consumer<ParticleController> getParticleInit() {
        return particleInit;
    }

    public CompositionData setParticleInit(Consumer<ParticleController> particleInit) {
        this.particleInit = particleInit;
        return this;
    }

    @Nullable
    public Controllable<?> getControllable() {
        return controllable;
    }

    public void setControllable(@Nullable Controllable<?> controllable) {
        this.controllable = controllable;
    }

    @Override
    public int compareTo(CompositionData other) {
        return this.order - other.order;
    }
}
