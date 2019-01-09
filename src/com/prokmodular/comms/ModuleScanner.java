package com.prokmodular.comms;

import com.prokmodular.ModuleInfo;
import com.prokmodular.model.ProkModel;
import org.slf4j.Logger;

import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class ModuleScanner implements HandshakeStatusListener {

    final Logger logger = getLogger(ModuleScanner.class);

    private Map<String, ProkModel> models;
    private List<ModuleInfo> modules;

    private ModuleSerialConnection currentModuleConnection;
    private Timer portTestTimer;
    private Timer connectTimer;
    private String[] portNames;
    private boolean allPortsTested = false;
    // All ports that have responded to a hello request
    private Map<String, List<ModuleSerialConnection>> modulePorts;
    private int currentPortIndex = 0;
    private Timer updateTimer;

    private List<ModuleScanStatusListener> scanStatusListeners;

    private int handshakeWaitCount = 0;
    private int handshakeWaitCountLimit = 3;

    public ModuleScanner() {
        scanStatusListeners = new ArrayList<>();
        models = new HashMap<>();
        modules = new ArrayList<>();
        modulePorts = new HashMap<>();
    }

    public void scan() {
        // List all the available serial ports:

        portNames = Serial.list();

        logger.debug("Starting port scan. Got " + portNames.length + " ports");

        if (portNames.length > 0) {
            currentPortIndex = 0;
//                updateTimer = new Timer();
//                updateTimer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        update();
//                    }
//                }, 20);
            testPort();
        } else {
            logger.debug("No serial ports found.. waiting");
            if(updateTimer != null) updateTimer.cancel();
            connectTimer = new Timer();
            connectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    scan();
                }
            }, 1000);
        }
    }

    public void close() {
        updateTimer.cancel();
    }

    private void update() {

    }

    public boolean isCheckingPorts() {
        return !allPortsTested;
    }

    private void testNextPort() {
        portTestTimer.cancel();
        currentPortIndex++;
        testPort();
    }

    private void testPort() {
        // Close the previous one
        if(currentModuleConnection != null) {
            currentModuleConnection.removeHandshakeStatusListener(this);
        }

        if (currentPortIndex == portNames.length) {
            logger.debug("No more ports to test");
            allPortsTested = true;
            portsChecked();
            return;
        }
        try {
            logger.debug("Testing port " + portNames[currentPortIndex]);
            Serial serial = new Serial(portNames[currentPortIndex], 19200);
            currentModuleConnection = new ModuleSerialConnection(serial);
            currentModuleConnection.addHandshakeStatusListener(this);
            logger.debug("Sending hello");
            handshakeWaitCount = 0;
            portTestTimer = new Timer();
            portTestTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkCurrentModuleHandshakeStatus();
                }
            }, 300, (300 * (handshakeWaitCountLimit + 1) ));
        } catch (Exception e) {
            logger.debug("Error trying port " + Serial.list()[currentPortIndex]);
            if (currentPortIndex < Serial.list().length - 1) {
                currentPortIndex++;
                testPort();
            }
        }
    }

    private void checkCurrentModuleHandshakeStatus() {
        logger.debug("Checking current module handshake status");
        if(currentModuleConnection.getHandshakeStatus() == ModuleSerialConnection.HandshakeStatus.WAITING) {
            logger.debug("Still waiting for handshake");
            handshakeWaitCount++;
        }
        if(handshakeWaitCount == handshakeWaitCountLimit) {
            logger.debug("Port is taking too long, trying next");
            portFailed();
        }
    }

    private void processSerial(StringBuffer serialBuffer) {
    }

    public void onHandshakeComplete(ModuleSerialConnection module) {
        // we've found a module serial port
        logger.debug("Module completed handshake : " + module.getDataValue(Messages.NAME));
        String moduleType = module.getDataValue(Messages.NAME);
        if(modulePorts.containsKey(moduleType)) {
            modulePorts.get(moduleType).add(module);
        } else {
            ArrayList<ModuleSerialConnection> modules = new ArrayList<>();
            modules.add(module);
            modulePorts.put(moduleType, modules);
        }

        if (allPortsTested) {
            logger.debug("All ports tested, " + modulePorts.size() + " responded.");
        }

        testNextPort();
    }


    private void portFailed() {
        logger.debug("Port failed : " + portNames[currentPortIndex]);
        portTestTimer.cancel();
        currentPortIndex++;
        testPort();
    }

    public void portsChecked() {
        logger.debug("All Ports Checked");
        modules = new ArrayList<>();
        for(Map.Entry<String, List<ModuleSerialConnection>> connection : modulePorts.entrySet()) {
            logger.debug("Available module type : " + connection.getKey() + " with " + connection.getValue().size() + " ports.");
            ModuleInfo module = new ModuleInfo();
            String moduleType = connection.getKey();
            if(moduleType.contains("_")) {
                moduleType = moduleType.substring(0,moduleType.indexOf("_"));
            }
            module.type = moduleType;
            if(!models.containsKey(moduleType)) {
                //module.setUnknownType();
            } else {
                module.model = models.get(moduleType);

                // App can wire in appropriate UI when opening param editor
                //module.ui = uis.get(moduleType);

                //module.setValid();
                //moduleState.put("name", module.getModelName());
            }
            modules.add(module);

        }
        List<ModuleScanStatusListener> tempListeners = new ArrayList<>(scanStatusListeners);

        for (ModuleScanStatusListener l : tempListeners) {
            l.scanComplete(modules);
        }

    }

    public void register(ProkModel model) {
        models.put(model.getConfig().getName(), model);
    }

    public int getPortCount() {
        return Serial.list().length;
    }

    public void addScanStatusListener(ModuleScanStatusListener l) {
        scanStatusListeners.add(l);
    }

    public void removeScanStatusListener(ModuleScanStatusListener l) {
        scanStatusListeners.remove(l);
    }
}
