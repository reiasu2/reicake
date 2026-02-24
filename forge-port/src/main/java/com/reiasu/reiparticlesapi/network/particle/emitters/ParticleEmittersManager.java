// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.emitters;

import com.reiasu.reiparticlesapi.event.ReiEventBus;
import com.reiasu.reiparticlesapi.event.events.particle.emitter.EmitterRemoveEvent;
import com.reiasu.reiparticlesapi.event.events.particle.emitter.EmitterSpawnEvent;
import com.reiasu.reiparticlesapi.network.ReiParticlesNetwork;
import com.reiasu.reiparticlesapi.network.packet.PacketParticleEmittersS2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.reiasu.reiparticlesapi.config.APIConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class ParticleEmittersManager {
    private static final List<ParticleEmitters> EMITTERS = new ArrayList<>();
    private static final Map<UUID, Set<UUID>> VISIBLE = new ConcurrentHashMap<>();
    private static final Map<UUID, ParticleEmitters> CLIENT_EMITTERS = new ConcurrentHashMap<>();
    private static volatile boolean builtinsRegistered;
    private static final AtomicInteger packetsThisTick = new AtomicInteger(0);
    private static final int PACKETS_PER_TICK_LIMIT = 512;
    private static long visibilityTick = 0;
    private static final int PLAYER_SHARD_COUNT = 4;

    private static int statSynced;
    private static int statSkippedLod;
    private static int statSkippedShard;
    private static int statThrottled;
    private static volatile int[] lastTickStats = new int[4];

    private ParticleEmittersManager() {
    }

    public static void registerBuiltinCodecs() {
        if (builtinsRegistered) {
            return;
        }
        builtinsRegistered = true;
        registerCodec(DebugParticleEmitters.CODEC_ID, DebugParticleEmitters::decode);
        registerCodec(DebugRailgunEmitters.CODEC_ID, DebugRailgunEmitters::decode);
    }

    public static int registerCodec(ResourceLocation id, Function<FriendlyByteBuf, ParticleEmitters> decoder) {
        if (id == null || decoder == null) {
            return -1;
        }
        return EmitterRegistry.INSTANCE.register(id, decoder);
    }

    public static Function<FriendlyByteBuf, ParticleEmitters> getCodecFromID(ResourceLocation id) {
        if (id == null) {
            return null;
        }
        return EmitterRegistry.INSTANCE.getDecoder(id);
    }

    public static void spawnEmitters(Object emitter) {
        spawnEmitters(emitter, null, 0.0, 0.0, 0.0);
    }

    public static void spawnEmitters(Object emitter, ServerLevel level, double x, double y, double z) {
        if (!(emitter instanceof ParticleEmitters particleEmitters)) {
            return;
        }
        // TODO: log a warning when hitting the limit?
        int limit = APIConfig.INSTANCE.getParticleCountLimit();
        synchronized (EMITTERS) {
            if (EMITTERS.size() >= limit) {
                return;
            }
        }
        if (level != null) {
            particleEmitters.bind(level, x, y, z);
        }
        synchronized (EMITTERS) {
            EMITTERS.add(particleEmitters);
        }
        ReiEventBus.call(new EmitterSpawnEvent(particleEmitters, false));
    }

    public static void createOrChangeClient(ParticleEmitters emitters, Level viewWorld) {
        if (emitters == null || viewWorld == null) {
            return;
        }
        ParticleEmitters old = CLIENT_EMITTERS.get(emitters.getUuid());
        if (old == null) {
            Vec3 pos = emitters.position();
            emitters.bind(viewWorld, pos.x, pos.y, pos.z);
            CLIENT_EMITTERS.put(emitters.getUuid(), emitters);
            ReiEventBus.call(new EmitterSpawnEvent(emitters, true));
            return;
        }
        old.update(emitters);
        Vec3 pos = emitters.position();
        old.bind(viewWorld, pos.x, pos.y, pos.z);
        if (emitters.getCanceled()) {
            old.cancel();
        }
    }

    public static int[] getLastTickStats() {
        return lastTickStats.clone();
    }

    public static String getDebugInfo() {
        int serverCount;
        synchronized (EMITTERS) {
            serverCount = EMITTERS.size();
        }
        int clientCount = CLIENT_EMITTERS.size();
        int totalViewers = 0;
        for (Set<UUID> vis : VISIBLE.values()) {
            totalViewers += vis.size();
        }
        int[] stats = getLastTickStats();
        return String.format(
                "Emitters: server=%d, client=%d | Viewers: %d players tracking | "
                        + "Last tick: synced=%d, skippedLod=%d, skippedShard=%d, throttled=%d",
                serverCount, clientCount, VISIBLE.size(),
                stats[0], stats[1], stats[2], stats[3]);
    }

    public static void tickAll() {
        lastTickStats = new int[]{statSynced, statSkippedLod, statSkippedShard, statThrottled};
        statSynced = 0;
        statSkippedLod = 0;
        statSkippedShard = 0;
        statThrottled = 0;
        packetsThisTick.set(0);
        long tick = visibilityTick++;
        synchronized (EMITTERS) {
            EMITTERS.removeIf(emitters -> {
                updateClientVisible(emitters, tick);
                emitters.tick();
                if (!emitters.getCanceled()) {
                    return false;
                }
                removeAllViews(emitters);
                ReiEventBus.call(new EmitterRemoveEvent(emitters, false));
                return true;
            });
        }
        pruneDisconnectedPlayers();
    }

    private static void updateClientVisible(ParticleEmitters emitters, long tick) {
        if (!(emitters.level() instanceof ServerLevel level)) {
            return;
        }

        List<ServerPlayer> players = level.players();
        for (int i = 0; i < players.size(); i++) {
            // shard players across ticks
            if (i % PLAYER_SHARD_COUNT != (int)(tick % PLAYER_SHARD_COUNT)) {
                statSkippedShard++;
                continue;
            }
            ServerPlayer player = players.get(i);
            Set<UUID> visibleSet = VISIBLE.computeIfAbsent(player.getUUID(), ignored -> ConcurrentHashMap.newKeySet());
            boolean shouldView = canViewEmitter(emitters, player);
            boolean isViewing = visibleSet.contains(emitters.getUuid());

            if (shouldView && !isViewing) {
                addView(player, emitters);
                continue;
            }

            if (!shouldView && isViewing) {
                removeView(player, emitters);
                continue;
            }

            if (shouldView) {
                // LOD: skip updates for far players
                double dist = player.position().distanceTo(emitters.position());
                double range = emitters.getVisibleRange();
                int lodInterval = computeLodInterval(dist, range);
                if (lodInterval > 1 && (emitters.getTick() % lodInterval) != 0) {
                    statSkippedLod++;
                    continue;
                }
                sendChange(emitters, player);
            }
        }
    }

    // FIXME: these thresholds are hardcoded, should be configurable
    private static int computeLodInterval(double dist, double range) {
        double r = dist / Math.max(1.0, range);
        if (r < 0.25) return 1;
        if (r < 0.50) return 3;
        if (r < 0.75) return 6;
        return 12;
    }

    private static boolean canViewEmitter(ParticleEmitters emitters, ServerPlayer player) {
        if (emitters.level() == null || player == null) {
            return false;
        }
        if (player.isRemoved() || player.isSpectator()) {
            return false;
        }
        if (emitters.level() != player.level()) {
            return false;
        }
        double range = Math.min(emitters.getVisibleRange(), APIConfig.INSTANCE.getMaxEmitterVisibleRange());
        return player.position().distanceTo(emitters.position()) <= range;
    }

    private static void sendChange(ParticleEmitters emitters, ServerPlayer player) {
        if (packetsThisTick.incrementAndGet() > APIConfig.INSTANCE.getPacketsPerTickLimit()) {
            statThrottled++;
            return;
        }
        statSynced++;
        ResourceLocation key = emitters.getEmittersID();
        if (key == null || EmitterRegistry.INSTANCE.getDecoder(key) == null) {
            return;
        }
        PacketParticleEmittersS2C packet = new PacketParticleEmittersS2C(
                key,
                emitters.encodeToBytes(),
                PacketParticleEmittersS2C.PacketType.CHANGE_OR_CREATE
        );
        ReiParticlesNetwork.sendTo(player, packet);
    }

    private static void addView(ServerPlayer player, ParticleEmitters emitters) {
        Set<UUID> visibleSet = VISIBLE.computeIfAbsent(player.getUUID(), ignored -> ConcurrentHashMap.newKeySet());
        if (!visibleSet.add(emitters.getUuid())) {
            return;
        }
        sendChange(emitters, player);
    }

    private static void removeView(ServerPlayer player, ParticleEmitters emitters) {
        Set<UUID> visibleSet = VISIBLE.computeIfAbsent(player.getUUID(), ignored -> ConcurrentHashMap.newKeySet());
        visibleSet.remove(emitters.getUuid());
        ResourceLocation key = emitters.getEmittersID();
        if (key == null || EmitterRegistry.INSTANCE.getDecoder(key) == null) {
            return;
        }
        PacketParticleEmittersS2C packet = new PacketParticleEmittersS2C(
                key,
                emitters.encodeToBytes(),
                PacketParticleEmittersS2C.PacketType.REMOVE
        );
        ReiParticlesNetwork.sendTo(player, packet);
    }

    private static void removeAllViews(ParticleEmitters emitters) {
        ServerLevel level = emitters.level() instanceof ServerLevel sl ? sl : null;
        for (Map.Entry<UUID, Set<UUID>> entry : VISIBLE.entrySet()) {
            UUID playerId = entry.getKey();
            Set<UUID> visibleSet = entry.getValue();
            if (!visibleSet.remove(emitters.getUuid())) {
                continue;
            }
            if (level == null) {
                continue;
            }
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(playerId);
            if (player != null) {
                ResourceLocation rkey = emitters.getEmittersID();
                if (rkey == null || EmitterRegistry.INSTANCE.getDecoder(rkey) == null) {
                    continue;
                }
                PacketParticleEmittersS2C packet = new PacketParticleEmittersS2C(
                        rkey,
                        emitters.encodeToBytes(),
                        PacketParticleEmittersS2C.PacketType.REMOVE
                );
                ReiParticlesNetwork.sendTo(player, packet);
            }
        }
    }

    private static void pruneDisconnectedPlayers() {
        if (EMITTERS.isEmpty()) {
            VISIBLE.clear();
            return;
        }
        net.minecraft.server.MinecraftServer server = null;
        synchronized (EMITTERS) {
            for (ParticleEmitters emitter : EMITTERS) {
                if (emitter.level() instanceof ServerLevel sl) {
                    server = sl.getServer();
                    break;
                }
            }
        }
        if (server == null) {
            return;
        }
        final net.minecraft.server.MinecraftServer srv = server;
        VISIBLE.entrySet().removeIf(entry ->
                srv.getPlayerList().getPlayer(entry.getKey()) == null);
    }

    public static void tickClient() {
        CLIENT_EMITTERS.entrySet().removeIf(entry -> {
            ParticleEmitters emitters = entry.getValue();
            emitters.tick();
            if (emitters.getCanceled()) {
                ReiEventBus.call(new EmitterRemoveEvent(emitters, true));
                return true;
            }
            return false;
        });
    }

    public static int activeCount() {
        synchronized (EMITTERS) {
            return EMITTERS.size();
        }
    }

    public static void clear() {
        synchronized (EMITTERS) {
            for (ParticleEmitters emitters : EMITTERS) {
                emitters.cancel();
            }
            EMITTERS.clear();
        }
        VISIBLE.clear();
        for (ParticleEmitters emitters : CLIENT_EMITTERS.values()) {
            emitters.cancel();
        }
        CLIENT_EMITTERS.clear();
    }

    public static List<ParticleEmitters> getEmitters() {
        synchronized (EMITTERS) {
            return Collections.unmodifiableList(new ArrayList<>(EMITTERS));
        }
    }

    public static Map<UUID, ParticleEmitters> getClientEmitters() {
        return Collections.unmodifiableMap(CLIENT_EMITTERS);
    }
}
