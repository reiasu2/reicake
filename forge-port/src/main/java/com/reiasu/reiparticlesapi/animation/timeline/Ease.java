// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.animation.timeline;

@FunctionalInterface
public interface Ease {
    double cal(double t);
}
