package com.prokmodular.patches;

import com.prokmodular.model.ParamDefinition;
import com.prokmodular.model.PatchDefinition;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileScanners {

    public static PatchDefinition getPatchFromParams(File paramNamesFile, PatchDefinition defaultValues) throws Exception {
        List<String> names = scanParamNames(paramNamesFile);
        // Load param name map for this version
        PatchDefinition patchDefinition = new PatchDefinition();
        int index = 0;
        for(String name : names) {
            ParamDefinition param = new ParamDefinition(index, name, defaultValues.getDefaultValueFor(name));
            patchDefinition.addParam(param);
            index++;
        }

        return patchDefinition;
    }

    public static List<String> scanParamNames(File paramNamesFile) throws Exception {
        List<String> names = new ArrayList<>();

        //", *"

        try {
            Scanner scan = new Scanner(paramNamesFile);
            scan.useDelimiter(", *");
            while(scan.hasNext()){
                String name = scan.next().trim();
                if(name.length() > 0 && !name.equalsIgnoreCase("NUM_PARAMS")) {
//                    System.out.println("Name:" + name + ".");
                    names.add(name);
                }

            }
            scan.close();

        } catch (FileNotFoundException e) {
            throw new Exception("Param names file doesn't exist " + paramNamesFile.getAbsolutePath());
        }
        return names;
    }

    public static void scanDefaultValues(File defaultValuesFile, PatchDefinition defaultPatchValues) throws Exception {
        try {
            Scanner scan = new Scanner(defaultValuesFile);
            int paramIndex = 0;
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String[] parts = line.split(" = ");
                if(parts.length == 2) {
                    String name = parts[0];
                    Float value = Float.parseFloat(parts[1]);
                    ParamDefinition param = new ParamDefinition(name, value);
                    param.index = paramIndex++;
                    defaultPatchValues.addParam(param);
                }
            }
            scan.close();

        } catch (FileNotFoundException e) {
            throw new Exception("Default Values file doesn't exist");
        }
    }

    private enum PresetHeaderState {
        BEFORE_PARAMS, IN_PARAMS, FINISHED
    }

    public static List<List<Float>> scanExistingPresets(File presetsFile) throws Exception {

        PresetHeaderState parseState = PresetHeaderState.BEFORE_PARAMS;

        List<List<Float>> presetList = new ArrayList<>();
        try {
            Scanner scan = new Scanner(presetsFile);
            while(scan.hasNextLine()){
                String line = scan.nextLine().trim();
                if(parseState == PresetHeaderState.BEFORE_PARAMS) {
                    if(line.contains("float defaultParams[16]")) {
                        parseState = PresetHeaderState.IN_PARAMS;
                    }
                    continue;
                }
                if(parseState == PresetHeaderState.IN_PARAMS) {
                    if(!line.startsWith("{")) {
                        parseState = PresetHeaderState.FINISHED;
                        break;
                    }

                    String[] values = line.substring(1, line.length() - 2).split(",");

                    if(values.length > 0) {
//                        System.out.println("Got " + values.length + " values");
                        List<Float> preset = new ArrayList<>();
                        for(String val : values) {
                            preset.add(Float.parseFloat(val));
                        }

                        presetList.add(preset);
                    }
                }
            }
            scan.close();

        } catch (FileNotFoundException e) {
            throw new Exception("Preset Header file doesn't exist");
        }
        return presetList;
    }
}
