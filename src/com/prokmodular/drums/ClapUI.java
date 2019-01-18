package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;

import static com.prokmodular.model.ParameterMapping.createLinear;
import static com.prokmodular.model.ParameterMapping.createSquared;

public class ClapUI implements ModelUI {
    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {
        ui.addMixerChannel("White Noise");
        ui.addMixerChannel("Sid Noise");
        ui.addMixerChannel("Pink Noise");

        ui.addSpace();

        ui.addStateVariable("White", 50, 5000);
        ui.addStateVariable("Sid", 50, 5000);
        ui.addStateVariable("Pink", 50, 5000);

        ui.addSpace();

        ui.addNoiseSampleRate();
        ui.addSlider("Distort", 1, 100);

        ui.addSpace();

        ui.addIntSlider("Rolls", 1, 10);
        ui.addIntSlider("Roll Time", createSquared(1, 100, 10, 5000));
        ui.addIntSlider("Roll Low", createLinear(0, 50, 0, 16000));
        ui.addSlider("Roll High Start", createLinear(1, 100, 100, 32000));
        ui.addIntSlider("Roll High Step", createLinear(1, 100, 10, 8000));
        ui.addSlider("Roll Attack", createSquared(0, 100, 0, 8000));

        ui.nextColumn();

        ui.addADEnvelope("Verb");
        ui.addADEnvelope("Main");

        ui.addSpace();
        ui.addBiquad("Main", 40, 12000);
        ui.addSpace();
        ui.addStateVariable("Verb", 40, 8000);

        ui.nextColumn();

        ui.addMixerChannel("Main Roll Level");
        ui.addMixerChannel("Main Body Level");
        ui.addMixerChannel("Verb Roll Level");
        ui.addMixerChannel("Verb Body Level");

        ui.addSpace();

        ui.addSlider("Low Shelf Cutoff", 400, 15000);
        ui.addSlider("Low Shelf Gain", 0, -24);
        ui.addSlider("Low Shelf Slope", createLinear(1, 100, 0.1f, 6f));

        ui.addSpace();
        ui.addSlider("High Shelf Cutoff", 400, 15000);
        ui.addSlider("High Shelf Gain", 0, 24);
        ui.addSlider("High Shelf Slope", createLinear(1, 100, 0.1f, 6f));

        ui.addSpace();

        ui.addMixerChannel("Dry Level");
        ui.addMixerChannel("Wet Level");

        ui.addSlider("Damping", createLinear(0, 100, 0f, 1f));
        ui.addSlider("Room Size", createLinear(0, 100, 0f, 1f));

        ui.addMixerChannel("Output Level");
    }
}
