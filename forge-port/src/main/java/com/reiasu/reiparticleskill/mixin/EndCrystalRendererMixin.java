package com.reiasu.reiparticleskill.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.reiasu.reiparticleskill.config.SkillClientConfig;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndCrystalRenderer.class)
public abstract class EndCrystalRendererMixin {

    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;getBeamTarget()Lnet/minecraft/core/BlockPos;")
    )
    private BlockPos reiparticleskill$hideBeam(EndCrystal crystal, Operation<BlockPos> original) {
        if (SkillClientConfig.INSTANCE.isSuppressCrystalBeam()) {
            return null;
        }
        return original.call(crystal);
    }
}
