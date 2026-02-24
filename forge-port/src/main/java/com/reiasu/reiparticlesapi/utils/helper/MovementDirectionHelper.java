// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.utils.helper;

import com.reiasu.reiparticlesapi.utils.RelativeLocation;

public final class MovementDirectionHelper {
    private final RelativeLocation reference;
    private RelativeLocation direction;
    private final int maxTick;
    private int tick;
    private final RelativeLocation defaultPos;

    public MovementDirectionHelper(RelativeLocation reference, RelativeLocation direction, int maxTick) {
        this.reference = reference;
        this.direction = direction;
        this.maxTick = maxTick;
        this.defaultPos = reference.clone();
    }

    public RelativeLocation getReference() {
        return reference;
    }

    public RelativeLocation getDirection() {
        return direction;
    }

    public void setDirection(RelativeLocation direction) {
        this.direction = direction;
    }

    public int getMaxTick() {
        return maxTick;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public RelativeLocation getDefaultPos() {
        return defaultPos;
    }

        public void next() {
        if (tick++ > maxTick) {
            return;
        }
        reference.add(direction);
    }

        public void reset() {
        reference.setX(defaultPos.getX());
        reference.setY(defaultPos.getY());
        reference.setZ(defaultPos.getZ());
        tick = 0;
    }
}
