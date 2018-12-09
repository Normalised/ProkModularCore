package com.prokmodular.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PresetHeaderWriter {
    public static void generateHeaderFile(File outputDirectory, String modelName, List<List<Float>> presets) {

        File headerFile = new File(outputDirectory, "DefaultParams.h");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(headerFile));
            writer.write("#ifndef Default_Params_h");
            writer.newLine();
            writer.write("#define Default_Params_h");
            writer.newLine();
            writer.write("#include \"Config.h\"");
            writer.newLine();
            writer.newLine();
            writer.write("// Default Params for " + modelName);
            writer.newLine();
            writer.newLine();

            List<String> modelString = new ArrayList<String>();
            for (List<Float> params : presets) {

                List<String> str = new ArrayList<String>();
                for (Float p : params) {
                    str.add(String.valueOf(p));
                }
                modelString.add("\t{" + String.join(",", str) + "}");
            }

            writer.write("const float defaultParams[16][NUM_PARAMS] = {");
            writer.newLine();
            writer.write(String.join(",\n", modelString));
            writer.newLine();
            writer.write("};");
            writer.newLine();
            writer.write("#endif");
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("Write exception " + e.getMessage());
        }

    }
}
