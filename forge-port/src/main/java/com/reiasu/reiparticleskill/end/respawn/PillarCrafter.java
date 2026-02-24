// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.end.respawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

final class PillarCrafter {
    static final int CRAFT_INTERVAL = 15;
    static final int MAX_TICKS = 500;

    private final Vec3 start;
    private Vec3 target;
    private final UUID crystalId;
    private int tick;

    PillarCrafter(Vec3 start, Vec3 target, UUID crystalId) {
        this.start = start;
        this.target = target;
        this.crystalId = crystalId;
    }

    Vec3 start() {
        return start;
    }

    boolean shouldCraftNow() {
        return tick % CRAFT_INTERVAL == 0;
    }

    void advanceTick() {
        tick++;
    }

    boolean expired() {
        return tick > MAX_TICKS;
    }

    boolean shouldCancel(ServerLevel level, EndRespawnPhase phase) {
        if (phase == EndRespawnPhase.BEFORE_END_WAITING || phase == EndRespawnPhase.END) {
            return true;
        }
        if (crystalId == null) {
            return true;
        }
        net.minecraft.world.entity.Entity entity = level.getEntity(crystalId);
        if (!(entity instanceof EndCrystal crystal) || !crystal.isAlive()) {
            return true;
        }
        return crystal.getBeamTarget() == null;
    }

    Vec3 resolveTarget(ServerLevel level) {
        if (crystalId == null) {
            return target;
        }
        net.minecraft.world.entity.Entity entity = level.getEntity(crystalId);
        if (entity instanceof EndCrystal crystal && crystal.isAlive()) {
            target = crystal.position().add(0.0, 1.7, 0.0);
        }
        return target;
    }
}
