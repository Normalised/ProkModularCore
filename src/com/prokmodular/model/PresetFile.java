package com.prokmodular.model;

import java.io.File;
import java.nio.CharBuffer;

public class PresetFile {

    public File file;
    public Preset preset;

    public PresetFile(Preset p, File f) {
        preset = p;
        file = f;
    }

    public String spaces( int spaces ) {
        return CharBuffer.allocate( spaces ).toString().replace( '\0', ' ' );
    }

    public String getVersionedName() {
        int fileNameLength = file.getName().length();
        return file.getName() + spaces(40 - fileNameLength) + "(" + preset.config.getVersion() + ")";
    }

    public String getName() {
        return file.getName();
    }
}
