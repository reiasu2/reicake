// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill.end.respawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

final class PillarPulseScheduler {
    private static final int PILLAR_EVENT_DELAY_TICKS = 39;
    private static final double CRYSTAL_SEARCH_RADIUS = 96.0;
    private static final double PILLAR_RING_RADIUS = 56.0;
    private static final double PILLAR_RING_Y_OFFSET = 80.0;

    private final Map<UUID, BlockPos> crystalBeamTargets = new HashMap<>();
    private final Map<BlockPos, PendingPillarPulse> pendingPulses = new HashMap<>();
    private int pulseIndex;
    private long lastPulseTick = Long.MIN_VALUE;

    record PulseResult(Vec3 pillarCenter, UUID preferredCrystalId) {}

        List<PulseResult> tick(ServerLevel level, Vec3 center, long phaseTick) {
        List<EndCrystal> crystals = level.getEntitiesOfClass(
                EndCrystal.class,
                new AABB(center, center).inflate(CRYSTAL_SEARCH_RADIUS),
                EndCrystal::isAlive
        );
        Set<UUID> seen = new HashSet<>();
        for (EndCrystal crystal : crystals) {
            UUID uuid = crystal.getUUID();
            seen.add(uuid);
            BlockPos beam = crystal.getBeamTarget();
            if (beam == null) {
                continue;
            }
            BlockPos prev = crystalBeamTargets.put(uuid, beam.immutable());
            if (prev == null || !prev.equals(beam)) {
                pendingPulses.put(beam.immutable(),
                        new PendingPillarPulse(phaseTick + PILLAR_EVENT_DELAY_TICKS, uuid));
            }
        }
        crystalBeamTargets.keySet().removeIf(uuid -> !seen.contains(uuid));

        List<PulseResult> results = new java.util.ArrayList<>();
        boolean fired = false;
        Iterator<Map.Entry<BlockPos, PendingPillarPulse>> it = pendingPulses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, PendingPillarPulse> entry = it.next();
            PendingPillarPulse pulse = entry.getValue();
            if (pulse.triggerTick() > phaseTick) {
                continue;
            }
            results.add(new PulseResult(Vec3.atCenterOf(entry.getKey()), pulse.crystalId()));
            it.remove();
            fired = true;
        }
        if (fired) {
            lastPulseTick = phaseTick;
            return results;
        }

        // Fallback: fire once when no beam targets are visible yet
        if (!pendingPulses.isEmpty() || !crystalBeamTargets.isEmpty()
                || lastPulseTick != Long.MIN_VALUE) {
            return results;
        }
        Vec3 fallback = chooseFallbackTarget(crystals, center);
        results.add(new PulseResult(fallback, null));
        lastPulseTick = phaseTick;
        return results;
    }

    int nextPulseIndex() {
        return pulseIndex++;
    }

    void clear() {
        crystalBeamTargets.clear();
        pendingPulses.clear();
        pulseIndex = 0;
        lastPulseTick = Long.MIN_VALUE;
    }

    private Vec3 chooseFallbackTarget(List<EndCrystal> crystals, Vec3 center) {
        if (!crystals.isEmpty()) {
            EndCrystal crystal = crystals.get(Math.floorMod(pulseIndex, crystals.size()));
            BlockPos beam = crystal.getBeamTarget();
            if (beam != null) {
                return Vec3.atCenterOf(beam);
            }
            return crystal.position();
        }
        double angle = (Math.PI * 2.0 * (pulseIndex % 10)) / 10.0;
        return center.add(Math.cos(angle) * PILLAR_RING_RADIUS, PILLAR_RING_Y_OFFSET,
                Math.sin(angle) * PILLAR_RING_RADIUS);
    }

    private record PendingPillarPulse(long triggerTick, UUID crystalId) {}
}
