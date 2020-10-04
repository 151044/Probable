package com.colin.probability;

import com.colin.probability.eval.EvalResult;
import com.colin.probability.eval.EvalSnippet;

import java.util.Scanner;

public class Main {
    private static Storage storage = new Storage();
    public static void main(String[] args) {
        System.out.println("Welcome to pcalc.");
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter(System.lineSeparator());
        System.out.print(">> ");
        while(scan.hasNext()){
            String op = scan.next();
            EvalSnippet eval = new EvalSnippet(op);
            EvalResult evalR = eval.eval();
            System.out.println(evalR.output());
            System.out.print(">> ");
        }
    }
    public static Storage getStorage(){
        return storage;
    }
}
