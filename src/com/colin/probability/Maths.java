package com.colin.probability;

public class Maths {
    private Maths(){
        throw new AssertionError("Math object created!");
    }
    /**
     * Returns the value of the factorial of n. If n {@literal <}= 0, then 1 is returned.
     * @param n The number to factorial
     * @return The result of n!
     */
    public static long fact(int n){
        if(n <= 0){
            return 1;
        }
        long result = 1;
        for(int i = 1; i <= n; i++){
            result *= i;
        }
        return result;
    }

    /**
     * Returns the combination of the given values.
     * @param n The n value in nCr
     * @param r The r value in nCr
     * @return The value of the expression nCr
     */
    public static long comb(int n,int r){
        if(n < r){
            throw new IllegalArgumentException("n < r in nCr");
        }
        return fact(n)/(fact(r)*fact(n-r));
    }
}
