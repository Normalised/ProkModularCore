package com.prokmodular.files;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.Preset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PresetWriter {

    public boolean write(Preset preset, File file) throws Exception {

        ModelConfig modelConfig = preset.config;
        List<Float> params = preset.params;

        if(modelConfig == null) {
            throw new Exception("Preset model config is null");
        }

        System.out.println("Writing preset with " + String.valueOf(params.size()) + " params to " + file.getAbsolutePath());
        if(file == null) {
            return false;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(String.valueOf(modelConfig.version));
            writer.newLine();
            writer.write(modelConfig.hello);
            writer.newLine();

            for (int i = 0; i < params.size(); i++) {
                writer.write(i + "=" + String.valueOf(params.get(i)));
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Write exception " + e.getMessage());
            return false;
        }

        System.out.println("Write config to " + file.getAbsolutePath());
        return true;
    }

}
