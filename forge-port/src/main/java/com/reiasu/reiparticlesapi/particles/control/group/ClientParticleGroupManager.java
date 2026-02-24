package com.reiasu.reiparticlesapi.particles.control.group;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public final class ClientParticleGroupManager {
    public static final ClientParticleGroupManager INSTANCE = new ClientParticleGroupManager();

    private final ConcurrentHashMap<UUID, ControllableParticleGroup> visibleControls = new ConcurrentHashMap<>();
    private final HashMap<Class<? extends ControllableParticleGroup>, ControllableParticleGroupProvider> registerBuilders = new HashMap<>();

    private ClientParticleGroupManager() {}

    public void register(Class<? extends ControllableParticleGroup> type, ControllableParticleGroupProvider provider) {
        registerBuilders.put(type, provider);
    }

    public ControllableParticleGroupProvider getBuilder(Class<? extends ControllableParticleGroup> type) {
        return registerBuilders.get(type);
    }

    public ControllableParticleGroup getControlGroup(UUID groupId) {
        return visibleControls.get(groupId);
    }

    public void addVisibleGroup(ControllableParticleGroup group) {
        visibleControls.put(group.getUuid(), group);
    }

    public void removeVisible(UUID id) {
        ControllableParticleGroup group = visibleControls.get(id);
        if (group != null) {
            group.remove();
        }
        visibleControls.remove(id);
    }

    public void clearAllVisible() {
        for (ControllableParticleGroup group : visibleControls.values()) {
            group.setCanceled(true);
        }
        visibleControls.clear();
    }

        public void doClientTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (player.isRemoved()) {
            visibleControls.clear();
            return;
        }
        for (ControllableParticleGroup group : visibleControls.values()) {
            group.tick();
        }
    }
}
