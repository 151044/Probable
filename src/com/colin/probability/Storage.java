package com.colin.probability;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private Map<String,Distribution> dist = new HashMap<>();
    private Map<String,Double> vars = new HashMap<>();
    public void addDistribution(String repr, Distribution dist){
        this.dist.put(repr, dist);
    }
    public void addVariable(String name, double toAdd){
        vars.put(name,toAdd);
    }
    public double getVariable(String varName){
        return vars.get(varName);
    }
    public boolean hasVariable(String varName){
        return vars.containsKey(varName);
    }
    public Distribution getDistribution(String repr){
        return dist.get(repr);
    }
    public boolean hasDistribution(String repr){
        return dist.containsKey(repr);
    }
    public String dump(){
        return dist.toString() + " " + vars.toString();
    }
}
