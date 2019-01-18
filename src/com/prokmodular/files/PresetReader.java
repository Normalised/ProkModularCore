package com.prokmodular.files;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.Preset;
import com.prokmodular.model.ProkModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PresetReader {

    final Logger logger = LoggerFactory.getLogger(PresetReader.class);
    private enum ScannerState {
        VERSION, MODEL, PARAMS
    }

    public boolean modelCanReadFile(ProkModel model, File file) {

        ScannerState state = ScannerState.VERSION;

        try {
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                if (state == ScannerState.VERSION) {
                    int version = Integer.parseInt(scan.nextLine());
                    if(model.getConfig().version < version) {
                        return false;
                    }
                    state = ScannerState.MODEL;
                } else if (state == ScannerState.MODEL) {
                    String type = scan.nextLine();
                    if(type.equalsIgnoreCase(model.getConfig().getName())) {
                        return true;
                    }
                    state = ScannerState.PARAMS;

                }
            }
        } catch (FileNotFoundException e) {
            logger.debug("File not found " + file.getAbsolutePath());
            return false;
        }
        return false;
    }

    public Preset readFile(File modelFile) throws Exception {

        Preset preset = new Preset();
        ModelConfig config = new ModelConfig("","");
        List<Float> params = new ArrayList<>();

        ScannerState state = ScannerState.VERSION;

        try {
            Scanner scan = new Scanner(modelFile);
            while(scan.hasNextLine()){
                if(state == ScannerState.VERSION) {
                    config.version = Integer.parseInt(scan.nextLine());
                    state = ScannerState.MODEL;
                } else if(state == ScannerState.MODEL) {
                    config.hello = scan.nextLine();
                    state = ScannerState.PARAMS;
                } else {
                    String param = scan.nextLine();
                    String[] parts = param.split("=");

                    if(parts.length == 2) {
                        params.add(Float.parseFloat(parts[1]));
                    } else {
                        logger.debug("Config format is wrong! " + param);
                        throw new Exception("Config is broken");
                    }
                }
            }
            scan.close();

        } catch (FileNotFoundException e) {
            logger.debug("Model file not found " + modelFile.getAbsolutePath());
            throw new Exception("Model file doesn't exist");
        }

        preset.config = config;
        preset.params = params;
        return preset;
    }
}
