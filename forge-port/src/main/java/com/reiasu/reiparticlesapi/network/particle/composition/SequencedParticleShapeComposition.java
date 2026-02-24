package com.reiasu.reiparticlesapi.network.particle.composition;

import com.reiasu.reiparticlesapi.utils.RelativeLocation;
import com.reiasu.reiparticlesapi.utils.builder.PointsBuilder;
import com.reiasu.reiparticlesapi.utils.helper.ScaleHelper;
import com.reiasu.reiparticlesapi.utils.helper.StatusHelper;
import com.reiasu.reiparticlesapi.utils.helper.impl.composition.CompositionBezierScaleHelper;
import com.reiasu.reiparticlesapi.utils.helper.impl.composition.CompositionScaleHelper;
import net.minecraft.world.phys.Vec3;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;

public final class SequencedParticleShapeComposition extends SequencedParticleComposition {

    private final List<Map.Entry<PointsBuilder, BiFunction<RelativeLocation, Integer, CompositionData>>> points = new ArrayList<>();
    private final List<Consumer<SequencedParticleShapeComposition>> invokes = new ArrayList<>();
    private final List<BiFunction<SequencedParticleShapeComposition, SortedMap<CompositionData, RelativeLocation>, Void>> beforeInvokes = new ArrayList<>();

    private ScaleHelper scaleHelper;
    private int spawnAge;
    private boolean scalePreTick;
    private boolean scaleReversed;

    public SequencedParticleShapeComposition(UUID uuid) {
        super(Vec3.ZERO);
        setControlUUID(uuid);
    }

    // --”€--”€--”€ Property accessors --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

    public int getSpawnAge() {
        return spawnAge;
    }

    public void setSpawnAge(int spawnAge) {
        this.spawnAge = spawnAge;
    }

    public boolean getScalePreTick() {
        return scalePreTick;
    }

    public boolean getScaleReversed() {
        return scaleReversed;
    }

    // --”€--”€--”€ Scale helper configuration --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

        public SequencedParticleShapeComposition loadScaleHelper(double min, double max, int scalingTick) {
        CompositionScaleHelper helper = new CompositionScaleHelper(min, max, scalingTick);
        helper.loadComposition(this);
        this.scaleHelper = helper;
        this.scalePreTick = true;
        return this;
    }

        public SequencedParticleShapeComposition loadScaleHelperBezierValue(
            double minScale, double maxScale, int scaleTick,
            RelativeLocation c1, RelativeLocation c2) {
        CompositionBezierScaleHelper helper = new CompositionBezierScaleHelper(scaleTick, minScale, maxScale, c1, c2);
        this.scaleHelper = helper;
        this.scalePreTick = true;
        helper.loadComposition(this);
        return this;
    }

    // --”€--”€--”€ Display action registration --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

        public SequencedParticleShapeComposition applyDisplayAction(Consumer<SequencedParticleShapeComposition> action) {
        invokes.add(action);
        return this;
    }

        public SequencedParticleShapeComposition applyBeforeDisplayAction(
            BiFunction<SequencedParticleShapeComposition, SortedMap<CompositionData, RelativeLocation>, Void> action) {
        beforeInvokes.add(action);
        return this;
    }

    // --”€--”€--”€ Point registration --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

        public SequencedParticleShapeComposition applyPoint(
            RelativeLocation point,
            BiFunction<RelativeLocation, Integer, CompositionData> dataSupplier) {
        points.add(new AbstractMap.SimpleEntry<>(
                new PointsBuilder().addPoint(point),
                dataSupplier
        ));
        return this;
    }

        public SequencedParticleShapeComposition applyPointRel(
            RelativeLocation point,
            Function<RelativeLocation, CompositionData> dataSupplier) {
        points.add(new AbstractMap.SimpleEntry<>(
                new PointsBuilder().addPoint(point),
                (BiFunction<RelativeLocation, Integer, CompositionData>) (rel, o) -> dataSupplier.apply(rel)
        ));
        return this;
    }

        public SequencedParticleShapeComposition applyPointI(
            RelativeLocation point,
            Function<Integer, CompositionData> dataSupplier) {
        points.add(new AbstractMap.SimpleEntry<>(
                new PointsBuilder().addPoint(point),
                (BiFunction<RelativeLocation, Integer, CompositionData>) (rel, o) -> dataSupplier.apply(o)
        ));
        return this;
    }

        public SequencedParticleShapeComposition applyBuilder(
            PointsBuilder builder,
            BiFunction<RelativeLocation, Integer, CompositionData> dataSupplier) {
        points.add(new AbstractMap.SimpleEntry<>(builder, dataSupplier));
        return this;
    }

        public SequencedParticleShapeComposition applyBuilderI(
            PointsBuilder builder,
            Function<Integer, CompositionData> dataSupplier) {
        points.add(new AbstractMap.SimpleEntry<>(
                builder,
                (BiFunction<RelativeLocation, Integer, CompositionData>) (rel, o) -> dataSupplier.apply(o)
        ));
        return this;
    }

        public SequencedParticleShapeComposition applyBuilderRel(
            PointsBuilder builder,
            Function<RelativeLocation, CompositionData> dataSupplier) {
        points.add(new AbstractMap.SimpleEntry<>(
                builder,
                (BiFunction<RelativeLocation, Integer, CompositionData>) (rel, o) -> dataSupplier.apply(rel)
        ));
        return this;
    }

    // --”€--”€--”€ Scale-reversal on status change --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

        public SequencedParticleShapeComposition setReversedScaleOnDisableStatus(StatusHelper status) {
        addPreTickAction(pc -> {
            if (status.getDisplayStatus() == StatusHelper.Status.DISABLE.id()) {
                scaleReversed = true;
            }
        });
        return this;
    }

        public SequencedParticleShapeComposition setReversedScaleOnCompositionStatus(ParticleComposition composition) {
        addPreTickAction(pc -> {
            if (composition.getStatus().getCurrentStatus() == StatusHelper.Status.DISABLE) {
                scaleReversed = true;
            }
        });
        return this;
    }

    // --”€--”€--”€ Abstract/override implementations --”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€--”€

    @Override
    public SortedMap<CompositionData, RelativeLocation> getParticleSequenced() {
        SortedMap<CompositionData, RelativeLocation> result = new TreeMap<>();
        AtomicInteger order = new AtomicInteger(0);
        for (Map.Entry<PointsBuilder, BiFunction<RelativeLocation, Integer, CompositionData>> entry : points) {
            PointsBuilder builder = entry.getKey();
            BiFunction<RelativeLocation, Integer, CompositionData> supplier = entry.getValue();
            result.putAll(builder.createWithCompositionData(rel -> {
                int o = order.getAndIncrement();
                CompositionData data = supplier.apply(rel, o);
                data.setOrder(o);
                return data;
            }));
        }
        return result;
    }

    @Override
    public void beforeDisplaySequenced(SortedMap<CompositionData, RelativeLocation> map) {
        super.beforeDisplaySequenced(map);
        for (BiFunction<SequencedParticleShapeComposition, SortedMap<CompositionData, RelativeLocation>, Void> action : beforeInvokes) {
            action.apply(this, map);
        }
    }

    @Override
    public void onDisplay() {
        getAnimate().clientOnly();
        for (Consumer<SequencedParticleShapeComposition> action : invokes) {
            action.accept(this);
        }
        addPreTickAction(pc -> {
            spawnAge++;
            if (scaleHelper == null || !scalePreTick) return;
            if (!scaleReversed) {
                scaleHelper.doScale();
            } else {
                scaleHelper.doScaleReversed();
            }
        });
    }
}
