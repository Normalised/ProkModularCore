package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ModelUI;
import com.prokmodular.model.ProkModel;
import com.prokmodular.ui.ModelUIBuilder;

import static com.prokmodular.model.ParameterMapping.createLinear;
import static com.prokmodular.model.ParameterMapping.createSquared;

public class ClapModel implements ProkModel {
    private ModelConfig config = new ModelConfig("clap","clap_");

    @Override
    public ModelConfig getConfig() {
        return config;
    }

}
