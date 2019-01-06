package com.prokmodular.model;

import com.prokmodular.files.PresetReader;
import com.prokmodular.files.PresetWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PresetManager {
    private ArrayList<File> presetFiles;
    private ProkModel model;
    private PresetReader reader;
    private PresetWriter writer;

    public PresetManager() {
        presetFiles = new ArrayList<>();

        reader = new PresetReader();
        writer = new PresetWriter();

    }

    public void setCurrentModel(ProkModel currentModel) {
        if(model == currentModel) return;
        model = currentModel;
        presetFiles.clear();
    }

    public List<File> listFilesFrom(File path) {
        if(model == null) return presetFiles;

        File[] files = path.listFiles((dir, name) -> name.toLowerCase().endsWith("prk"));

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
