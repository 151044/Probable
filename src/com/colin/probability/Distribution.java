package com.colin.probability;

public interface Distribution {
    double getParameter();
    int getTrials();
    double getProbability(int start, int end);
    double getExpectation();
    double getVariance();
}
