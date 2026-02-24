package com.reiasu.reiparticlesapi.animation.timeline;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BooleanSupplier;

public final class Timeline {
    private final Deque<BooleanSupplier> steps = new ArrayDeque<>();

    public Timeline step(BooleanSupplier block) {
        this.steps.addLast(block);
        return this;
    }

    public void doTick() {
        if (this.steps.isEmpty()) {
            return;
        }
        boolean done = this.steps.peekFirst().getAsBoolean();
        if (done) {
            this.steps.removeFirst();
        }
    }

    public boolean isEmpty() {
        return this.steps.isEmpty();
    }
}
