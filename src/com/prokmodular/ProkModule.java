package com.prokmodular;

import com.prokmodular.comms.*;
import com.prokmodular.files.ModelExporter;
import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ModelParamListener;
import com.prokmodular.model.ModelUI;
import com.prokmodular.model.ProkModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.prokmodular.comms.Commands.*;

public class ProkModule {

    final Logger logger = LoggerFactory.getLogger(ProkModule.class);

    private ModuleSerialConnection connection;
    public ProkModel model;
    public ModelUI ui;

    private int firmwareVersion = 0;
    public String type;

    public int getSize() {
        return Integer.parseInt(connection.getDataValue(Messages.PARAM_SIZE));
    }

    public boolean hasSD() {
        return connection.getDataValue(Messages.HAS_SD_CARD).equalsIgnoreCase("1");
    }

    public int getVersion() {
        return model.getConfig().getVersion();
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
        logger.debug("Set Connection " + prokModel.getConfig().getName() + " : " + connectionToUse.getPortName() + " : " + connectionToUse.getHandshakeStatus().toString());
        connection = connectionToUse;
        model = prokModel;
        ModelConfig config = model.getConfig();
        config.setVersion(Integer.parseInt(connection.getDataValue(Messages.VERSION)));
        firmwareVersion = Integer.parseInt(connection.getDataValue(Messages.FIRMWARE_VERSION));
    }

    public boolean isConnected() {
        return connection.getHandshakeStatus() == ModuleSerialConnection.HandshakeStatus.OK;
    }

    public String getConnectionKey() {
        return type + ":" + connection.getPortName();
    }

    public void clearQuad(int index) {
        connection.sendCommand(new CommandContents(Commands.CLEAR_QUAD, String.valueOf(index)));
    }

    public void selectModel(int index) {
        connection.sendCommand(new CommandContents(Commands.SELECT_MODEL, String.valueOf(index)));
    }

    public void saveModel(int index) {
        connection.sendCommand(new CommandContents(Commands.SAVE, String.valueOf(index)));
    }

    public void getCurrentParams() {
        connection.sendCommand(new CommandContents(Commands.SEND_PARAMS, Commands.INDEX_FOR_CURRENT_MODEL));
    }

    public void getCurrentConfig() {
        connection.sendCommand(new CommandContents(Commands.SEND_CONFIG));
    }
    public void trigger() {
        connection.sendCommandWithNoData(Commands.TRIGGER);
    }

    public void clearPatches() {
        connection.sendCommand(new CommandContents(CLEAR));
    }

    public void ignoreCV(boolean ignore) {
        connection.sendCommand(new CommandContents(Commands.EXCLUSIVE, ignore ? "1" : "0"));
    }

    public void morphX(float morphX) {
        connection.sendCommand(new CommandContents(MORPH_X, Float.toString(morphX)));
    }

    public void morphY(float morphY) {
        connection.sendCommand(new CommandContents(MORPH_Y, Float.toString(morphY)));
    }

    public void setParam(ParamMessage paramMessage) {
        connection.sendCurrentParam(paramMessage);
    }

    public void sendParams(int index) {
        connection.sendCommand(new CommandContents(Commands.SEND_PARAMS, String.valueOf(index)));
    }

    public void removeParamListener(ModelParamListener listener) {
        if(connection != null) {
            connection.removeModelParamListener(listener);
        }
    }

    public void addParamListener(ModelParamListener listener) {
        if(connection != null) {
            connection.addModelParamListener(listener);
        }
    }

    public void removeCommandListener(ModuleCommandListener listener) {
        connection.removeModuleCommandListener(listener);
    }

    public void addCommandListener(ModuleCommandListener listener) {
        connection.addModuleCommandListener(listener);
    }

    public void selectQuad(int q) {
        logger.debug("Selected Quad " + q + " : " + connection.getDataValue(Messages.QUAD_STATE) + " : " + connection.getDataValue(Messages.QUAD_SELECT_INDEX));

    }

    public void readEeprom(int index) {
        connection.sendCommand(new CommandContents(EEPROM_READ, index));
    }

    public void quickOpenSD() {
        logger.debug("Quick Open SD");
        connection.sendCommandWithNoData(Commands.QUICK_OPEN_SD);
    }

    public void reboot() {
        connection.sendCommandWithNoData(Commands.REBOOT);
    }

    public void alwaysCheckSD(boolean on) {
        connection.sendCommand(new CommandContents(Commands.ALWAYS_CHECK_SD, on ? "1" : "0"));
    }
}
