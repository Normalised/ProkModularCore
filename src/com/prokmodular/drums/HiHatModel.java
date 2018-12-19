package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ProkModel;

import java.util.ArrayList;
import java.util.List;

public class HiHatModel implements ProkModel {

    private ModelConfig config = new ModelConfig("hihat","hihat");

    @Override
    public ModelConfig getConfig() {
        return config;
    }

}
