package com.prokmodular.comms;

/**
 * Created by martin on 2018-12-21 at 18:49
 */
public interface HandshakeStatusListener {
    void onHandshakeComplete(ModuleSerialConnection module);
}
