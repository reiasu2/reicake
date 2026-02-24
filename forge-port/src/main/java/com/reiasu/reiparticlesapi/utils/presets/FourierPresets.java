package com.reiasu.reiparticlesapi.utils.presets;

import com.reiasu.reiparticlesapi.utils.builder.FourierSeriesBuilder;

public final class FourierPresets {
    public static FourierSeriesBuilder pentagon() {
        return new FourierSeriesBuilder()
                .addFourier(3.0, 2.0, 0.0)
                .addFourier(1.0, -8.0, 0.0)
                .addFourier(3.0, 2.0, 0.0);
    }

    public static FourierSeriesBuilder clover() {
        return new FourierSeriesBuilder()
                .addFourier(3.0, 2.0, 0.0)
                .addFourier(1.0, -8.0, 0.0)
                .addFourier(-3.0, 2.0, 0.0)
                .addFourier(6.0, -2.0, 0.0);
    }

    public static FourierSeriesBuilder boomerang() {
        return new FourierSeriesBuilder()
                .addFourier(3.0, 1.0, 0.0)
                .addFourier(7.0, -2.0, 0.0)
                .addFourier(2.0, 4.0, 0.0);
    }

    public static FourierSeriesBuilder runesOnAllSides() {
        return new FourierSeriesBuilder()
                .addFourier(3.0, -1.0, 0.0)
                .addFourier(7.0, -5.0, 0.0)
                .addFourier(2.0, 11.0, 0.0);
    }

    public static FourierSeriesBuilder knot() {
        return new FourierSeriesBuilder()
                .addFourier(3.0, -1.0, 0.0)
                .addFourier(0.0, -5.0, 0.0)
                .addFourier(4.0, 11.0, 0.0)
                .addFourier(4.0, -4.0, 0.0);
    }

    public static FourierSeriesBuilder circlesAndTriangles() {
        return new FourierSeriesBuilder()
                .addFourier(3.0, -1.0, 0.0)
                .addFourier(0.0, -5.0, 0.0)
                .addFourier(4.0, 11.0, 0.0)
                .addFourier(5.0, 2.0, 0.0);
    }

    public static FourierSeriesBuilder bowsOnAllSides() {
        return new FourierSeriesBuilder()
                .addFourier(3.0, -1.0, 0.0)
                .addFourier(-3.0, -5.0, 0.0)
                .addFourier(4.0, 11.0, 0.0)
                .addFourier(5.0, 3.0, 0.0)
                .addFourier(-3.0, -5.0, 0.0)
                .addFourier(1.0, 3.0, 0.0);
    }

    public static FourierSeriesBuilder rhombic() {
        return new FourierSeriesBuilder()
                .addFourier(-3.0, 1.0, 0.0)
                .addFourier(-9.0, -3.0, 0.0)
                .addFourier(8.0, -3.0, 0.0);
    }
}
