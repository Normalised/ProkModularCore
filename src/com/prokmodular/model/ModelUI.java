package com.prokmodular.model;

import com.prokmodular.ui.ModelUIBuilder;
import controlP5.ControlP5;

public interface ModelUI {
    void createUI(ModelUIBuilder ui, int firmwareVersion, int modelVersion);
    void createExtraUI(ControlP5 cp5);
    void removeExtraUI(ControlP5 cp5);
}
