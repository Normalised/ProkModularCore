package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;

import static com.prokmodular.model.ParameterMapping.createLinear;
import static com.prokmodular.model.ParameterMapping.createNone;
import static com.prokmodular.model.ParameterMapping.createSquared;

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

        ui.addSpace();

        ui.addMixerChannel("Sid Noise LPF");
        ui.addMixerChannel("Sid Noise HPF");

        ui.addSpace();

        ui.addMixerChannel("Sine A");
        ui.addMixerChannel("Sine B");

        ui.nextColumn();

        ui.addBiquad("Body", 50, 5000);

        ui.addSpace();

        ui.addMixerChannel("Body");
        ui.addMixerChannel("Noise");
        ui.addMixerChannel("Transient");

        ui.addSpace();

        if(version == 1) {
            ui.addMixerChannel("Output Level");

            ui.addSlider("Distort",1,100);
            ui.addSlider("Distort2",1,100);

        } else {

            ui.addSlider("Distort",1,100);
            ui.addSlider("Distort2",1,100);

            ui.addSpace();

            ui.addSlider("Feedback A",createLinear(0,100, 0, 32767));
            ui.addSlider("Feedback B",createLinear(0,100, 0, 32767));

            ui.addSlider("Time A",1,200);
            ui.addSlider("Time B", 1,200);

            ui.addMixerChannel("Delay Level A");
            ui.addMixerChannel("Delay Level B");
            ui.addSpace();

            ui.addSlider("Feedback Filter A Cutoff", createNone(50, 10000));
            ui.addSlider("Feedback Filter A Res", createLinear(0, 100, 0.7f, 2.0f));
            ui.addSlider("Feedback Filter B Cutoff", createNone(40, 4000));
            ui.addSlider("Feedback Filter B Res", createLinear(0, 100, 0.7f, 2.0f));

            ui.addSpace();
            ui.addADEnvelope("Out Env");
            ui.addSpace();
            ui.addMixerChannel("Output Level");
        }

    }
}
