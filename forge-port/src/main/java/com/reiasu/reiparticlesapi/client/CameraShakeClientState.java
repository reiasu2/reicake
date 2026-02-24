package com.reiasu.reiparticlesapi.client;

import com.reiasu.reiparticlesapi.ReiParticlesAPIForge;
import com.reiasu.reiparticlesapi.network.packet.CameraShakeS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ReiParticlesAPIForge.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CameraShakeClientState {
    private static final RandomSource RANDOM = RandomSource.create();

    private static int remainingTicks = 0;
    private static double amplitude = 0.0;
    private static double range = -1.0;
    private static Vec3 origin = Vec3.ZERO;

    private CameraShakeClientState() {
    }

    public static void start(CameraShakeS2CPacket packet) {
        remainingTicks = Math.max(remainingTicks, packet.tick());
        amplitude = Math.max(amplitude, packet.amplitude());
        range = packet.range();
        origin = packet.origin();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (remainingTicks <= 0) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            remainingTicks = 0;
            return;
        }

        if (range > 0.0 && player.position().distanceTo(origin) > range) {
            remainingTicks--;
            return;
        }

        double fade = remainingTicks <= 1 ? 0.0 : (double) remainingTicks / (double) Math.max(2, remainingTicks + 1);
        double amount = amplitude * Math.max(0.15, fade) * 0.45;
        float yawShake = (float) ((RANDOM.nextDouble() - 0.5) * amount);
        float pitchShake = (float) ((RANDOM.nextDouble() - 0.5) * amount);
        player.turn(yawShake, pitchShake);

        remainingTicks--;
        if (remainingTicks <= 0) {
            amplitude = 0.0;
            range = -1.0;
            origin = Vec3.ZERO;
        }
    }
}
