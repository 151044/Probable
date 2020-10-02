package com.colin.probability;

public class GeometricDistribution extends AbstractDistribution{
    public GeometricDistribution(double parameter) {
        super(parameter);
    }

    @Override
    public double getProbability(int start, int end) {
        return 0;
    }
}
