package com.prokmodular.comms;

public interface ModuleConnectionListener {
    void connected(String helloType);
    void disconnected();
}
