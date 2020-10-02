package com.colin.probability.dists;

import com.colin.probability.AbstractDistribution;

public class GeometricDistribution extends AbstractDistribution {
    public GeometricDistribution(double parameter) {
        super(parameter);
    }

    @Override
    public double getProbability(int start, int end) {
        double sum = 0.0;
        for(int i = start; i <= end; i++){
            sum += getParameter() * Math.pow(1 - getParameter(),i - 1);
        }
        return sum;
    }

    @Override
    public double getExpectation() {
        return 1.0 / getParameter();
    }

    @Override
    public double getVariance() {
        return (1 - getParameter()) / (getParameter() * getParameter());
    }
}
