// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi;

import net.neoforged.neoforge.common.NeoForge;
import com.reiasu.reiparticlesapi.event.ReiEventBus;
import com.reiasu.reiparticlesapi.event.api.ReiEvent;
import com.reiasu.reiparticlesapi.network.particle.emitters.ParticleEmittersManager;
import com.reiasu.reiparticlesapi.scheduler.ReiScheduler;
import com.reiasu.reiparticlesapi.test.SimpleTestGroupBuilder;
import com.reiasu.reiparticlesapi.test.TestManager;
import net.minecraft.server.level.ServerPlayer;
public final class ReiParticlesAPI {
    public static final ReiParticlesAPI INSTANCE = new ReiParticlesAPI();
    private static boolean initialized;
    private static boolean scannersLoaded;
    private static boolean testHooksRegistered;

    private ReiParticlesAPI() {
    }

    public static void init() {
        if (initialized) return;
        initialized = true;
        ParticleEmittersManager.registerBuiltinCodecs();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public void loadScannerPackages() {
        if (scannersLoaded) return;
        scannersLoaded = true;
        ReiEventBus.INSTANCE.initListeners();
    }

    public boolean scannersLoaded() {
        return scannersLoaded;
    }

    public void registerTest() {
        if (testHooksRegistered) return;
        testHooksRegistered = true;
        TestManager.INSTANCE.register("api-test-group-builder", user -> buildSmokeTestGroup(user));
    }

    public boolean testHooksRegistered() {
        return testHooksRegistered;
    }

    public void appendEventListenerTarget(String modId, String packageName) {
        ReiEventBus.INSTANCE.appendListenerTarget(modId, packageName);
    }

    public void initEventListeners() {
        ReiEventBus.INSTANCE.initListeners();
    }

    public void registerEventListener(String modId, Object listener) {
        ReiEventBus.INSTANCE.registerListenerInstance(modId, listener);
    }

    public <T extends ReiEvent> T callEvent(T event) {
        return NeoForge.EVENT_BUS.post(event);
    }

    public static ReiScheduler reiScheduler() {
        return ReiScheduler.INSTANCE;
    }

    private static SimpleTestGroupBuilder buildSmokeTestGroup(ServerPlayer user) {
        return new SimpleTestGroupBuilder("api-test-group-builder", user);
    }
}
