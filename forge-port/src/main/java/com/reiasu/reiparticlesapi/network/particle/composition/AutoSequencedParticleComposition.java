package com.reiasu.reiparticlesapi.network.particle.composition;

import com.reiasu.reiparticlesapi.annotations.codec.BufferCodec;
import com.reiasu.reiparticlesapi.annotations.composition.handler.ParticleCompositionHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AutoSequencedParticleComposition extends SequencedParticleComposition {

    protected AutoSequencedParticleComposition(Vec3 position, Level world) {
        super(position, world);
    }

    protected AutoSequencedParticleComposition(Vec3 position) {
        super(position);
    }

        public BufferCodec<ParticleComposition> getCodec() {
        return ParticleCompositionHelper.INSTANCE.generateCodec(this);
    }
}
