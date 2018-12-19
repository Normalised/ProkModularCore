package com.prokmodular.model;

public class ModelConfig {
    public String filename = "";
    public String hello = "";
    public int version = 0;

    public ModelConfig(String hello, String filename) {
        this.hello = hello;
        this.filename = filename;
    }

    public String getName() {
        return hello;
    }

    /**
     * Version compatibility check assumes that 'this' is the destination
     * For x.isCompatibleWith(y) to be true, x.version must be greater than or equal to y.version
     *
     * @param config
     * @return
     */
    public boolean isCompatibleWith(ModelConfig config) {
        if(!hello.equalsIgnoreCase(config.hello)) {
            return false;
        }
        if(config.version > version) return false;

        return true;
    }
}
