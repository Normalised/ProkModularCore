package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ProkModel;
import com.prokmodular.ui.ModelUIBuilder;

public class SnareModel implements ProkModel {

    public ModelConfig getConfig() {

        ModelConfig config = new ModelConfig();

        config.filename = "snare";
        config.hello = "snare";
        config.version = 1;

        return config;
    }

}
