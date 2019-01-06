package com.prokmodular.model;

import com.prokmodular.comms.ParamMessage;

public interface ModelParamListener {
    void setModelSize(int numParams);
    void setCurrentParam(ParamMessage param);
    void setParam(int modelIndex, ParamMessage param);
}
