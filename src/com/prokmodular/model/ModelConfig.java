package com.prokmodular.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelConfig {

    final Logger logger = LoggerFactory.getLogger(ModelConfig.class);

    public String filename = "";
    private String hello = "";
    private int version = 0;

    public ModelConfig(String hello, String filename) {
        this.hello = hello;
        this.filename = filename;
    }

    public void setName(String name) {
        hello = name;
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
        if(config.version > version) {
            logger.debug("Versions not compatible. Ours is " + version + " and other is " + config.version);
            return false;
        }

        return true;
    }

    public boolean isOlderThan(ModelConfig config) {
        return version < config.version;
    }


    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
