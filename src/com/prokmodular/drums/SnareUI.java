package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;

public class SnareUI implements ModelUI {
    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {
        ui.addSineWithEnvelope("Sine A", 32500);
        ui.addSpace();
        ui.addSineWithEnvelope("Sine B", 32500);
        ui.addSpace();

        ui.addADEnvelope("Amp A");
        ui.addSpace();

        ui.addADEnvelope("Amp B");

        ui.nextColumn();

        ui.addNoiseSampleRate();

        ui.addStateVariable("Filter A", 50, 10000);
        ui.addStateVariable("Filter B", 50, 10000);

        ui.addSpace();

        ui.addShortExpEnv("Sid Noise LPF");

        ui.addSpace();

        ui.addShortExpEnv("Sid Noise HPF");

        ui.nextColumn();

        ui.addMixerChannel("Sid Noise LPF");
        ui.addMixerChannel("Sid Noise HPF");

        ui.addSpace();

        ui.addMixerChannel("Sine A");
        ui.addMixerChannel("Sine B");

        ui.addSpace();

        ui.addBiquad("Body", 50, 5000);

        ui.addSpace();

        ui.addMixerChannel("Body");
        ui.addMixerChannel("Noise");
        ui.addMixerChannel("Transient");

        ui.addSpace();

        ui.addMixerChannel("Output Level");

        ui.addSlider("Distort",1,100);
        ui.addSlider("Distort2",1,100);

    }
}
