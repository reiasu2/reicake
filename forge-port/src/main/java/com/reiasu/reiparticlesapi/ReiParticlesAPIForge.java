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

@Mod(ReiParticlesAPIForge.MOD_ID)
public final class ReiParticlesAPIForge {
    public static final String MOD_ID = "reiparticlesapi";
    private static final Logger LOGGER = LogUtils.getLogger();

    private record TickHandler(String name, Runnable action) {}
    private static final int ERROR_THROTTLE_TICKS = 200; // ~10 seconds at 20 tps
    private static final int[] errorCooldowns = new int[32]; // indexed per handler slot
    private static int tickCounter;

    private final TickHandler[] clientHandlers;
    private final TickHandler[] serverHandlers;
    public ReiParticlesAPIForge(IEventBus modBus, ModContainer container) {
        clientHandlers = buildClientHandlers();
        serverHandlers = buildServerHandlers(serverRef);
        registerConfig(container);
        registerTickCallbacks(modBus);
        registerCommands();
        initSystems(modBus);

        
    }
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
                NeoForge.EVENT_BUS.post(new ServerPreTickEvent(event.getServer())));
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
        ReiParticlesAPI.INSTANCE.loadScannerPackages();
        ReiParticlesAPI.INSTANCE.registerTest();
    }
    private TickHandler[] buildClientHandlers() {
        return new TickHandler[] {
                new TickHandler("ClientTickEventForwarder", ClientTickEventForwarder::onClientEndTick),
                new TickHandler("AnimateManager.client", () -> AnimateManager.INSTANCE.tickClient()),
                new TickHandler("ParticleEmittersManager.client", ParticleEmittersManager::tickClient),
                new TickHandler("DisplayEntityManager.client", () -> DisplayEntityManager.INSTANCE.tickClient()),
                new TickHandler("ParticleCompositionManager.client", () -> ParticleCompositionManager.INSTANCE.tickClient()),
                new TickHandler("ParticleStyleManager.client", ParticleStyleManager::doTickClient),
                new TickHandler("ClientParticleGroupManager", () -> ClientParticleGroupManager.INSTANCE.doClientTick()),
                new TickHandler("ClientRenderEntityManager", () -> ClientRenderEntityManager.INSTANCE.doClientTick()),
                new TickHandler("PathMotionManager", () -> PathMotionManager.INSTANCE.tick()),
                new TickHandler("ReiScheduler", () -> ReiScheduler.INSTANCE.doTick()),
                new TickHandler("ClientCameraUtil", () -> ClientCameraUtil.INSTANCE.tick()),
        };
    }

    private TickHandler[] buildServerHandlers(net.minecraft.server.MinecraftServer[] serverRef) {
        return new TickHandler[] {
                new TickHandler("AnimateManager.server", () -> AnimateManager.INSTANCE.tickServer()),
                new TickHandler("ParticleEmittersManager.server", ParticleEmittersManager::tickAll),
                new TickHandler("DisplayEntityManager.server", () -> DisplayEntityManager.INSTANCE.tickAll()),
                new TickHandler("ParticleCompositionManager.server", () -> ParticleCompositionManager.INSTANCE.tickAll()),
                new TickHandler("ParticleStyleManager.server", ParticleStyleManager::doTickServer),
                new TickHandler("ServerRenderEntityManager.tick", () -> ServerRenderEntityManager.INSTANCE.tick()),
                new TickHandler("ServerRenderEntityManager.upgrade", () -> ServerRenderEntityManager.INSTANCE.upgrade(serverRef[0])),
                new TickHandler("TestManager", () -> TestManager.INSTANCE.doTickServer()),
                new TickHandler("ReiScheduler.server", () -> ReiScheduler.INSTANCE.doTick()),
                new TickHandler("ServerPostTickEvent", () -> NeoForge.EVENT_BUS.post(new ServerPostTickEvent(serverRef[0]))),
        };
    }

    private void onClientEndTick() {
        tickCounter++;
        runHandlers(clientHandlers, 0);
    }

    private void onServerEndTick(net.minecraft.server.MinecraftServer server) {
        serverRef[0] = server;
        runHandlers(serverHandlers, clientHandlers.length);
    }

    private final net.minecraft.server.MinecraftServer[] serverRef = new net.minecraft.server.MinecraftServer[1];

    private static void runHandlers(TickHandler[] handlers, int cooldownOffset) {
        for (int i = 0; i < handlers.length; i++) {
            try {
                handlers[i].action.run();
            } catch (Exception e) {
                int slot = cooldownOffset + i;
                if (errorCooldowns[slot] <= 0) {
                    errorCooldowns[slot] = ERROR_THROTTLE_TICKS;
                    LOGGER.warn("Tick handler '{}' threw (throttled to once per ~10s):",
                            handlers[i].name, e);
                } else {
                    errorCooldowns[slot]--;
                }
            }
        }
    }
    private void onClientSetup() {
        
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
        
    }
}