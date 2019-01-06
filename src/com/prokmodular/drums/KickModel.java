package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ModelUI;
import com.prokmodular.model.ProkModel;
import com.prokmodular.ui.ModelUIBuilder;

public class KickModel implements ProkModel {
    private ModelConfig config = new ModelConfig("kick","kick_");

    @Override
    public ModelConfig getConfig() {
        return config;
    }

}
