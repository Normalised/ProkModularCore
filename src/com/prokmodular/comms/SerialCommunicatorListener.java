package com.prokmodular.comms;

public interface SerialCommunicatorListener {
    void serialConnected(String helloType);
    void serialDisconnected();
    void onData(String propName, String propValue);
}
