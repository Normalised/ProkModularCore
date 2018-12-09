package com.prokmodular.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatchDefinition {
    public int version;
    public List<ParamDefinition> params;

    // from name to param
    public HashMap<String, ParamDefinition> paramMap;

    // from index to param
    public HashMap<Integer, ParamDefinition> paramPositionMap;

    public PatchDefinition() {
        params = new ArrayList<>();
        paramMap = new HashMap<>();
        paramPositionMap = new HashMap<>();
    }

    public void addParam(ParamDefinition param) {
        params.add(param);
        paramMap.put(param.name, param);
        if(param.index != -1) {
            paramPositionMap.put(param.index, param);
        }
    }

    public Float getDefaultValueFor(String name) {
        return paramMap.get(name).defaultValue;
    }

    public ParamDefinition getParamFromIndex(int index) {
        return paramPositionMap.get(index);
    }

    public boolean hasParamNamed(String name) {
        return paramMap.containsKey(name);
    }

    public ParamDefinition getParamFromName(String name) {
        return paramMap.get(name);
    }
}
