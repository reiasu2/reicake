// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi;

import com.reiasu.reiparticlesapi.event.ReiEventBus;
import com.reiasu.reiparticlesapi.event.api.ReiEvent;
import com.reiasu.reiparticlesapi.network.particle.emitters.ParticleEmittersManager;
import com.reiasu.reiparticlesapi.scheduler.ReiScheduler;
import com.reiasu.reiparticlesapi.test.SimpleTestGroupBuilder;
import com.reiasu.reiparticlesapi.test.TestManager;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;


/**
 * Main entry point for the ReiParticles API.
 * <p>
 * Provides lifecycle management (initialization, scanner loading, hook registration),
 * a {@link com.reiasu.reiparticlesapi.scheduler.ReiScheduler ReiScheduler} for deferred tasks,
 * and convenience methods for the {@link com.reiasu.reiparticlesapi.event.ReiEventBus ReiEventBus}
 * event system.
 * <p>
 * Typical mod integration:
 * <pre>{@code
 * // During mod construction
 * ReiParticlesAPI.init();
 *
 * // Register event listener packages (scanned via ClassGraph)
 * ReiParticlesAPI.INSTANCE.appendEventListenerTarget("mymod", "com.example.mymod.listeners");
 * ReiParticlesAPI.INSTANCE.initEventListeners();
 *
 * // Schedule a deferred task (20 ticks = 1 second)
 * ReiParticlesAPI.reiScheduler().runTask(20, () -> { ... });
 * }</pre>
 */
public final class ReiParticlesAPI {
    public static final ReiParticlesAPI INSTANCE = new ReiParticlesAPI();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean initialized;
    private static boolean scannersLoaded;
    private static boolean testHooksRegistered;

    private ReiParticlesAPI() {
    }

    /** Initializes the API. Safe to call multiple times; only the first call takes effect. */
    public static void init() {
        if (initialized) {
            LOGGER.debug("init() called again — already initialized, skipping");
            return;
        }
        initialized = true;
        ParticleEmittersManager.registerBuiltinCodecs();
        LOGGER.info("ReiParticlesAPI init completed");
    }

    /** Returns {@code true} if {@link #init()} has been called. */
    public static boolean isInitialized() {
        return initialized;
    }

    /** Scans and initializes event listener packages. Call once after all listener targets are registered. */
    public void loadScannerPackages() {
        if (scannersLoaded) {
            LOGGER.debug("loadScannerPackages() called again — already loaded, skipping");
            return;
        }
        scannersLoaded = true;
        if (!initialized) {
            LOGGER.warn("loadScannerPackages() called before init() — call init() first");
        }
        LOGGER.info("ReiParticlesAPI scanner packages loaded");
        ReiEventBus.INSTANCE.initListeners();
    }

    public boolean scannersLoaded() {
        return scannersLoaded;
    }

    public void registerTest() {
        if (testHooksRegistered) return;
        testHooksRegistered = true;
        TestManager.INSTANCE.register("api-test-group-builder", user -> buildSmokeTestGroup(user));
        LOGGER.info("ReiParticlesAPI test hooks registered");
    }

    public boolean testHooksRegistered() {
        return testHooksRegistered;
    }

    /**
     * Registers a package for {@link com.reiasu.reiparticlesapi.annotations.events.EventListener @EventListener}
     * class scanning. The package is scanned via ClassGraph when {@link #initEventListeners()} is called.
     *
     * @param modId       the mod identifier
     * @param packageName fully-qualified package name to scan (e.g. {@code "com.example.mymod.listeners"})
     */
    public void appendEventListenerTarget(String modId, String packageName) {
        ReiEventBus.INSTANCE.appendListenerTarget(modId, packageName);
    }

    /** Initializes all registered event listeners. Call after all targets have been appended. */
    public void initEventListeners() {
        if (!scannersLoaded) {
            LOGGER.debug("initEventListeners() called before loadScannerPackages() — Forge scanning is a no-op, listeners must be registered explicitly");
        }
        ReiEventBus.INSTANCE.initListeners();
    }

    /**
     * Manually registers a single event listener instance.
     *
     * @param modId    the mod identifier
     * @param listener the listener object (must have methods annotated with event handler annotations)
     */
    public void registerEventListener(String modId, Object listener) {
        ReiEventBus.INSTANCE.registerListenerInstance(modId, listener);
    }

    /**
     * Fires an event through the {@link com.reiasu.reiparticlesapi.event.ReiEventBus ReiEventBus}.
     *
     * @param event the event to dispatch
     * @param <T>   event type
     * @return the same event instance (may have been modified by listeners)
     */
    public <T extends ReiEvent> T callEvent(T event) {
        return ReiEventBus.call(event);
    }

    /**
     * Returns the primary scheduler. Supports one-shot, repeating, max-tick,
     * cancel predicates, and finish callbacks. Ticked on both server and client.
     *
     * @see com.reiasu.reiparticlesapi.scheduler.ReiScheduler
     */
    public static ReiScheduler reiScheduler() {
        return ReiScheduler.INSTANCE;
    }

    private static SimpleTestGroupBuilder buildSmokeTestGroup(ServerPlayer user) {
        return new SimpleTestGroupBuilder("api-test-group-builder", user);
    }
}
