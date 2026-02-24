package com.reiasu.reiparticleskill.end.respawn.runtime.emitter.client;

import com.reiasu.reiparticlesapi.annotations.ReiAutoRegister;
import com.reiasu.reiparticlesapi.network.particle.emitters.AutoParticleEmitters;
import com.reiasu.reiparticleskill.util.ClientParticleHelper;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@ReiAutoRegister
public final class ClientCloudRingEmitter extends AutoParticleEmitters {
    public static final ResourceLocation CODEC_ID = ResourceLocation.fromNamespaceAndPath("reiparticleskill", "client_cloud_ring");
    private static final Vector3f MAIN_COLOR = new Vector3f(210f / 255f, 80f / 255f, 1.0f);
    private static final double TAU = Math.PI * 2.0;
    private static final int SCALE_TICKS = 24;

    private final RandomSource random = RandomSource.create();

    private double radius = 1.0;
    private double discrete = 0.0;
    private int countMin = 80;
    private int countMax = 260;
    private double rotateSpeed = 0.05;
    private double minSize = 0.2;
    private double maxSize = 1.0;
    private double yOffset;

    public ClientCloudRingEmitter() {
        super();
    }

    public ClientCloudRingEmitter(Vec3 center, Level level, int maxTick,
                                  double radius, int countMin, int countMax,
                                  double discrete, double rotateSpeed,
                                  double minSize, double maxSize, double yOffset) {
        super();
        if (center != null && level != null) {
            bind(level, center.x, center.y, center.z);
        }
        setMaxTick(maxTick);
        this.radius = radius;
        this.countMin = countMin;
        this.countMax = countMax;
        this.discrete = discrete;
        this.rotateSpeed = rotateSpeed;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.yOffset = yOffset;
    }

    @Override
    protected void emitTick() {
        Level level = level();
        if (level == null || !level.isClientSide()) return;
        renderCloud(level, position(), getTick());
    }

    private void renderCloud(Level level, Vec3 center, int tick) {
        double cx = center.x;
        double cy = center.y;
        double cz = center.z;

        int cMin = Math.max(1, countMin);
        int cMax = Math.max(cMin + 1, countMax);
        int count = random.nextInt(cMin, cMax);
        double scale = easeScale(tick);
        double rotation = tick * rotateSpeed;
        double breath = 1.0 + 0.05 * Math.sin(tick * 0.07);
        double r = radius * scale * breath;
        double disc = discrete * scale;
        double yOff = yOffset * scale;

        double waveLobes = 4.0;
        double waveAmplitude = radius * 0.08 * scale;
        double wavePhase = tick * 0.12;

        float dustSize = (float) Math.max(0.4, Math.min(4.0,
                randRange(minSize, maxSize) * scale * 2.5));
        DustParticleOptions dust = new DustParticleOptions(MAIN_COLOR, dustSize);
        DustParticleOptions dustLg = new DustParticleOptions(MAIN_COLOR, Math.min(4.0f, dustSize * 1.5f));

        for (int i = 0; i < count; i++) {
            double angleJitter = (random.nextDouble() - 0.5) * 0.15;
            double angle = (TAU * i) / (double) count + rotation + angleJitter;
            double radialWobble = 1.0 + 0.1 * Math.sin(angle * 3.0 + tick * 0.05);
            double localR = r * radialWobble;
            double waveY = waveAmplitude * Math.sin(waveLobes * angle - wavePhase);

            double px = Math.cos(angle) * localR;
            double pz = Math.sin(angle) * localR;

            if (disc > 0) {
                double jR = random.nextDouble() * disc;
                double jA = random.nextDouble() * TAU;
                px += Math.cos(jA) * jR;
                pz += Math.sin(jA) * jR;
            }

            double wx = cx + px;
            double wy = cy + yOff + waveY;
            double wz = cz + pz;

            double tgx = -Math.sin(angle) * 0.03;
            double tgz =  Math.cos(angle) * 0.03;

            ClientParticleHelper.addForce(level, dust, wx, wy, wz,
                    3, 0.18 + Math.abs(tgx), 0.12, 0.18 + Math.abs(tgz), 0.02);

            if ((i & 1) == 0) {
                ClientParticleHelper.addForce(level, dustLg, wx, wy, wz,
                        2, 0.12, 0.08, 0.12, 0.015);
            }
        }
    }

    // FIXME: cloud and enchant share a lot of ring logic, could extract base class
    private double randRange(double lo, double hi) {
        return lo + random.nextDouble() * (hi - lo);
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
        buf.writeDouble(radius);
        buf.writeDouble(discrete);
        buf.writeInt(countMin);
        buf.writeInt(countMax);
        buf.writeDouble(rotateSpeed);
        buf.writeDouble(minSize);
        buf.writeDouble(maxSize);
        buf.writeDouble(yOffset);
    }

    @Override
    protected void readPayload(FriendlyByteBuf buf) {
        radius = buf.readDouble();
        discrete = buf.readDouble();
        countMin = buf.readInt();
        countMax = buf.readInt();
        rotateSpeed = buf.readDouble();
        minSize = buf.readDouble();
        maxSize = buf.readDouble();
        yOffset = buf.readDouble();
    }

    public static ClientCloudRingEmitter decode(FriendlyByteBuf buf) {
        ClientCloudRingEmitter e = new ClientCloudRingEmitter();
        e.decodeFromBuffer(buf);
        return e;
    }
}
