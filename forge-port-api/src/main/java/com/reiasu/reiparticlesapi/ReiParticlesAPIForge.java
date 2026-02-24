// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi;

import com.reiasu.reiparticlesapi.animation.AnimateManager;
import com.reiasu.reiparticlesapi.client.ClientTickEventForwarder;
import com.reiasu.reiparticlesapi.commands.APICommand;
import com.reiasu.reiparticlesapi.config.APIConfig;
import com.reiasu.reiparticlesapi.display.DisplayEntityManager;
import com.reiasu.reiparticlesapi.event.ForgeEventForwarder;
import com.reiasu.reiparticlesapi.event.ReiEventBus;
import com.reiasu.reiparticlesapi.event.events.server.ServerPostTickEvent;
import com.reiasu.reiparticlesapi.event.events.server.ServerPreTickEvent;
import com.reiasu.reiparticlesapi.network.ReiParticlesNetwork;
import com.reiasu.reiparticlesapi.network.animation.PathMotionManager;
import com.reiasu.reiparticlesapi.network.particle.composition.manager.ParticleCompositionManager;
import com.reiasu.reiparticlesapi.network.particle.emitters.ParticleEmittersManager;
import com.reiasu.reiparticlesapi.network.particle.emitters.environment.wind.WindDirections;
import com.reiasu.reiparticlesapi.network.particle.emitters.type.EmittersShootTypes;
import com.reiasu.reiparticlesapi.network.particle.style.ParticleStyleManager;
import com.reiasu.reiparticlesapi.particles.ControllableParticleEffectManager;
import com.reiasu.reiparticlesapi.particles.ReiModParticles;
import com.reiasu.reiparticlesapi.particles.control.group.ClientParticleGroupManager;
import com.reiasu.reiparticlesapi.particles.impl.particles.*;
import com.reiasu.reiparticlesapi.renderer.client.ClientRenderEntityManager;
import com.reiasu.reiparticlesapi.renderer.server.ServerRenderEntityManager;
import com.reiasu.reiparticlesapi.scheduler.ReiScheduler;
import com.reiasu.reiparticlesapi.test.TestManager;
import com.reiasu.reiparticlesapi.utils.ClientCameraUtil;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod(ReiParticlesAPIForge.MOD_ID)
public final class ReiParticlesAPIForge {
    public static final String MOD_ID = "reiparticlesapi";
    private static final Logger LOGGER = LogUtils.getLogger();
    public ReiParticlesAPIForge(IEventBus modBus, ModContainer container) {
        registerConfig(container);
        registerTickCallbacks(modBus);
        registerCommands();
        initSystems(modBus);

        LOGGER.info("ReiParticlesAPI NeoForge runtime initialized");
    }

    // ---- Lifecycle phases ----

    private void registerConfig(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, APIConfig.SPEC);
    }

    private void registerTickCallbacks(IEventBus modBus) {
        modBus.addListener((FMLClientSetupEvent event) -> onClientSetup());
        ReiModParticles.register(modBus);
        modBus.addListener(this::onRegisterParticleProviders);

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Pre event) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientTickEventForwarder.onClientStartTick();
            }
        });
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> onClientEndTick());
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Pre event) ->
                ReiEventBus.call(new ServerPreTickEvent(event.getServer())));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) ->
                onServerEndTick(event.getServer()));
    }

    private void registerCommands() {
        NeoForge.EVENT_BUS.addListener(
                (RegisterCommandsEvent event) ->
                        APICommand.INSTANCE.register(event.getDispatcher())
        );
    }

    private void initSystems(IEventBus modBus) {
        modBus.addListener(ReiParticlesNetwork::registerPayloads);
        ParticleEmittersManager.registerBuiltinCodecs();
        EmittersShootTypes.INSTANCE.init();
        WindDirections.INSTANCE.init();
        ControllableParticleEffectManager.INSTANCE.init();
        ReiParticlesAPI.init();
        ForgeEventForwarder.init();
        ReiParticlesAPI.INSTANCE.loadScannerPackages();
        ReiParticlesAPI.INSTANCE.registerTest();
    }

    // ---- Tick handlers (each manager wrapped in try-catch) ----

    private void onClientEndTick() {
        safeTick("ClientTickEventForwarder", ClientTickEventForwarder::onClientEndTick);
        safeTick("AnimateManager.client", () -> AnimateManager.INSTANCE.tickClient());
        safeTick("ParticleEmittersManager.client", ParticleEmittersManager::tickClient);
        safeTick("DisplayEntityManager.client", () -> DisplayEntityManager.INSTANCE.tickClient());
        safeTick("ParticleCompositionManager.client", () -> ParticleCompositionManager.INSTANCE.tickClient());
        safeTick("ParticleStyleManager.client", ParticleStyleManager::doTickClient);
        safeTick("ClientParticleGroupManager", () -> ClientParticleGroupManager.INSTANCE.doClientTick());
        safeTick("ClientRenderEntityManager", () -> ClientRenderEntityManager.INSTANCE.doClientTick());
        safeTick("PathMotionManager", () -> PathMotionManager.INSTANCE.tick());
        safeTick("ReiScheduler", () -> ReiScheduler.INSTANCE.doTick());
        safeTick("ClientCameraUtil", () -> ClientCameraUtil.INSTANCE.tick());
    }

    private void onServerEndTick(net.minecraft.server.MinecraftServer server) {
        safeTick("AnimateManager.server", () -> AnimateManager.INSTANCE.tickServer());
        safeTick("ParticleEmittersManager.server", ParticleEmittersManager::tickAll);
        safeTick("DisplayEntityManager.server", () -> DisplayEntityManager.INSTANCE.tickAll());
        safeTick("ParticleCompositionManager.server", () -> ParticleCompositionManager.INSTANCE.tickAll());
        safeTick("ParticleStyleManager.server", ParticleStyleManager::doTickServer);
        safeTick("ServerRenderEntityManager.tick", () -> ServerRenderEntityManager.INSTANCE.tick());
        safeTick("ServerRenderEntityManager.upgrade", () -> ServerRenderEntityManager.INSTANCE.upgrade(server));
        safeTick("TestManager", () -> TestManager.INSTANCE.doTickServer());
        safeTick("ReiScheduler.server", () -> ReiScheduler.INSTANCE.doTick());
        safeTick("ServerPostTickEvent", () -> ReiEventBus.call(new ServerPostTickEvent(server)));
    }

    private static final Map<String, Long> LAST_ERROR_LOG = new ConcurrentHashMap<>();
    private static final long ERROR_LOG_INTERVAL_MS = 10_000L;

    private static void safeTick(String name, Runnable tick) {
        try {
            tick.run();
        } catch (Exception e) {
            long now = System.currentTimeMillis();
            Long last = LAST_ERROR_LOG.get(name);
            if (last == null || now - last >= ERROR_LOG_INTERVAL_MS) {
                LAST_ERROR_LOG.put(name, now);
                LOGGER.warn("Tick handler '{}' threw (throttled to once per {}s):",
                        name, ERROR_LOG_INTERVAL_MS / 1000, e);
            }
        }
    }

    // ---- Setup callbacks ----

    private void onClientSetup() {
        LOGGER.info("ReiParticlesAPI client setup completed");
    }

    private void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ReiModParticles.CONTROLLABLE_END_ROD.get(),
                ControllableEndRodParticle.Factory::new);
        event.registerSpriteSet(ReiModParticles.CONTROLLABLE_ENCHANTMENT.get(),
                ControllableEnchantmentParticle.Factory::new);
        event.registerSpriteSet(ReiModParticles.CONTROLLABLE_CLOUD.get(),
                ControllableCloudParticle.Factory::new);
        event.registerSpriteSet(ReiModParticles.CONTROLLABLE_FLASH.get(),
                ControllableFlashParticle.Factory::new);
        event.registerSpriteSet(ReiModParticles.CONTROLLABLE_FIREWORK.get(),
                ControllableFireworkParticle.Factory::new);
        event.registerSpecial(ReiModParticles.CONTROLLABLE_FALLING_DUST.get(),
                new ControllableFallingDustParticle.Factory());
        event.registerSpriteSet(ReiModParticles.CONTROLLABLE_SPLASH.get(),
                ControllableSplashParticle.Factory::new);
        LOGGER.info("Registered ReiParticlesAPI particle providers (7 types)");
    }
}