package com.prokmodular.model;

/**
 * Created by martin on 31/07/2017 at 19:58
 */
public interface ModelParamListener {
    void setModelSize(int numParams);
    void setCurrentParam(int paramID, float val);
    void setParam(int modelIndex, int paramID, float paramValue);
}
