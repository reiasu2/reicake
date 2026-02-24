// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticleskill;

import com.reiasu.reiparticlesapi.ReiParticlesAPI;
import com.reiasu.reiparticleskill.command.ReiParticleSkillDebugCommand;
import com.reiasu.reiparticleskill.command.SkillActionCommand;
import com.reiasu.reiparticleskill.command.port.APITestCommandPort;
import com.reiasu.reiparticleskill.command.port.DisplayCommandPort;
import com.reiasu.reiparticleskill.command.port.RailgunCommandPort;
import com.reiasu.reiparticleskill.enchantments.SkillEnchantments;
import com.reiasu.reiparticleskill.entities.SkillEntityTypes;
import com.reiasu.reiparticleskill.end.respawn.EndRespawnStateBridge;
import com.reiasu.reiparticleskill.end.respawn.EndRespawnWatcher;
import com.reiasu.reiparticleskill.listener.KeyListener;
import com.reiasu.reiparticleskill.listener.ServerListener;
import com.reiasu.reiparticleskill.register.RuntimePortAutoRegistrar;
import com.reiasu.reiparticleskill.sounds.SkillSoundEvents;
import com.reiasu.reiparticleskill.config.SkillClientConfig;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

@Mod(ReiParticleSkillForge.MOD_ID)
public final class ReiParticleSkillForge {
    public static final String MOD_ID = "reiparticleskill";
    private static final Logger LOGGER = LogUtils.getLogger();
    private final EndRespawnStateBridge endRespawnBridge = new EndRespawnStateBridge();

    public ReiParticleSkillForge(IEventBus modBus, ModContainer container) {
        SkillEntityTypes.register(modBus);
        SkillEnchantments.register(modBus);
        SkillSoundEvents.register(modBus);
        container.registerConfig(ModConfig.Type.CLIENT, SkillClientConfig.SPEC);

        modBus.addListener((FMLClientSetupEvent event) -> onClientSetup());

        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                onRegisterCommands(event.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> {
            EndRespawnWatcher.tickServer(event.getServer(), endRespawnBridge, LOGGER);
            ServerListener.onServerPostTick(event.getServer());
        });

        ReiParticlesAPI.init();
        ReiParticlesAPI.INSTANCE.loadScannerPackages();
        registerApiListeners();
        registerRuntimePorts();
        ReiParticlesAPI.INSTANCE.registerTest();

        LOGGER.info("ReiParticleSkill NeoForge runtime initialized");
    }

    private void onClientSetup() {
        LOGGER.info("ReiParticleSkill client setup completed");
    }

    private void onRegisterCommands(com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher) {
        ReiParticleSkillDebugCommand.register(dispatcher, endRespawnBridge, LOGGER);
        SkillActionCommand.register(dispatcher);
        DisplayCommandPort.register(dispatcher);
        RailgunCommandPort.register(dispatcher);
        APITestCommandPort.register(dispatcher);
        LOGGER.info("Registered reiparticleskill debug commands");
    }

    private void registerApiListeners() {
        try {
            ReiParticlesAPI.INSTANCE.registerEventListener(MOD_ID, new KeyListener());
            LOGGER.info("Registered ReiParticleSkill API listeners");
        } catch (Throwable t) {
            LOGGER.warn("Failed to register ReiParticleSkill API listeners", t);
        }
    }

    private void registerRuntimePorts() {
        try {
            RuntimePortAutoRegistrar.registerAll(LOGGER,
                    "com.reiasu.reiparticleskill.particles",
                    "com.reiasu.reiparticleskill.end.respawn.runtime.emitter.client");
        } catch (Throwable t) {
            LOGGER.warn("Failed to auto-register ReiParticleSkill runtime ports", t);
        }
    }
}
