package com.prokmodular.model;

import com.prokmodular.files.PresetReader;
import com.prokmodular.files.PresetWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PresetManager {
    private ArrayList<File> presetFiles;
    private File modelDir;
    private ProkModel model;
    private PresetReader reader;
    private PresetWriter writer;

    public PresetManager() {
        presetFiles = new ArrayList<>();

        reader = new PresetReader();
        writer = new PresetWriter();

    }

    public void setCurrentModel(ProkModel currentModel, File modelDir) {
        if(model == currentModel) return;
        model = currentModel;
        this.modelDir = modelDir;
        presetFiles.clear();

        //println("Set Current Model " + currentModel.getConfig().getName());
    }

    public List<File> listFiles() {

        if(model == null) return presetFiles;

        File[] files = modelDir.listFiles((dir, name) -> name.toLowerCase().endsWith("prk"));

        presetFiles.clear();

        for(File file : files) {
            presetFiles.add(file);
        }
        return presetFiles;
    }

    public File getFileAtIndex(int index) {
        return presetFiles.get(index);
    }

    public void savePreset(Preset preset, File f) {
        try {
            writer.write(preset, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listFiles();
    }

    public Preset readPreset(File presetFile) throws Exception {
        Preset p = reader.readFile(presetFile);
        if(p.config.isCompatibleWith(model.getConfig())) {
            return p;
        } else {
            throw new Exception("Incompatible preset");
        }
    }
}
