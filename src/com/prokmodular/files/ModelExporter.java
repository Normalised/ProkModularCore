package com.prokmodular.files;

import com.prokmodular.comms.CommandContents;
import com.prokmodular.comms.Commands;
import com.prokmodular.comms.ModuleSerialConnection;
import com.prokmodular.comms.ParamMessage;
import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ModelParamListener;
import com.prokmodular.model.Preset;
import com.prokmodular.model.ProkModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import static processing.core.PApplet.println;
//import static processing.core.PApplet.selectFolder;

enum LocalModelState {
    EMPTY, FETCHING, LOADED
}

enum LoadedAction {
    NONE, GENERATE_HEADER, SAVE_LOCALLY
}

public class ModelExporter implements ModelParamListener {

    private ModuleSerialConnection serial;
    private List<List<Float>> modelParams;

    private LocalModelState localModelState = LocalModelState.EMPTY;
    private LoadedAction loadedAction = LoadedAction.NONE;

    private int modelSize = 0;

    private int currentFetchIndex = 0;

    private ProkModel currentModel;

    private PresetWriter presetWriter;
    private File folderToSaveTo;

    public ModelExporter() {

        presetWriter = new PresetWriter();

        modelParams = new ArrayList<>(16);
        for(int i=0;i<16;i++) {
            modelParams.add(i, new ArrayList<>());
        }
    }

    public void saveLocally(ProkModel model) {
        currentModel = model;
        loadedAction = LoadedAction.SAVE_LOCALLY;
        loadModels();
    }

    public void saveLocally(ProkModel model, File folder) {
        currentModel = model;
        loadedAction = LoadedAction.SAVE_LOCALLY;
        folderToSaveTo = folder;
        loadModels();
    }

    public void generateHeader(ProkModel model) {
        currentModel = model;
        loadedAction = LoadedAction.GENERATE_HEADER;
        loadModels();
    }

    private void loadModels() {
        System.out.println("Load Models " + localModelState);

        if(localModelState == LocalModelState.EMPTY) {
            currentFetchIndex = 0;
            serial.sendCommand(new CommandContents(Commands.SEND_PARAMS, String.valueOf(currentFetchIndex)));
            System.out.println("Requesting params");
            localModelState = LocalModelState.FETCHING;
        } else if(localModelState == LocalModelState.FETCHING) {
            System.out.println("Fetching..");
        } else if(localModelState == LocalModelState.LOADED) {
            doLoadedAction();
        }
    }

    private void saveModelFiles() {
        System.out.println("Writing model files to disk");

        File outputDirectory;

        if(folderToSaveTo != null) {
            outputDirectory = folderToSaveTo;
        } else {
            outputDirectory = new File(getOutputDirectory(), "from_module");
        }

        outputDirectory.mkdirs();

        int index = 0;
        ModelConfig config = currentModel.getConfig();
        for(List<Float> params : modelParams) {
            Preset preset = new Preset(config, params);
            String outputFilename = config.filename + Integer.toHexString(index) + ".prk";
            File file = new File(outputDirectory, outputFilename.toUpperCase());
            try {
                presetWriter.write(preset, file);
            } catch (Exception e) {

                e.printStackTrace();
            }
            index++;
        }
    }

    private File getOutputDirectory() {

        File homeDir = new File(System.getProperty("user.home"));
        File outputDir = new File(homeDir, "prokDrums");
        File modelDir = new File(outputDir, currentModel.getConfig().filename);
        if(!modelDir.exists()) {
            modelDir.mkdirs();
        }

        return modelDir;
    }

    @Override
    public void setModelSize(int numParams) {
        this.modelSize = numParams;
//        for(int i=0;i<16;i++) {
//            modelParams.get(i).ensureCapacity(numParams);
//        }
    }

    @Override
    public void setCurrentParam(ParamMessage msg) {
        // Don't care.
    }

    @Override
    public void setParam(int modelIndex, ParamMessage msg) {
        //int paramID , float paramValue
        System.out.println("Set Header Param " + modelIndex + " : " + msg.id + " : " + msg.value);

        ArrayList<Float> params = (ArrayList<Float>) modelParams.get(modelIndex);
        if(msg.id == params.size())  {
            params.add(msg.value);
        } else {
            params.ensureCapacity(msg.id);
            params.set(msg.id, msg.value);
        }

        if(localModelState == LocalModelState.FETCHING) {
            int unfilledModelIndex = checkAllParamsReceived();
            if(unfilledModelIndex == 16) {
                localModelState = LocalModelState.LOADED;
                doLoadedAction();
            } else if(unfilledModelIndex > currentFetchIndex) {
                currentFetchIndex = unfilledModelIndex;
                serial.sendCommand(new CommandContents(Commands.SEND_PARAMS, String.valueOf(currentFetchIndex)));
            }
        }
    }

    private void doLoadedAction() {
        if(loadedAction == LoadedAction.GENERATE_HEADER) {
            PresetHeaderWriter.generateHeaderFile(getOutputDirectory(), currentModel.getConfig().filename, modelParams);
        } else if(loadedAction == LoadedAction.SAVE_LOCALLY) {
            saveModelFiles();
        }
    }

    private int checkAllParamsReceived() {
        System.out.println("Checking all received : " + modelParams.get(15).size() + " == " + modelSize);
        for(int i=0;i<16;i++) {
            if(modelParams.get(i).size() != modelSize) {
                System.out.println("Model " + i + " not full. " + modelParams.get(i).size());
                return i;
            }
        }
        System.out.println("All params received");
        return 16;
    }

    public void setConnection(ModuleSerialConnection connectionToUse) {
        if(serial != null) {
            serial.removeModelParamListener(this);
        }
        serial = connectionToUse;
        serial.addModelParamListener(this);
    }
}
