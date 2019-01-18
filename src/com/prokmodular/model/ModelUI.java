package com.prokmodular.model;

import com.prokmodular.ui.ModelUIBuilder;

public interface ModelUI {
    void createUI(ModelUIBuilder ui, int firmwareVersion, int modelVersion);
}
