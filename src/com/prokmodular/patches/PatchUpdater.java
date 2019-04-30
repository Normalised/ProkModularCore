package com.prokmodular.patches;

import com.prokmodular.drums.ClapModel;
import com.prokmodular.drums.HiHatModel;
import com.prokmodular.drums.KickModel;
import com.prokmodular.drums.SnareModel;
import com.prokmodular.files.PresetHeaderWriter;
import com.prokmodular.files.PresetReader;
import com.prokmodular.files.PresetWriter;
import com.prokmodular.model.ParamDefinition;
import com.prokmodular.model.PatchDefinition;
import com.prokmodular.model.Preset;
import com.prokmodular.model.ProkModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class PatchUpdater {

    final Logger logger = LoggerFactory.getLogger(PatchUpdater.class);
    
    private PresetReader patchReader;
    private PresetWriter patchWriter;
    private Map<String, ProkModel> models;

    private Map<String, Map<Integer, PatchDefinition>> modelPatchDefinitionMap;

    public PatchUpdater() {
        models = new HashMap<>();

        addModel(new SnareModel());
        addModel(new ClapModel());
        addModel(new KickModel());
        addModel(new HiHatModel());

        modelPatchDefinitionMap = new HashMap();
    }

    private void addModel(ProkModel model) {
        models.put(model.getConfig().getName(), model);
    }

    public void updateDefaultParams(String modelName) throws Exception {

        File definitionFolder = new File("data\\" + modelName);
        logger.debug("Drum Config Folder " + definitionFolder.getAbsolutePath());

        int latestVersion = getMostRecentVersion(definitionFolder);
        logger.debug("Most recent version " + String.valueOf(latestVersion));

        if(latestVersion == 1) {
            logger.debug("Latest version is 1, nothing to do");
            return;
        }

        // Read 'defaultValues.txt' from definitionFolder
        // create map from name to default value
        PatchDefinition defaultPatchValues = new PatchDefinition();
        FileScanners.scanDefaultValues(new File(definitionFolder, "DefaultValues.txt"), defaultPatchValues);

        logger.debug("loaded default patch values " + defaultPatchValues.params.size());

        // From latestVersion - 1 get
        // DefaultParams.h
        // ParamNames.txt
        int previousVersion = latestVersion - 1;
        File previousVersionFolder = new File(definitionFolder, String.valueOf(previousVersion));
        List<String> previousParamNames = FileScanners.scanParamNames(new File(previousVersionFolder, "ParamNames.txt"));

//        logger.debug("Previous Param Names");
//        for(String s : previousParamNames) {
//            logger.debug(s);
//        }

        List<List<Float>> presets = FileScanners.scanExistingPresets(new File(previousVersionFolder, "DefaultParams.h"));

        // From latestVersion get
        // ParamNames.txt
        File currentVersionFolder = new File(definitionFolder, String.valueOf(latestVersion));
        List<String> currentParamNames = FileScanners.scanParamNames(new File(currentVersionFolder, "ParamNames.txt"));

//        logger.debug("Current Param Names");
//        for(String s : currentParamNames) {
//            logger.debug(s);
//        }

        int paramCountDiff = currentParamNames.size() - previousParamNames.size();
        logger.debug("Current size difference " + paramCountDiff);

        if(currentParamNames.size() != defaultPatchValues.params.size()) {
            logger.debug("Current params and default values have different sizes. Quitting");
            return;
        }

        List<List<Float>> newPresets = new ArrayList<>();
        for(int i=0;i<presets.size();i++) {
            List<Float> newPreset = new ArrayList<>();
            List<Float> oldPreset = presets.get(i);

            for(String name : currentParamNames) {

                // get position of 'name' in old preset list
                int oldNamePos = previousParamNames.indexOf(name);
                if(oldNamePos == -1) {
//                    logger.debug("Name not in old preset : " + name);
                    // use default value
                    Float defaultValue = defaultPatchValues.getDefaultValueFor(name);
                    newPreset.add(defaultValue);
                } else {
                    // use old value
                    Float oldValue = oldPreset.get(oldNamePos);
                    newPreset.add(oldValue);
                }
            }
            newPresets.add(newPreset);
        }

        logger.debug("Writing updated header");
        // Write new <latestVersion>/DefaultParams.h
        PresetHeaderWriter.generateHeaderFile(currentVersionFolder, modelName, newPresets);
    }

    public void updatePatchFiles(File definitionFolder, String modelName, File patchFolder, File backupFolder) throws Exception {

        if(!patchFolder.exists()) {
            throw new Exception("Patch folder doesnt exist " + patchFolder.getAbsolutePath());
        }

        patchReader = new PresetReader();
        patchWriter = new PresetWriter();
        int latestVersion = getMostRecentVersion(definitionFolder);

        if(!modelPatchDefinitionMap.containsKey(modelName)) {
            loadModelPatchDefinitions(modelName);
        }

        Map<Integer, PatchDefinition> patchDefinitionMap = modelPatchDefinitionMap.get(modelName);

        File[] presetFiles = patchFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".prk"));

        for(File presetFile : presetFiles) {

            try {
                Preset preset = patchReader.readFile(presetFile);

                if(preset.config.getVersion() < latestVersion) {
                    logger.debug("Upgrading " + presetFile.getName() + " from " + preset.config.getVersion() + " to " + latestVersion);
                    // insert / delete new entries
                    Preset upgraded = upgradePreset(preset, latestVersion, patchDefinitionMap);
                    // rewrite
                    String presetFileName = presetFile.getName();
                    logger.debug("Backing up " + presetFileName + " to " + backupFolder.getAbsolutePath());
                    presetFile.renameTo(new File(backupFolder, presetFile.getName()));
                    patchWriter.write(upgraded, new File(patchFolder,presetFileName));
                }
            } catch (Exception e) {
                logger.debug("Exception in update : " + e.getMessage());
//                e.printStackTrace();
            }

        }
    }

    private void loadModelPatchDefinitions(String modelName) throws Exception {

        File definitionFolder = new File("data\\" + modelName);

        logger.debug("Load Model Patch definitions for " + modelName + " from " + definitionFolder.getAbsolutePath());

        Map<Integer, PatchDefinition> patchDefinitionMap = new HashMap<>();
        PatchDefinition defaultPatchValues = new PatchDefinition();
        FileScanners.scanDefaultValues(new File(definitionFolder, "DefaultValues.txt"), defaultPatchValues);
        patchDefinitionMap.put(0, defaultPatchValues);

        List<Integer> availableVersions = getAvailableVersions(definitionFolder);

        for(Integer version : availableVersions) {
            File versionFolder = new File(definitionFolder, String.valueOf(version));
            PatchDefinition patch = FileScanners.getPatchFromParams(new File(versionFolder, "ParamNames.txt"), defaultPatchValues);
            patchDefinitionMap.put(version, patch);
        }

        modelPatchDefinitionMap.put(modelName, patchDefinitionMap);
    }

    private List<Integer> getAvailableVersions(File definitionFolder) {
        List<Integer> versions = new ArrayList<>();
        File[] directories = definitionFolder.listFiles(File::isDirectory);
        for(File dir : directories) {
            try {
                Integer dirVersion = Integer.parseInt(dir.getName());
                if(dirVersion > 0) {
                    versions.add(dirVersion);
                }
            } catch (Exception e) {

            }
        }
        return versions;
    }

    private int getMostRecentVersion(File definitionFolder) {
        File[] directories = definitionFolder.listFiles(File::isDirectory);
        int highestVersion = 1;
        for(File dir : directories) {
            try {
                int dirVersion = Integer.parseInt(dir.getName());
                if(dirVersion > highestVersion) {
                    highestVersion = dirVersion;
                }
            } catch (Exception e) {

            }

        }
        return highestVersion;
    }

    public Preset upgradePreset(Preset preset, int destVersion) throws Exception {
        logger.debug("Upgrade Preset " + preset.config.getName() + " to " + destVersion);
        // Get patch definition map for this model type
        if(!modelPatchDefinitionMap.containsKey(preset.config.getName())) {
            loadModelPatchDefinitions(preset.config.getName());
        }
        Map<Integer, PatchDefinition> patchDefinitionMap = modelPatchDefinitionMap.get(preset.config.getName());
        return upgradePreset(preset, destVersion, patchDefinitionMap);
    }

    private Preset upgradePreset(Preset preset, int newVersion, Map<Integer, PatchDefinition> patchDefinitionMap) throws Exception {

        // For the preset get the name of each param from the index

        PatchDefinition oldPatch = patchDefinitionMap.get(preset.config.getVersion());
        PatchDefinition latestPatch = patchDefinitionMap.get(newVersion);

        Preset newPreset = new Preset();
        newPreset.config.setVersion(newVersion);
        newPreset.config.setName(preset.config.getName());

        for(ParamDefinition param : latestPatch.params) {
            String name = param.name;
            // get position of 'name' in old preset list
            boolean useDefault = false;
            ParamDefinition oldParamDef = null;
            if(!oldPatch.hasParamNamed(param.name)) {
                useDefault = true;
            } else {
                oldParamDef = oldPatch.getParamFromName(param.name);
                if(oldParamDef.index == -1) {
                    useDefault = true;
                }
            }

            if(useDefault) {
                Float defaultValue = latestPatch.getDefaultValueFor(name);
                logger.debug("Using default for " + name + " -> " + String.valueOf(defaultValue));
                newPreset.params.add(param.index, defaultValue);
            } else if(oldParamDef != null) {
                newPreset.params.add(param.index, preset.params.get(oldParamDef.index));
            }
        }

        return newPreset;
    }


}
