package com.colin.probability.eval;

public class Success extends AbstractResult{
    public Success(String output) {
        super(output);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

}
