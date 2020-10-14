package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ProkModel;

public class KlonkModel implements ProkModel {
    private ModelConfig config = new ModelConfig("klonk","klonk");

    @Override
    public ModelConfig getConfig() {
        return config;
    }

}
