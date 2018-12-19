package com.prokmodular.files;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.Preset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PresetReader {

    private enum ScannerState {
        VERSION, MODEL, PARAMS
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
                        System.out.println("Config format is wrong! " + param);
                        throw new Exception("Config is broken");
                    }
                }
            }
            scan.close();

        } catch (FileNotFoundException e) {
            System.out.println("Model file not found " + modelFile.getAbsolutePath());
            throw new Exception("Model file doesn't exist");
        }

        preset.config = config;
        preset.params = params;
        return preset;
    }
}
