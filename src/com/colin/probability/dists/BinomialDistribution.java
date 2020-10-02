package com.colin.probability.dists;

import com.colin.probability.AbstractDistribution;
import com.colin.probability.Maths;

public class BinomialDistribution extends AbstractDistribution {
    private int trials = 0;
    public BinomialDistribution(double parameter, int trials){
        super(parameter);
        this.trials = trials;
    }

    @Override
    public int getTrials() {
        return trials;
    }

    @Override
    public double getProbability(int start, int end) {
        double sum = 0.0;
        for(int i = start; i <= end; i++){
            sum += Maths.comb(trials,i) * Math.pow(getParameter(), i) * Math.pow(1.0 - getParameter(), trials - i);
        }
        return sum;
    }

    @Override
    public double getExpectation() {
        return trials * getParameter();
    }

    @Override
    public double getVariance() {
        return trials * getParameter() * (1.0 - getParameter());
    }
}
