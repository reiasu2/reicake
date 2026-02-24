package com.reiasu.reiparticlesapi.barrages;

import com.reiasu.reiparticlesapi.network.particle.ServerController;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Barrage {

        Vec3 getLoc();

    void setLoc(Vec3 loc);

        ServerLevel getWorld();

        HitBox getHitBox();

    void setHitBox(HitBox hitBox);

        @Nullable
    LivingEntity getShooter();

    void setShooter(@Nullable LivingEntity shooter);

        Vec3 getDirection();

    void setDirection(Vec3 direction);

        boolean getLaunch();

    void setLaunch(boolean launch);

        boolean getValid();

        BarrageOption getOptions();

        UUID getUuid();

        ServerController<?> getBindControl();

        void hit(BarrageHitResult result);

        void onHit(BarrageHitResult result);

        boolean noclip();

        void tick();
}
