package com.prokmodular.model;

public class ParamDefinition {

    public int index = -1;
    public String name = "";
    public float defaultValue = 1.0f;

    public ParamDefinition(String name, float defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public ParamDefinition(int index, String name, float defaultVal) {
        this.index = index;
        this.name = name;
        this.defaultValue = defaultVal;
    }
}
