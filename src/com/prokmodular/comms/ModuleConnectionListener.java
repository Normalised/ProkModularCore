package com.prokmodular.comms;

public interface ModuleConnectionListener {
    void connected(String helloType);
    void disconnected();
    //void portsChecked(Map<String, List<Serial>> ports);
    void onData(String propName, String propValue);
}
