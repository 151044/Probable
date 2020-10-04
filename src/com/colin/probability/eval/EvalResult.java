package com.colin.probability.eval;

import java.util.function.Function;

public interface EvalResult {
    boolean isSuccess();
    String output();
    <T> T getValue(Function<Object,T> mapper);
}
