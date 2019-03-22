package com.prokmodular.comms;

import com.prokmodular.model.ModelParamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class ModuleSerialConnection {

    final Logger logger = LoggerFactory.getLogger(ModuleSerialConnection.class);
    private static final char COMMAND_START = '!';
    private static final char PARAM_START = '@';
    private static final char LOG_START = '#';
    private final Timer updateTimer;

    private HashMap<String, String> dataCache;

    public enum HandshakeStatus {
        WAITING, UNKNOWN_MODEL, FIRMWARE_MISMATCH, MODEL_MISMATCH, OK
    }

    public enum HandshakeState {
        HELLO, NAME, FIRMWARE_VERSION, MODEL_VERSION, SD_CHECK, PARAM_COUNT, CURRENT_PARAMS, QUAD_STATE, CURRENT_QUAD, COMPLETE
    }

    private HandshakeStatus handshakeStatus = HandshakeStatus.WAITING;
    // Make it a stack or something.
    private Stack<String> nextHandshakeMessage;
    //private List<String> nextHandshakeMessage;

    private static final int MAX_KEEP_ALIVE_FAIL_COUNT = 10;

    private List<ModuleConnectionListener> listeners;
    private List<ModuleCommandListener> commandListeners;
    private List<ModelParamListener> modelParamListeners;

    private List<HandshakeStatusListener> handshakeStatusListeners;

    private Timer keepAliveTimer;
    private int keepAliveCount = 0;

    enum ReceiveState {
        IDLE,
        RECEIVING_LOG, RECEIVING_COMMAND, RECEIVING_PARAM,
    }

    private ReceiveState receiveState = ReceiveState.IDLE;
    private Serial modulePort;
    private StringBuffer incomingDataBuffer;

    private CommandContents keepAliveCommand = new CommandContents(Commands.KEEP_ALIVE, "");

    public ModuleSerialConnection(Serial port) {
        handshakeStatusListeners = new ArrayList<>();

        nextHandshakeMessage = new Stack<>();
        nextHandshakeMessage.push(Messages.QUAD_SELECT_INDEX);
        nextHandshakeMessage.push(Messages.QUAD_STATE);
        nextHandshakeMessage.push(Messages.PARAM_SIZE);
        nextHandshakeMessage.push(Messages.HAS_SD_CARD);
        nextHandshakeMessage.push(Messages.VERSION);
        nextHandshakeMessage.push(Messages.FIRMWARE_VERSION);
        //nextHandshakeMessage.push(Messages.NAME);
        nextHandshakeMessage.push(Messages.HELLO_RESPONSE);

        dataCache = new HashMap<>();
        listeners = new ArrayList<>();
        modelParamListeners = new ArrayList<>();
        commandListeners = new ArrayList<>();

        incomingDataBuffer = new StringBuffer();
        modulePort = port;//new Serial(portName, 19200);
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 5, 5);
    }

    public String getPortName() {
        return modulePort.port.getPortName();
    }

    public void addHandshakeStatusListener(HandshakeStatusListener listener) {
        handshakeStatusListeners.add(listener);
    }

    public void removeHandshakeStatusListener(HandshakeStatusListener listener) {
        handshakeStatusListeners.remove(listener);
    }

    public HandshakeStatus getHandshakeStatus() {
        return handshakeStatus;
    }

    public String getDataValue(String name) {
        return dataCache.get(name);
    }

    private void startKeepAlive() {
        keepAliveTimer = new Timer();
        keepAliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendCommand(keepAliveCommand);
                keepAliveCount++;
                if (keepAliveCount > MAX_KEEP_ALIVE_FAIL_COUNT) {
                    logger.debug("Max Keep alives missed. Disconnected");
                    keepAliveTimer.cancel();
                    sendDisconnect();
                    handshakeStatus = HandshakeStatus.WAITING;
                }
            }
        }, 1500, 1500);
    }

    public void close() {
        logger.debug("Closing port. Cancel update timer");
        updateTimer.cancel();
    }

    public void update() {
        if (modulePort != null) {
            boolean clearBuffer = false;
            boolean appendData = false;

            while (modulePort.available() > 0) {
                char next = modulePort.readChar();
                if (receiveState == ReceiveState.IDLE) {
                    if (next == COMMAND_START) {
                        receiveState = ReceiveState.RECEIVING_COMMAND;
                        clearBuffer = true;
                    } else if (next == LOG_START) {
                        receiveState = ReceiveState.RECEIVING_LOG;
                        clearBuffer = true;
                    } else if (next == 10) {
                        //logger.debug("Received " + serialBuffer);
                    } else if (next == PARAM_START) {
                        receiveState = ReceiveState.RECEIVING_PARAM;
                        clearBuffer = true;
                    } else {
                        appendData = true;
                    }
                } else if (receiveState == ReceiveState.RECEIVING_COMMAND) {
                    if (next == 10) {
                        //processSerial(incomingDataBuffer);
                        processCommand(incomingDataBuffer);
                        clearBuffer = true;
                        receiveState = ReceiveState.IDLE;
                    } else {
                        appendData = true;
                    }
                } else if (receiveState == ReceiveState.RECEIVING_LOG) {
                    if (next == 10) {
                        processLog(incomingDataBuffer);
                        clearBuffer = true;
                        receiveState = ReceiveState.IDLE;
                    } else {
                        appendData = true;
                    }
                } else if (receiveState == ReceiveState.RECEIVING_PARAM) {
                    if (next == 10) {
                        processParam(incomingDataBuffer);
                        clearBuffer = true;
                        receiveState = ReceiveState.IDLE;
                    } else {
                        appendData = true;
                    }
                }
                if (clearBuffer) {
                    //logger.debug("Clear buffer : " + incomingDataBuffer);
                    incomingDataBuffer = new StringBuffer();
                    clearBuffer = false;
                } else if (appendData) {
                    incomingDataBuffer.append(next);
                    appendData = false;
                }
            }
        }
    }

    private void processLog(StringBuffer incomingDataBuffer) {
        logger.debug("ModuleLog::" + incomingDataBuffer.toString());
    }

    private CommandContents parseCommand(String message) throws Exception {
        String[] parts;
        if (message.contains(" ")) {
            parts = message.split(" ");
        } else {
            parts = message.split(":");
        }

        if (parts.length == 2) {
            String commandName = parts[0].toLowerCase();
            String value = parts[1];
            return new CommandContents(commandName, value);
        }
        throw new Exception("Bad command format : " + message);
    }

    private void sendDisconnect() {
        List<ModuleConnectionListener> tempListeners = new ArrayList<>(listeners);

        for (ModuleConnectionListener l : tempListeners) {
            l.disconnected();
        }
    }


    // param format : @MODEL_INDEX:PARAM_ID=PARAM_VALUE
    // PARAM_ID : Int
    // PARAM_VALUE : Float
    private void processParam(StringBuffer serialBuffer) {
        String paramData = serialBuffer.toString();
        String[] modelAndParam = paramData.split(":");
        int modelIndex = parseInt(modelAndParam[0]);
        String[] parts = modelAndParam[1].split("=");
        if (parts.length == 2) {
            int paramID = parseInt(parts[0]);
            float paramValue = parseFloat(parts[1]);

            ParamMessage param = new ParamMessage(paramID, paramValue);
            if (modelIndex == 100) {
                for (ModelParamListener mpl : modelParamListeners) {
                    mpl.setCurrentParam(param);
                }
            } else {
                for (ModelParamListener mpl : modelParamListeners) {
                    mpl.setParam(modelIndex, param);
                }
            }

        }
    }

    private void processCommand(StringBuffer serialBuffer) {
        String message = serialBuffer.toString().trim();
        CommandContents command = null;
        try {
            command = parseCommand(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (command == null) return;

        //logger.debug("Process command " + command.name + " : " + command.data);
        if (handshakeStatus != HandshakeStatus.OK) {
            processHandshake(command);
        } else {

            if (command.name.equals(Messages.KEEP_ALIVE)) {
                keepAliveCount--;
            } else if (command.name.equals("x") || command.name.equals("y")) {
                logger.debug("Morph " + command.name + " -> " + command.data);
            }

            if (command.is(Commands.SIZE)) {
                logger.debug("Size command " + command.data + ". Listeners " + modelParamListeners.size());
                for (ModelParamListener mpl : modelParamListeners) {
                    mpl.setModelSize(Integer.parseInt(command.data));
                }
            }
            for (ModuleCommandListener l : commandListeners) {
                l.onCommand(command);
            }
        }
        // TODO : Keep a timestamped history of commands to replay and simulate a module.
        dataCache.put(command.name, command.data);
    }

    // HELLO(NAME), FIRMWARE_VERSION, MODEL_VERSION, SD_CHECK,
    // PARAM_COUNT, CURRENT_PARAMS, QUAD_STATE, CURRENT_QUAD, COMPLETE
    private void processHandshake(CommandContents command) {

        if (command.is(nextHandshakeMessage.peek())) {
            logger.debug("Got handshake message " + command.name + " : " + command.data);
            nextHandshakeMessage.pop();
            if (command.name.equals(Messages.HELLO_RESPONSE)) {
                //logger.debug("Got hello, module name is " + command.data);
                dataCache.put(Messages.NAME, command.data);
            } else {
                dataCache.put(command.name, command.data);
            }
        }

        if (nextHandshakeMessage.isEmpty()) {
            handshakeStatus = HandshakeStatus.OK;
            startKeepAlive();
            sendHandshakeComplete();
        }
    }

    private void sendHandshakeComplete() {
        List<HandshakeStatusListener> listeners = new ArrayList<>(handshakeStatusListeners);
        for (HandshakeStatusListener l : listeners) {
            l.onHandshakeComplete(this);
        }
    }

    public void sendCurrentParam(ParamMessage param) {
        if (modulePort != null) {
//            logger.debug("Send param " + param.id + " : " + param.value);
            modulePort.write(PARAM_START + Commands.INDEX_FOR_CURRENT_MODEL + " " + param.id + ":" + param.value + "\n");
        }
    }

    public void writeEeprom(int index, int data) {
        if(data < 0) data = 0;
        if(data > 255) data = 255;
        modulePort.write(PARAM_START + Commands.EEPROM_WRITE_INDEX + " " + index + ":" + data + "\n");
    }

    public void sendCommandWithNoData(String commandName) {
        if (modulePort != null) {
            try {
                modulePort.write(COMMAND_START + commandName + "\n");
            } catch (Exception e) {
                logger.debug("Failed to write to port : " + e.getMessage());
            }
        } else {
            logger.debug("Not sending " + commandName + ". No port connected.");
        }

    }

    public void sendCommand(CommandContents command) {
        if (modulePort != null) {
            try {
                //logger.debug("Send Command " + command.name + " " + command.data);
                modulePort.write(COMMAND_START + command.name + " " + command.data + "\n");
            } catch (Exception e) {
                logger.debug("Failed to write to port : " + e.getMessage());
            }
        } else {
            logger.debug("Not sending " + command.name + ". No port connected.");
        }
    }

    public void addModelParamListener(ModelParamListener modelParamListener) {
        if (!modelParamListeners.contains(modelParamListener)) {
            modelParamListeners.add(modelParamListener);
        }
    }

    public void removeModelParamListener(ModelParamListener modelParamListener) {
        if (modelParamListeners.contains(modelParamListener)) {
            modelParamListeners.remove(modelParamListener);
        }
    }

    public void addModuleCommandListener(ModuleCommandListener l) {
        if (!commandListeners.contains(l)) {
            commandListeners.add(l);
        }
    }

    public void removeModuleCommandListener(ModuleCommandListener l) {
        if (commandListeners.contains(l)) {
            commandListeners.remove(l);
        }
    }

    public void addSerialCommsListener(ModuleConnectionListener moduleConnectionListener) {
        if (!listeners.contains(moduleConnectionListener)) {
            listeners.add(moduleConnectionListener);
        }
    }

    public void removeSerialCommsListener(ModuleConnectionListener moduleConnectionListener) {
        if (listeners.contains(moduleConnectionListener)) {
            listeners.remove(moduleConnectionListener);
        }
    }

}
