package com.reiasu.reiparticleskill.end.respawn.runtime.emitter.client;

import com.reiasu.reiparticlesapi.annotations.ReiAutoRegister;
import com.reiasu.reiparticlesapi.network.particle.emitters.AutoParticleEmitters;
import com.reiasu.reiparticleskill.util.ClientParticleHelper;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@ReiAutoRegister
public final class ClientCenterEmitter extends AutoParticleEmitters {
    public static final ResourceLocation CODEC_ID = ResourceLocation.fromNamespaceAndPath("reiparticleskill", "client_center");
    private static final Vector3f MAIN_COLOR = new Vector3f(210f / 255f, 80f / 255f, 1.0f);
    private static final double TAU = Math.PI * 2.0;
    private static final int SCALE_TICKS = 24;

    private static final int LAYER1_ROSE_POINTS = 140;
    private static final int LAYER1_STAR_POINTS = 40;
    private static final int LAYER1_RING_POINTS = 30;
    private static final int LAYER2_LISSAJOUS_POINTS = 55;
    private static final int LAYER2_REPEAT = 3;
    private static final int LAYER3_RING_POINTS = 40;

    private final RandomSource random = RandomSource.create();

    private double yOffset;

    public ClientCenterEmitter() {
        super();
    }

    public ClientCenterEmitter(Vec3 center, Level level, int maxTick, double yOffset) {
        super();
        if (center != null && level != null) {
            bind(level, center.x, center.y, center.z);
        }
        setMaxTick(maxTick);
        this.yOffset = yOffset;
    }

    @Override
    protected void emitTick() {
        Level level = level();
        if (level == null || !level.isClientSide()) return;
        renderCenter(level, position(), getTick());
    }

    private void renderCenter(Level level, Vec3 center, int tick) {
        double cx = center.x;
        double cy = center.y + yOffset;
        double cz = center.z;

        double scale = easeScale(tick);
        double breath = 1.0 + 0.15 * Math.sin(tick * 0.08);
        double rotA = tick * 0.008;
        double rotB = tick * -0.012;
        double rotC = tick * -0.006;

        DustParticleOptions dustMed = new DustParticleOptions(MAIN_COLOR, 3.0f);
        DustParticleOptions dustSm = new DustParticleOptions(MAIN_COLOR, 2.2f);
        DustParticleOptions dustLg = new DustParticleOptions(MAIN_COLOR, 4.0f);

        // Precompute sines/cosines
        double cosRotA = Math.cos(rotA);
        double sinRotA = Math.sin(rotA);

        // Layer 1: Rhodonea curve r = cos(5/3 * theta)
        for (int i = 0; i < LAYER1_ROSE_POINTS; i++) {
            double theta = 3.0 * TAU * i / (double) LAYER1_ROSE_POINTS;
            double r = Math.cos(5.0 / 3.0 * theta) * 42.0 * scale * breath;
            double rx = r * Math.cos(theta);
            double rz = r * Math.sin(theta);
            
            // rotateY
            double relX = rx * cosRotA - rz * sinRotA;
            double relZ = rx * sinRotA + rz * cosRotA;
            
            addDust(level, cx + relX, cy, cz + relZ, dustMed);
        }

        // Layer 1: Dual pentagram
        emitStar(level, cx, cy, cz, 5, 48.0 * scale * breath, 0.0, LAYER1_STAR_POINTS, cosRotA, sinRotA, dustMed);
        emitStar(level, cx, cy, cz, 5, 36.0 * scale * breath, Math.PI / 5.0, LAYER1_STAR_POINTS, cosRotA, sinRotA, dustMed);

        // Layer 1: Breathing inner ring
        double ringR = 28.0 * scale * breath;
        for (int i = 0; i < LAYER1_RING_POINTS; i++) {
            double a = rotA + TAU * i / (double) LAYER1_RING_POINTS;
            double wobble = 1.0 + 0.12 * Math.sin(a * 6.0 + tick * 0.1);
            double relX = Math.cos(a) * ringR * wobble;
            double relZ = Math.sin(a) * ringR * wobble;
            addDust(level, cx + relX, cy, cz + relZ, dustMed);
            if (i % 3 == 0) {
                addEnchant(level, cx + relX, cy, cz + relZ);
            }
        }

        // Layer 2: Three Lissajous figures
        double cosRotB = Math.cos(rotB);
        double sinRotB = Math.sin(rotB);
        for (int ring = 0; ring < LAYER2_REPEAT; ring++) {
            double pitch = (TAU / 9.0) * ring;
            double cosPitch = Math.cos(pitch);
            double sinPitch = Math.sin(pitch);
            double phase = ring * Math.PI / 3.0;
            for (int i = 0; i < LAYER2_LISSAJOUS_POINTS; i++) {
                double t = TAU * i / (double) LAYER2_LISSAJOUS_POINTS;
                double lx = Math.sin(3.0 * t + Math.PI / 4.0 + phase) * 14.0 * scale;
                double lz = Math.sin(4.0 * t) * 14.0 * scale;
                
                // rotateX
                double px = lx;
                double pz = lz * cosPitch;
                double py = lz * sinPitch;
                
                // rotateY
                double relX = px * cosRotB - pz * sinRotB;
                double relZ = px * sinRotB + pz * cosRotB;
                double relY = py;

                addDust(level, cx + relX, cy + relY, cz + relZ, dustSm);
            }
        }

        // Layer 3: Large pulsing enchant ring
        double outerR = 72.0 * scale * breath;
        for (int i = 0; i < LAYER3_RING_POINTS; i++) {
            double a = rotC + TAU * i / (double) LAYER3_RING_POINTS;
            double wobble = 1.0 + 0.08 * Math.sin(a * 5.0 - tick * 0.06);
            double relX = Math.cos(a) * outerR * wobble;
            double relZ = Math.sin(a) * outerR * wobble;
            addDust(level, cx + relX, cy, cz + relZ, dustLg);
            if (i % 2 == 0) {
                addEnchant(level, cx + relX, cy, cz + relZ);
            }
        }
    }

    private void emitStar(Level level, double cx, double cy, double cz,
                          int points, double radius, double angleOffset,
                          int samples, double cosRot, double sinRot, DustParticleOptions dust) {
        int skip = 2;
        for (int i = 0; i < samples; i++) {
            double u = (i / (double) samples) * points;
            int seg = (int) Math.floor(u);
            double frac = u - seg;
            int v0 = (seg * skip) % points;
            int v1 = ((seg + 1) * skip) % points;
            double a0 = angleOffset + TAU * v0 / (double) points;
            double a1 = angleOffset + TAU * v1 / (double) points;
            double rx = Math.cos(a0) * radius + (Math.cos(a1) - Math.cos(a0)) * radius * frac;
            double rz = Math.sin(a0) * radius + (Math.sin(a1) - Math.sin(a0)) * radius * frac;
            
            double relX = rx * cosRot - rz * sinRot;
            double relZ = rx * sinRot + rz * cosRot;
            
            addDust(level, cx + relX, cy, cz + relZ, dust);
        }
    }

    private void addDust(Level level, double x, double y, double z, DustParticleOptions dust) {
        ClientParticleHelper.addForce(level, dust, x, y, z, 3, 0.2, 0.1, 0.2, 0.015);
    }

    private void addEnchant(Level level, double x, double y, double z) {
        ClientParticleHelper.addForce(level, ParticleTypes.ENCHANT, x, y, z,
                0, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02,
                random.nextGaussian() * 0.02, 1.0);
    }

    private double easeScale(int tick) {
        if (tick <= 0) return 0.01;
        if (tick >= SCALE_TICKS) return 1.0;
        double t = tick / (double) SCALE_TICKS;
        double inv = 1.0 - t;
        return 0.01 + 0.99 * (1.0 - inv * inv * inv * inv * inv);
    }

    @Override
    protected void writePayload(FriendlyByteBuf buf) {
        buf.writeDouble(yOffset);
    }

    @Override
    protected void readPayload(FriendlyByteBuf buf) {
        yOffset = buf.readDouble();
    }

    public static ClientCenterEmitter decode(FriendlyByteBuf buf) {
        ClientCenterEmitter e = new ClientCenterEmitter();
        e.decodeFromBuffer(buf);
        return e;
    }
}
