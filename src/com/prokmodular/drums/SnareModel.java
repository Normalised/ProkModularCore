package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ProkModel;
import com.prokmodular.ui.ModelUIBuilder;

public class SnareModel implements ProkModel {

    private ModelConfig config = new ModelConfig("snare","snare");

    @Override
    public ModelConfig getConfig() {
        return config;
    }

}
