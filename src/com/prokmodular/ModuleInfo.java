package com.prokmodular;

import com.prokmodular.comms.Serial;
import com.prokmodular.model.ModelUI;
import com.prokmodular.model.ProkModel;

public class ModuleInfo {

    public String getModelName() {
        return model.getConfig().getName();
    }

    public int getVersion() {
        return model.getConfig().version;
    }

    public String getFilename() {
        return model.getConfig().filename;
    }

    public Serial port;
    public String type;

    public boolean hasSD;
    public int numParams;
    public int quadState;
    public int currentQuad = 0;
    public int firmwareVersion;
    //public int modelVersion;

    public ProkModel model;
    public ModelUI ui;
}
