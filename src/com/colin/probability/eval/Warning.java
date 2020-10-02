package com.colin.probability.eval;

public class Warning extends AbstractResult{
    public Warning(String output) {
        super("Warning: " + output);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
