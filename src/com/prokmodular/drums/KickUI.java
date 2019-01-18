package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;

public class KickUI implements ModelUI {
    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {
        ui.addSineWithEnvelope("Sine A");

        ui.addSpace();

        ui.addSineWithEnvelope("Sine B");

        ui.addSpace();

        ui.addADEnvelope("Amp A");

        ui.addSpace();

        ui.addADEnvelope("Amp B");

        ui.nextColumn();

        ui.addTriModWithEnvelope("Tri A");

        ui.addSpace();

        ui.addTriModWithEnvelope("Tri B");

        ui.addSpace();

        ui.addADEnvelope("Tri Amp A");

        ui.addSpace();

        ui.addADEnvelope("Tri Amp B");

        ui.nextColumn();

        ui.addBiquad("OSC", 50, 6500);
        ui.addSlider("OSC Distort", 0, 100);

        ui.addSpace();

        ui.addBiquad("Noise", 50, 3500);

        ui.addSpace();

        ui.addADEnvelope("Noise");

        ui.addMixerChannel("Sin A");
        ui.addMixerChannel("Sin B");
        ui.addMixerChannel("Tri A");
        ui.addMixerChannel("Tri B");

        ui.addSpace();

        ui.addMixerChannel("OSCs");
        ui.addMixerChannel("Noise");
        ui.addMixerChannel("Transient");
        ui.addSlider("Distort", 1, 100);
        ui.addADEnvelope("Out");
        ui.addMixerChannel("Main Output");

    }
}
