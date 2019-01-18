package com.prokmodular;

import com.prokmodular.comms.Messages;
import com.prokmodular.comms.ModuleSerialConnection;
import com.prokmodular.comms.Serial;
import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ModelUI;
import com.prokmodular.model.ProkModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleInfo {

    final Logger logger = LoggerFactory.getLogger(ModuleInfo.class);

    public ModuleSerialConnection connection;
    public String type;

    public boolean hasSD = false;
    private int firmwareVersion = 0;

    public ProkModel model;
    public ModelUI ui;

    public int getVersion() {
        return model.getConfig().version;
    }

    public String getFilename() {
        return model.getConfig().filename;
    }

    public String getProperty(String propName) {
        if(connection == null) return "No Connection";
//        logger.debug("Get Property " + propName + " : " + connection.getDataValue(propName));
        return connection.getDataValue(propName);
    }

    public int getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setConnection(ModuleSerialConnection connectionToUse, ProkModel prokModel) {
        connection = connectionToUse;
        model = prokModel;
        ModelConfig config = model.getConfig();
        config.version = Integer.parseInt(connection.getDataValue(Messages.VERSION));
        firmwareVersion = Integer.parseInt(connection.getDataValue(Messages.FIRMWARE_VERSION));

    }

    public boolean isConnected() {
        return connection.getHandshakeStatus() == ModuleSerialConnection.HandshakeStatus.OK;
    }

    public String getConnectionKey() {
        return type + ":" + connection.getPortName();
    }
}
