package com.colin.probability;

public abstract class AbstractDistribution implements Distribution{
    private double parameter = 0.0;
    public AbstractDistribution(double parameter){
        this.parameter = parameter;
    }
    @Override
    public double getParameter() {
        return parameter;
    }

    @Override
    public int getTrials() {
        return -1;
    }
}
