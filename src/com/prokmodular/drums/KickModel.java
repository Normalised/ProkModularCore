package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ModelUI;
import com.prokmodular.model.ProkModel;
import com.prokmodular.ui.ModelUIBuilder;

public class KickModel implements ProkModel {
    @Override
    public ModelConfig getConfig() {
        ModelConfig config = new ModelConfig();

        config.filename = "kick_";
        config.hello = "kick";
        config.version = 3;

        return config;
    }

}
