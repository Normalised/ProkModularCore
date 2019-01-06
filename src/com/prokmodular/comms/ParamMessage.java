package com.prokmodular.comms;

/**
 * Created by martin on 2018-12-21 at 18:11
 */
public class ParamMessage {
    public int id = -1;
    public float value = 0f;

    public ParamMessage(int paramID, float paramValue) {
        id = paramID;
        value = paramValue;
    }
}
