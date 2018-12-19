package com.prokmodular.model;

import java.util.ArrayList;
import java.util.List;

public class Preset {
    public ModelConfig config;
    public List<Float> params;

    public Preset(ModelConfig config, List<Float> params) {
        this.config = config;
        this.params = params;
    }

    public Preset() {
        params = new ArrayList<>();
        config = new ModelConfig("","");
    }
}
