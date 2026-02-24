package com.reiasu.reiparticlesapi.utils;

public final class RotationMatrix {
    private final double[][] matrix;

    private RotationMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

        public RelativeLocation applyToClone(RelativeLocation point) {
        return applyTo(point.copy());
    }

        public RelativeLocation applyTo(RelativeLocation point) {
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();
        point.setX(matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z);
        point.setY(matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z);
        point.setZ(matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z);
        return point;
    }

        public static RotationMatrix fromAxisAngle(RelativeLocation axis, double angle) {
        RelativeLocation u = axis.copy().normalize();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double omc = 1.0 - cos;
        double ux = u.getX(), uy = u.getY(), uz = u.getZ();
        double[][] m = {
                {cos + ux * ux * omc, ux * uy * omc - uz * sin, ux * uz * omc + uy * sin},
                {uy * ux * omc + uz * sin, cos + uy * uy * omc, uy * uz * omc - ux * sin},
                {uz * ux * omc - uy * sin, uz * uy * omc + ux * sin, cos + uz * uz * omc}
        };
        return new RotationMatrix(m);
    }
}
