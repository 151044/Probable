package com.colin.probability.eval;

public class Failure extends AbstractResult{
    public Failure(String output) {
        super("Failed to parse statement: " + output,null);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
