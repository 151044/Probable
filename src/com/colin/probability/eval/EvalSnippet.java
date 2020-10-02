package com.colin.probability.eval;

import com.colin.probability.Main;
import com.colin.probability.Storage;
import com.colin.probability.dists.BinomialDistribution;
import com.colin.probability.dists.GeometricDistribution;
import com.colin.probability.dists.PoissonDistribution;

import java.nio.file.FileAlreadyExistsException;
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
            }
            String params = str.substring(startPos,closePos);
            if(str.contains("Po")){
                try{
                    double p = Double.parseDouble(params);
                    if(store.hasDistribution(arr[0])){
                        if(doOverwrite){
                            store.addDistribution(arr[0],new PoissonDistribution(p));
                            return new Warning("Overwritten original value of " + arr[0] + ".");
                        }else{
                            return new Failure("Input distribution with the name " + arr[0] + " already exists.");
                        }
                    }else{
                        store.addDistribution(arr[0], new PoissonDistribution(p));
                        return new Success("Successfully added " + arr[0] + ".");
                    }
                }catch(NumberFormatException nfe){
                    return new Failure(str + " cannot be read as a number.");
                }
            }else if(str.contains("Geo")){
                try{
                    double p = Double.parseDouble(params);
                    if(store.hasDistribution(arr[0])){
                        if(doOverwrite){
                            store.addDistribution(arr[0],new GeometricDistribution(p));
                            return new Warning("Overwritten original value of " + arr[0] + ".");
                        }else{
                            return new Failure("Input distribution with the name " + arr[0] + " already exists.");
                        }
                    }else{
                        store.addDistribution(arr[0], new GeometricDistribution(p));
                        return new Success("Successfully added " + arr[0] + ".");
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
                            store.addDistribution(arr[0],new BinomialDistribution(p,trials));
                            return new Warning("Overwritten original value of " + arr[0] + ".");
                        }else{
                            return new Failure("Input distribution with the name " + arr[0] + " already exists.");
                        }
                    }else{
                        store.addDistribution(arr[0], new BinomialDistribution(p,trials));
                        return new Success("Successfully added " + arr[0] + ".");
                    }
                }catch(NumberFormatException nfe){
                    return new Failure(str + " cannot be read as a number.");
                }
            }else{
                return new Failure("Distribution " + str.substring(0,startPos) + " is not supported!");
            }
        }else if(toEval.contains("print")){
            return new Success("");
        }else{
            return new Failure("Operation not supported!");
        }
    }
}
