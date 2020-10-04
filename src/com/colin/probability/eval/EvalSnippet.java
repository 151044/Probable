package com.colin.probability.eval;

import com.colin.probability.Main;
import com.colin.probability.Storage;
import com.colin.probability.dists.BinomialDistribution;
import com.colin.probability.dists.GeometricDistribution;
import com.colin.probability.dists.PoissonDistribution;

import java.util.Arrays;

public class EvalSnippet {
    private String toEval;
    public EvalSnippet(String toEval){
        this.toEval = toEval;
    }
    public EvalResult eval(){
        Storage store = Main.getStorage();
        if(toEval.contains("~")){
            String[] arr = toEval.split("~");
            boolean doOverwrite = Arrays.asList(arr).contains("--overwrite");
            if(arr.length != 2 || (!doOverwrite && arr.length != 3)){
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
            String params = str.substring(startPos,closePos);
            if(str.contains("Po")){
                try{
                    double p = Double.parseDouble(params);
                    if(store.hasDistribution(arr[0])){
                        if(doOverwrite){
                            PoissonDistribution dist = new PoissonDistribution(p);
                            store.addDistribution(arr[0],dist);
                            return new Warning("Overwritten original value of " + arr[0] + ".",dist);
                        }else{
                            return new Failure("Input distribution with the name " + arr[0] + " already exists.");
                        }
                    }else{
                        PoissonDistribution dist = new PoissonDistribution(p);
                        store.addDistribution(arr[0], dist);
                        return new Success("Successfully added " + arr[0] + ".",dist);
                    }
                }catch(NumberFormatException nfe){
                    return new Failure(str + " cannot be read as a number.");
                }
            }else if(str.contains("Geo")){
                try{
                    double p = Double.parseDouble(params);
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
                    double p = Double.parseDouble(arr[1]);
                    int trials = Integer.parseInt(arr[0]);
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
                    build.append(s + "=" + store.getVariable(s) + "\n");
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
        }else{
            return new Failure("Operation not supported!");
        }
    }
}
