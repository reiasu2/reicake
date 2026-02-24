package com.reiasu.reiparticlesapi.utils.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface ModelPartPointCollector {
        List<Vec3> collectSamplePoints(ModelPart root, PoseStack poseStack, int density, boolean pixelToUnit);

        default List<Vec3> collectSamplePoints(ModelPart root, PoseStack poseStack, int density) {
        return collectSamplePoints(root, poseStack, density, true);
    }
}
