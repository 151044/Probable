package com.colin.probability.eval;

import com.colin.probability.Main;
import com.colin.probability.Storage;
import com.colin.probability.dists.BinomialDistribution;
import com.colin.probability.dists.GeometricDistribution;
import com.colin.probability.dists.PoissonDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EvalSnippet {
    private static final Pattern SYM = Pattern.compile("[+\\-*/^]");
    private final String toEval;
    public EvalSnippet(String toEval){
        this.toEval = toEval;
    }
    public EvalResult eval(){
        Storage store = Main.getStorage();
        if(toEval.contains("~")){
            System.out.println(toEval);
            String[] arr = toEval.split("~");
            boolean doOverwrite = Arrays.asList(arr).contains("--overwrite");
            if(arr.length != 2 || (doOverwrite && arr.length != 3)){
                return new Incomplete(toEval + " is missing either the name of the random variable or the distribution.");
            }
            String str = arr[1];
            int startPos = str.lastIndexOf("(");
            int closePos = str.lastIndexOf(")");
            if(startPos == -1){
                return new Incomplete("Start bracket is missing.");
            }else if(closePos == -1){
                return new Incomplete("End bracket is missing.");
            }else if(startPos > closePos){
                return new Failure("Close bracket before start bracket: " + toEval);
            }
            String params = str.substring(startPos + 1,closePos);
            System.out.println(params);
            if(str.contains("Po")){
                double p;
                try{
                    if(hasOperators(params)){
                        EvalResult eval = evalArithmetic(params);
                        if(eval.isSuccess()){
                            p = eval.getValue((Object obj) -> (double) obj);
                        }else{
                            return eval;
                        }
                    }else {
                        p = Double.parseDouble(params);
                    }
                    if (p < 0.0 || p > 1.0) {
                        return new Failure("Probability for distribution out of range!");
                    }
                    if (store.hasDistribution(arr[0])) {
                        if (doOverwrite) {
                            PoissonDistribution dist = new PoissonDistribution(p);
                            store.addDistribution(arr[0], dist);
                            return new Warning("Overwritten original value of " + arr[0] + ".", dist);
                        } else {
                            return new Failure("Input distribution with the name " + arr[0] + " already exists.");
                        }
                    } else {
                        PoissonDistribution dist = new PoissonDistribution(p);
                        store.addDistribution(arr[0], dist);
                        return new Success("Successfully added " + arr[0] + ".", dist);
                    }
                }catch(NumberFormatException nfe){
                    return new Failure(str + " cannot be read as a number.");
                }
            }else if(str.contains("Geo")){
                try{
                    double p;
                    if(hasOperators(params)){
                        EvalResult eval = evalArithmetic(params);
                        if(eval.isSuccess()){
                            p = eval.getValue((Object obj) -> (double) obj);
                        }else{
                            return eval;
                        }
                    }else{
                        p = Double.parseDouble(params);
                    }
                    if(p < 0.0 || p > 1.0){
                        return new Failure("Probability for distribution out of range!");
                    }
                    if(store.hasDistribution(arr[0])){
                        if(doOverwrite){
                            GeometricDistribution dist = new GeometricDistribution(p);
                            store.addDistribution(arr[0],dist);
                            return new Warning("Overwritten original value of " + arr[0] + ".",dist);
                        }else{
                            return new Failure("Input distribution with the name " + arr[0] + " already exists.");
                        }
                    }else{
                        GeometricDistribution dist = new GeometricDistribution(p);
                        store.addDistribution(arr[0], dist);
                        return new Success("Successfully added " + arr[0] + ".", dist);
                    }
                }catch(NumberFormatException nfe){
                    return new Failure(str + " cannot be read as a number.");
                }
            }else if(str.contains("B")){
                try{
                    String[] parameters = str.split(",");
                    if(parameters.length != 2){
                        return new Incomplete("Insufficient parameters for binomial distribution. Provided: " + parameters);
                    }
                    double p;
                    if(hasOperators(arr[1])){
                        EvalResult eval = evalArithmetic(arr[1]);
                        if(eval.isSuccess()){
                            p = eval.getValue((Object obj) -> (double) obj);
                        }else{
                            return eval;
                        }
                    }else{
                        p = Double.parseDouble(arr[1]);
                    }
                    int trials;
                    if(hasOperators(arr[0])){
                        EvalResult eval = evalArithmetic(params);
                        if(eval.isSuccess()){
                            //This is a round *down*, take note of that
                            trials = eval.getValue((Object obj) -> (int) obj);
                        }else{
                            return eval;
                        }
                    }else{
                        trials = Integer.parseInt(arr[0]);
                    }
                    if(p < 0.0 || p > 1.0){
                        return new Failure("Probability for distribution out of range!");
                    }else if(trials < 1){
                        return new Failure("Too few trials!");
                    }
                    if(store.hasDistribution(arr[0])){
                        if(doOverwrite){
                            BinomialDistribution dist = new BinomialDistribution(p, trials);
                            store.addDistribution(arr[0],dist);
                            return new Warning("Overwritten original value of " + arr[0] + ".",dist);
                        }else{
                            return new Failure("Input distribution with the name " + arr[0] + " already exists.");
                        }
                    }else{
                        BinomialDistribution dist = new BinomialDistribution(p, trials);
                        store.addDistribution(arr[0], dist);
                        return new Success("Successfully added " + arr[0] + ".",dist);
                    }
                }catch(NumberFormatException nfe){
                    return new Failure(str + " cannot be read as a number.");
                }
            }else{
                return new Failure("Distribution " + str.substring(0,startPos) + " is not supported!");
            }
        }else if(toEval.contains("print")){
            String[] split = toEval.split(" ");
            StringBuilder build = new StringBuilder();
            boolean success = true;
            for(String s : Arrays.asList(split).subList(1,split.length)){
                if(store.hasDistribution(s)){
                    build.append(s + "~" + store.getDistribution(s).toString() + "\n");
                }else if(store.hasVariable(s)){
                    build.append(s + " = " + store.getVariable(s) + "\n");
                }else{
                    build.append("Unable to resolve variable or distribution " + s + "!\n");
                    success = false;
                }
            }
            if(success) {
                return new Success(build.toString(), build.toString());
            }else{
                return new Warning(build.append("Warning: One or more variables cannot be found!\n").toString(), build.toString());
            }
        }else if(toEval.contains("=")) {
            String[] split = toEval.split("=");
            String name = split[0].trim();
            if(store.hasVariable(name)){
                if(!Arrays.asList(split).contains("--overwrite")){
                    return new Failure("Variable " + name + " already exists!");
                }else{
                    EvalResult eval = evalArithmetic(split[1]);
                    if(eval.isSuccess()) {
                        return new Warning("Variable " + name + " has been overwritten!", eval.getValue(Function.identity()));
                    }else{
                        return eval;
                    }
                }
            }else {
                EvalResult eval = evalArithmetic(split[1]);
                if(eval.isSuccess()) {
                    Main.getStorage().addVariable(name,eval.getValue((Object obj) -> (double) obj));
                    return new Success("Variable " + name + " has been created!", eval.getValue(Function.identity()));
                }else{
                    return eval;
                }
            }
        }else if(toEval.equals("dump")) {
            return new Success(Main.getStorage().dump(),Main.getStorage().dump());
        }else{
            return new Failure("Operation not supported!");
        }
    }
    private boolean hasOperators(String toCheck){
        return toCheck.contains("+") || toCheck.contains("-") || toCheck.contains("*") || toCheck.contains("/") || toCheck.contains("^") || toCheck.contains("(");
    }
    public static EvalResult evalArithmetic(String calc){
        List<String> operators = new ArrayList<>();
        Storage store = Main.getStorage();
        //Good ole regex
        List<String> ops = Arrays.asList(calc.split("[+\\-*/<>(^]+(?![^(]*\\Q)\\E)")).stream().map(str -> str.trim()).collect(Collectors.toList());
        for(String s : ops){
            operators.add(s);
            int pt = calc.indexOf(s) + s.length();
            if(!(pt >= calc.length())){
                operators.add(String.valueOf(calc.charAt(pt)));
            }
        }
        //First pass, brackets
        for(String s : operators){
            if(SYM.matcher(s).results().count() > 1){
                //We are reasonably sure this is another, yet unevaluated snippet
                //As only those can be not split
                //Final test for brackets, then throw this back into this method
                int startPos = s.lastIndexOf("(");
                int closePos = s.lastIndexOf(")");
                if(startPos == -1){
                    return new Incomplete("Start bracket is missing.");
                }else if(closePos == -1){
                    return new Incomplete("End bracket is missing.");
                }else if(startPos > closePos){
                    return new Failure("Close bracket before start bracket: " + calc);
                }
                if(startPos != 0 || closePos != s.length() - 1){
                    return new Incomplete("Impossible: Brackets not at the start of the split expression? Expression: " + calc);
                }
                //strip off the brackets, then reevaluate
                EvalResult eval = evalArithmetic(s.substring(1, s.length() - 2));
                if(eval.isSuccess()){
                    int index = operators.indexOf(s);
                    operators.remove(index + 1);
                    operators.remove(index);
                    operators.remove(index - 1);
                    operators.add(index, String.valueOf(eval.getValue((Object obj) -> {
                        //Cast, we know the answer is double, we stored it there!
                        return (double) obj;
                    })));
                }else{
                    return eval;
                }
            }
        }
        //1.5th pass, look for expressions that can be replaced
        for(String s : operators){
            if(s.startsWith("P(")){
                EvalResult eval = evalProbability(s);
                if(eval.isSuccess()){
                    int index = operators.indexOf(s);
                    operators.set(index,eval.getValue((Object obj) -> String.valueOf((double) obj)));
                }else{
                    return eval;
                }
            }
        }
        //Second pass, look for ^
        for(String s : operators){
            if(s.equals("^")){
                if(operators.indexOf(s) == 0 || operators.indexOf(s) == operators.size() - 1){
                    return new Incomplete("Exponent operator incomplete, missing either exponent or base. Expression:" + calc);
                }else{
                    try{
                        int index = operators.indexOf(s);
                        String base = operators.get(index - 1);
                        String exp = operators.get(index + 1);
                        double aBase;
                        double aExp;
                        if(needsSubstitution(base)){
                            if(!store.hasVariable(base)){
                                return new Failure("Unable to find variable " + base);
                            }else{
                                aBase = store.getVariable(base);
                            }
                        }else{
                            aBase = Double.parseDouble(base);
                        }
                        if (needsSubstitution(exp)) {
                            if(!store.hasVariable(exp)){
                                return new Failure("Unable to find variable " + base);
                            }else{
                                aExp = store.getVariable(exp);
                            }
                        }else{
                            aExp = Double.parseDouble(exp);
                        }
                        operators.remove(index + 1);
                        operators.remove(index);
                        operators.remove(index - 1);
                        operators.add(index, String.valueOf(Math.pow(aBase,aExp)));
                    }catch(NumberFormatException nfe){
                        return new Failure("Unable to parse number. Expression: " + calc);
                    }
                }
            }
        }
        //Third pass, look for * and /
        for(String s : operators){
            if(s.equals("*") || s.equals("/")){
                int index = operators.indexOf(s);
                if(index == 0 || index == operators.size() - 1){
                    return new Incomplete("Multiplication or division incomplete, missing an argument. Expression: " + calc);
                }else{
                    try{
                        String base = operators.get(index - 1);
                        String exp = operators.get(index + 1);
                        double aBase;
                        double aExp;
                        if(needsSubstitution(base)){
                            if(!store.hasVariable(base)){
                                return new Failure("Unable to find variable " + base);
                            }else{
                                aBase = store.getVariable(base);
                            }
                        }else{
                            aBase = Double.parseDouble(base);
                        }
                        if (needsSubstitution(exp)) {
                            if(!store.hasVariable(exp)){
                                return new Failure("Unable to find variable " + base);
                            }else{
                                aExp = store.getVariable(exp);
                            }
                        }else{
                            aExp = Double.parseDouble(exp);
                        }
                        operators.remove(index + 1);
                        operators.remove(index);
                        operators.remove(index - 1);
                        operators.add(index, String.valueOf(s.equals("*") ? aBase * aExp : aBase / aExp));
                    }catch(NumberFormatException nfe){
                        return new Failure("Unable to parse number. Expression: " + calc);
                    }
                }
            }
        }
        //Final pass, addition and subtraction
        for(String s : operators){
            if(s.equals("+") || s.equals("-")){
                int index = operators.indexOf(s);
                if(index == 0 || index == operators.size() - 1){
                    return new Incomplete("Addition or subtraction incomplete, missing an argument. Expression: " + calc);
                }else{
                    try{
                        String base = operators.get(index - 1);
                        String exp = operators.get(index + 1);
                        double aBase;
                        double aExp;
                        if(needsSubstitution(base)){
                            if(!store.hasVariable(base)){
                                return new Failure("Unable to find variable " + base);
                            }else{
                                aBase = store.getVariable(base);
                            }
                        }else{
                            aBase = Double.parseDouble(base);
                        }
                        if (needsSubstitution(exp)) {
                            if(!store.hasVariable(exp)){
                                return new Failure("Unable to find variable " + base);
                            }else{
                                aExp = store.getVariable(exp);
                            }
                        }else{
                            aExp = Double.parseDouble(exp);
                        }
                        operators.remove(index + 1);
                        operators.remove(index);
                        operators.remove(index - 1);
                        operators.add(index, String.valueOf(s.equals("+") ? aBase + aExp : aBase - aExp));
                    }catch(NumberFormatException nfe){
                        return new Failure("Unable to parse number. Expression: " + calc);
                    }
                }
            }
        }
        if(operators.size() > 1){
            return new Failure("Impossible: After all evaluations fail to get a single answer? Operators remaining: " + operators);
        }else{
            return new Success("Successfully calculated " + calc + " with answer " + operators.get(0),Double.parseDouble(operators.get(0)));
        }
    }
    public static EvalResult evalProbability(String toEval){
        return null;
    }
    public static boolean needsSubstitution(String check){
        return check.chars().mapToObj(i -> (char) i).anyMatch(c -> !(c.equals(".") || Character.isDigit(c)));
    }
}
