package com.reiasu.reiparticleskill.client;

import com.reiasu.reiparticleskill.ReiParticleSkillForge;
import com.reiasu.reiparticleskill.entities.SkillEntityTypes;
import com.reiasu.reiparticleskill.entities.renderer.BarrageItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import com.reiasu.reiparticleskill.config.SkillClientConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;

@net.neoforged.fml.common.EventBusSubscriber(modid = ReiParticleSkillForge.MOD_ID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SkillClientRegistries {
    private SkillClientRegistries() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SkillEntityTypes.BARRAGE_ITEM.get(), BarrageItemRenderer::new);
        if (SkillClientConfig.INSTANCE.isSuppressCrystalBeam()) {
            event.registerEntityRenderer(EntityType.END_CRYSTAL, NoBeamEndCrystalRenderer::new);
        }
    }
}
