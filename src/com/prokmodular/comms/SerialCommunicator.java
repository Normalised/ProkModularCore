package com.prokmodular.comms;

import com.prokmodular.model.ModelParamListener;

import java.util.*;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

/**
 * Created by martin on 10/06/2017 at 16:18
 */
public class SerialCommunicator {

    private static final char COMMAND_START = '!';
    private static final char PARAM_START = '@';
    private static final char LOG_START = '#';

    private static final String HELLO = "zdravo";
    private static final String keepAliveCommand = "ping";
    private static final String keepAliveResponse = "pong";
    private static final int MAX_KEEP_ALIVE_FAIL_COUNT = 10;

    private final Map<String, String> data;

    private static final String helloResponse = "hello:";

    private List<SerialCommunicatorListener> listeners;
    private List<ModelParamListener> modelParamListeners;

    private Timer portTestTimer;

    private Timer keepAliveTimer;

    private int keepAliveCount = 0;
    private Timer connectTimer;
    private String[] portNames;

    private Timer updateTimer;

    enum State {
        IDLE,
        RECEIVING_LOG, RECEIVING_COMMAND, RECEIVING_PARAM,
    }

    // Connected to the module, not to any serial port
    public boolean connected = false;

    private State state = State.IDLE;
    private Serial modulePort;
    private int currentPortIndex = 0;
    private String serialBuffer = "";

    public SerialCommunicator(Map<String, String> commandValues) {
        this.data = commandValues;
        listeners = new ArrayList<>();
        modelParamListeners = new ArrayList<>();
    }

    public void init() {
        // List all the available serial ports:

        portNames = Serial.list();

        // Open the port you are using at the rate you want:
        if (portNames.length > 0) {
            currentPortIndex = 0;
            updateTimer = new Timer();
            updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            }, 20);
            testPort();
        } else {
            System.out.println("No serial ports found.. waiting");
            connectTimer = new Timer();
            connectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    init();
                }
            }, 1000);
        }
    }

    public void close() {
        updateTimer.cancel();
    }

    private void portConnected(String helloType) {
        portTestTimer.cancel();
        // we've found the right serial port
        System.out.println("Serial port found : " + helloType);
        connected = true;

        keepAliveTimer = new Timer();
        keepAliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendCommand(keepAliveCommand, "");
                //println("Sending keep alive");
                keepAliveCount++;
                if (keepAliveCount > MAX_KEEP_ALIVE_FAIL_COUNT) {
                    System.out.println("Max Keep alives missed. Disconnected");
                    keepAliveTimer.cancel();
                    connected = false;
                    sendDisconnect();
                }
            }
        }, 1500, 1500);
        data.put("name", helloResponse);

        // Create a copy because other components might try and add listeners whilst we're iterating.
        List<SerialCommunicatorListener> tempListeners = new ArrayList<>(listeners);

        for (SerialCommunicatorListener l : tempListeners) {
            l.serialConnected(helloType);
        }
    }

    public void update() {
        if (modulePort != null) {

            while (modulePort.available() > 0) {
                char next = modulePort.readChar();
                if (state == State.IDLE) {
                    if (next == COMMAND_START) {
                        state = State.RECEIVING_COMMAND;
                        serialBuffer = "";
                    } else if (next == LOG_START) {
                        state = State.RECEIVING_LOG;
                        serialBuffer = "";
                    } else if (next == 10) {
                        System.out.println("Received " + serialBuffer);
                    } else if (next == PARAM_START) {
                        state = State.RECEIVING_PARAM;
                        serialBuffer = "";
                    } else {
                        serialBuffer += next;
                    }
                } else if (state == State.RECEIVING_COMMAND || state == State.RECEIVING_LOG) {
                    if (next == 10) {
                        processSerial(serialBuffer);
                        serialBuffer = "";
                        state = State.IDLE;
                    } else {
                        serialBuffer += next;
                    }
                } else if (state == State.RECEIVING_PARAM) {
                    if (next == 10) {
                        processParam(serialBuffer);
                        serialBuffer = "";
                        state = State.IDLE;
                    } else {
                        serialBuffer += next;
                    }
                }
            }
        }
    }

    private void processSerial(String serialBuffer) {
        serialBuffer = serialBuffer.trim();

        //System.out.println("Process serial:" + serialBuffer + ":" + state);

        if (!connected && serialBuffer.startsWith(helloResponse)) {
            String helloType = serialBuffer.substring(helloResponse.length());
            portConnected(helloType);
            return;
        }

        if (state == State.RECEIVING_COMMAND) {
            String[] parts = serialBuffer.split(" ");
            if (parts.length == 2) {
                String commandName = parts[0].toLowerCase();
                String value = parts[1];
                processCommand(commandName, value);
            }
        } else if (state == State.RECEIVING_LOG) {
            if (!serialBuffer.contains("ping")) {
                System.out.println("LOG: " + serialBuffer);
            }

        }
    }

    private void sendDisconnect() {
        List<SerialCommunicatorListener> tempListeners = new ArrayList<>(listeners);

        for (SerialCommunicatorListener l : tempListeners) {
            l.serialDisconnected();
        }
    }


    // param format : @MODEL_INDEX:PARAM_ID=PARAM_VALUE
    // PARAM_ID : Int
    // PARAM_VALUE : Float
    private void processParam(String serialBuffer) {
        //println("ProcessParam " + serialBuffer);
        String[] modelAndParam = serialBuffer.split(":");
        int modelIndex = parseInt(modelAndParam[0]);
        String[] parts = modelAndParam[1].split("=");
        if (parts.length == 2) {
            int paramID = parseInt(parts[0]);
            float paramValue = parseFloat(parts[1]);

            // TODO : Split this into a param update interface that some part of the app implements
            if (modelIndex == 100) {
                for (ModelParamListener mpl : modelParamListeners) {
                    mpl.setCurrentParam(paramID, paramValue);
                }
            } else {
                for (ModelParamListener mpl : modelParamListeners) {
                    mpl.setParam(modelIndex, paramID, paramValue);
                }
            }

        }
    }

    private void processCommand(String commandName, String value) {

        if (commandName.equals(keepAliveResponse)) {
            keepAliveCount--;
        } else if (commandName.equals("x") || commandName.equals("y")) {
            System.out.println("Process command: " + commandName + " -> " + value);
        }

        data.put(commandName, value);

        if (commandName.equals(Commands.SIZE)) {
            for (ModelParamListener mpl : modelParamListeners) {
                mpl.setModelSize(Integer.parseInt(value));
            }
        }
        for (SerialCommunicatorListener l : listeners) {
            l.onData(commandName, value);
        }
    }

    private void testPort() {
        // Close the previous one
        if (modulePort != null) modulePort.stop();
        if (currentPortIndex == portNames.length) {
            System.out.println("No more ports to test");
            return;
        }
        try {
            modulePort = new Serial(portNames[currentPortIndex], 19200);
            System.out.println("Sending hello");
            modulePort.write(COMMAND_START + HELLO + "\n");
            portTestTimer = new Timer();
            portTestTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    portFailed();
                }
            }, 1000);
        } catch (Exception e) {
            System.out.println("Error trying port " + Serial.list()[currentPortIndex]);
            if (currentPortIndex < Serial.list().length - 1) {
                currentPortIndex++;
                testPort();
            }
        }
    }

    private void portFailed() {
        if (!connected) {
            System.out.println("Port Failed");
            portTestTimer.cancel();
            currentPortIndex++;
            testPort();
        }
    }

    public void sendCurrentParam(int parameterID, float value) {
        if (modulePort != null) {
            modulePort.write(PARAM_START + "100 " + parameterID + ":" + Float.toString(value) + "\n");
        }
    }

    public void sendCommand(String commandName, String value) {
        if (modulePort != null) {
            try {
                modulePort.write(COMMAND_START + commandName + " " + value + "\n");
            } catch (Exception e) {
                System.out.println("Failed to write to port : " + e.getMessage());
            }
        } else {
            System.out.println("Not sending " + commandName + ". No port connected.");
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

    public void addSerialCommsListener(SerialCommunicatorListener serialCommunicatorListener) {
        if (!listeners.contains(serialCommunicatorListener)) {
            listeners.add(serialCommunicatorListener);
        }
    }

    public void removeSerialCommsListener(SerialCommunicatorListener serialCommunicatorListener) {
        if (listeners.contains(serialCommunicatorListener)) {
            listeners.remove(serialCommunicatorListener);
        }
    }

}
