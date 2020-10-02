package com.colin.probability.eval;

public abstract class AbstractResult implements EvalResult{
    private String out;
    public AbstractResult(String output){
        out = output;
    }

    @Override
    public String output() {
        return out;
    }
}
