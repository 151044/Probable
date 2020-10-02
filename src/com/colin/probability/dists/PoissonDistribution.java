package com.colin.probability.dists;

import com.colin.probability.AbstractDistribution;
import com.colin.probability.Maths;

public class PoissonDistribution extends AbstractDistribution{
    public PoissonDistribution(double parameter) {
        super(parameter);
    }

    @Override
    public double getProbability(int start, int end) {
        double ans = 0.0;
        for(int i = start; i <= end; i++){
            ans += Math.pow(getParameter(),i) / Maths.fact(i);
        }
        return ans * Math.exp(-getParameter());
    }

    @Override
    public double getExpectation() {
        return getParameter();
    }

    @Override
    public double getVariance() {
        return getParameter();
    }
}
