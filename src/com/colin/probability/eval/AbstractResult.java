package com.colin.probability.eval;

import java.util.function.Function;

public abstract class AbstractResult implements EvalResult{
    private String out;
    private Object result;
    public AbstractResult(String output,Object result){
        out = output;
        this.result = result;
    }

    @Override
    public String output() {
        return out;
    }

    @Override
    public <T> T getValue(Function<Object, T> mapper) {
        return mapper.apply(result);
    }
}
