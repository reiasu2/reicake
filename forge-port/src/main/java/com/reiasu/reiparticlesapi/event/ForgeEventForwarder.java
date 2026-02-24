package com.reiasu.reiparticlesapi.event;

import com.reiasu.reiparticlesapi.event.events.entity.EntityMoveEvent;
import com.reiasu.reiparticlesapi.event.events.world.client.ClientWorldChangeEvent;
import com.reiasu.reiparticlesapi.event.events.entity.EntityPostTickEvent;
import com.reiasu.reiparticlesapi.event.events.entity.EntityPreMoveEvent;
import com.reiasu.reiparticlesapi.event.events.entity.EntityPrePlaceBlockEvent;
import com.reiasu.reiparticlesapi.event.events.entity.EntityPreTickEvent;
import com.reiasu.reiparticlesapi.event.events.entity.player.PlayerBlockBreakEvent;
import com.reiasu.reiparticlesapi.event.events.entity.player.PlayerDisconnectEvent;
import com.reiasu.reiparticlesapi.event.events.entity.player.PlayerItemDestroyEvent;
import com.reiasu.reiparticlesapi.event.events.entity.player.PlayerLoggedInEvent;
import com.reiasu.reiparticlesapi.event.events.entity.player.ServerPlayerDeathEvent;
import com.reiasu.reiparticlesapi.event.events.entity.player.ServerPlayerRespawnEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ForgeEventForwarder {
    private static boolean initialized;

    private ForgeEventForwarder() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) ->
                NeoForge.EVENT_BUS.post(new PlayerLoggedInEvent(event.getEntity())));

        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) ->
                NeoForge.EVENT_BUS.post(new PlayerDisconnectEvent(event.getEntity())));

        NeoForge.EVENT_BUS.addListener((PlayerDestroyItemEvent event) ->
                NeoForge.EVENT_BUS.post(new PlayerItemDestroyEvent(event.getEntity(), event.getOriginal())));

        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent event) ->
                NeoForge.EVENT_BUS.post(new ServerPlayerRespawnEvent(event.getEntity(), event.getEntity().level())));

        NeoForge.EVENT_BUS.addListener((LivingDeathEvent event) -> {
            if (!(event.getEntity() instanceof Player player)) {
                return;
            }
            ServerPlayerDeathEvent deathEvent = NeoForge.EVENT_BUS.post(new ServerPlayerDeathEvent(player, player.level(), event.getSource()));
            if (deathEvent.isCancelled()) {
                event.setCanceled(true);
            }
        });

        NeoForge.EVENT_BUS.addListener((BlockEvent.BreakEvent event) -> {
            Player player = event.getPlayer();
            if (player == null) {
                return;
            }
            PlayerBlockBreakEvent breakEvent = NeoForge.EVENT_BUS.post(
                    new PlayerBlockBreakEvent(player, player.level(), event.getPos(), event.getState())
            );
            if (breakEvent.isCancelled()) {
                event.setCanceled(true);
            }
        });

        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Pre event) -> {
            NeoForge.EVENT_BUS.post(new EntityPreTickEvent(event.getEntity()));
        });
        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Post event) -> {
            NeoForge.EVENT_BUS.post(new EntityPostTickEvent(event.getEntity()));
        });

        // Client/Server/World tick events are forwarded by ClientTickEventForwarder
        // and ReiParticlesAPIForge lifecycle hooks to avoid duplication.

        // Client world change (dimension switch / join)
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                NeoForge.EVENT_BUS.post(new ClientWorldChangeEvent(mc.level));
            }
        });

        // Entity place block
        NeoForge.EVENT_BUS.addListener((BlockEvent.EntityPlaceEvent event) -> {
            Entity entity = event.getEntity();
            if (entity == null) return;
            EntityPrePlaceBlockEvent placeEvent = NeoForge.EVENT_BUS.post(
                    new EntityPrePlaceBlockEvent(entity, entity.level(), event.getPos(), null)
            );
            if (placeEvent.isCancelled()) {
                event.setCanceled(true);
            }
        });

        // Entity movement approximation (via LivingTickEvent)
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) -> {
            Entity entity = event.getEntity();
            Vec3 pos = entity.position();
            Vec3 delta = entity.getDeltaMovement();
            if (delta.lengthSqr() < 1e-10) return;
            Vec3 target = pos.add(delta);
            EntityPreMoveEvent preMoveEvent = NeoForge.EVENT_BUS.post(new EntityPreMoveEvent(entity, delta));
            if (preMoveEvent.isCancelled()) return;
            NeoForge.EVENT_BUS.post(new EntityMoveEvent(entity, delta, target));
        });
    }
}

