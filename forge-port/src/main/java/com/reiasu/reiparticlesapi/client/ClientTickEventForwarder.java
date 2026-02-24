package com.reiasu.reiparticlesapi.client;

import net.neoforged.neoforge.common.NeoForge;
import com.reiasu.reiparticlesapi.event.ReiEventBus;
import com.reiasu.reiparticlesapi.event.events.client.ClientPostTickEvent;
import com.reiasu.reiparticlesapi.event.events.client.ClientPreTickEvent;
import com.reiasu.reiparticlesapi.event.events.world.client.ClientWorldPostTickEvent;
import com.reiasu.reiparticlesapi.event.events.world.client.ClientWorldPreTickEvent;
import net.minecraft.client.Minecraft;

public final class ClientTickEventForwarder {
    private ClientTickEventForwarder() {
    }

    public static void onClientStartTick() {
        Minecraft minecraft = Minecraft.getInstance();
        NeoForge.EVENT_BUS.post(new ClientPreTickEvent(minecraft));
        if (minecraft.level != null) {
            NeoForge.EVENT_BUS.post(new ClientWorldPreTickEvent(minecraft.level));
        }
    }

    public static void onClientEndTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            NeoForge.EVENT_BUS.post(new ClientWorldPostTickEvent(minecraft.level));
        }
        NeoForge.EVENT_BUS.post(new ClientPostTickEvent(minecraft));
    }
}

