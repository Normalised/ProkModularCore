package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ProkModel;

import java.util.ArrayList;
import java.util.List;

public class HiHatModel implements ProkModel {

    public HiHatModel() {
    }

    @Override
    public ModelConfig getConfig() {
        ModelConfig config = new ModelConfig();

        config.filename = "hihat";
        config.hello = "hihat";
        config.version = 5;

        return config;
    }

}
