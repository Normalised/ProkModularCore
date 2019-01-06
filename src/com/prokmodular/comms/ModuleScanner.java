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

    public ModuleScanner() {
        scanStatusListeners = new ArrayList<>();
        models = new HashMap<>();
        modules = new ArrayList<>();
        modulePorts = new HashMap<>();
    }

    public void init(boolean useExternalUpdate) {
        // List all the available serial ports:

        portNames = Serial.list();

        logger.debug("Got " + portNames.length + " ports");

        // Open the port you are using at the rate you want:
        if (portNames.length > 0) {
            currentPortIndex = 0;
            if(!useExternalUpdate) {
                updateTimer = new Timer();
                updateTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        update();
                    }
                }, 20);
            }
            testPort();
        } else {
            logger.debug("No serial ports found.. waiting");
            if(updateTimer != null) updateTimer.cancel();
            connectTimer = new Timer();
            connectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    init(false);
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
            currentModuleConnection = new ModuleSerialConnection(portNames[currentPortIndex]);
            currentModuleConnection.addHandshakeStatusListener(this);
            logger.debug("Sending hello");
            portTestTimer = new Timer();
            portTestTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkCurrentModuleHandshakeStatus();
                }
            }, 1000);
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
            l.scanComplete(modulePorts);
        }

    }

    public void register(ProkModel model) {
        models.put(model.getConfig().getName(), model);
    }

    public void scan() {

    }
}
