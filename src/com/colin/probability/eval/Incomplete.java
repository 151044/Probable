package com.colin.probability.eval;

public class Incomplete extends AbstractResult{
    public Incomplete(String output) {
        super("Input statement is incomplete: " + output,null);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
