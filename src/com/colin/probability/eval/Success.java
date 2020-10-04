package com.colin.probability.eval;

public class Success extends AbstractResult{
    public Success(String output,Object result) {
        super(output,result);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

}
