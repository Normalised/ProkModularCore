package com.prokmodular.model;

import com.prokmodular.files.PresetReader;
import com.prokmodular.files.PresetWriter;
import com.prokmodular.patches.PatchUpdater;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PresetManager {
    private ArrayList<PresetFile> presetFiles;
    private ProkModel model;
    private PresetReader reader;
    private PresetWriter writer;

    private PatchUpdater patchUpdater;

    public PresetManager() {
        presetFiles = new ArrayList<>();

        reader = new PresetReader();
        writer = new PresetWriter();

        patchUpdater = new PatchUpdater();
    }

    public void setCurrentModel(ProkModel currentModel) {
        if(model == currentModel) return;
        model = currentModel;
        presetFiles.clear();
    }

    public List<PresetFile> listFilesFrom(File path) {
        if(model == null) return presetFiles;

        File[] files = path.listFiles((dir, name) -> name.toLowerCase().endsWith("prk"));

        presetFiles.clear();

        for(File file : files) {
            try {
                Preset p = reader.readFile(file);
                if(p.isForModel(model)) {
                    presetFiles.add(new PresetFile(p, file));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return presetFiles;
    }

    public File getFileAtIndex(int index) {
        return presetFiles.get(index).file;
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
            if(p.config.isOlderThan(model.getConfig())) {
                return patchUpdater.upgradePreset(p, model.getConfig().getVersion());
            } else {
                return patchUpdater.downgradePreset(p, model.getConfig().getVersion());
            }

        }
    }
}
