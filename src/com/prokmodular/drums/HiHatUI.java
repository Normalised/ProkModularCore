package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;

import static com.prokmodular.model.ParameterMapping.createLinear;
import static com.prokmodular.model.ParameterMapping.createSquared;

public class HiHatUI implements ModelUI {
    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {
        //ui.addKnob("Base Freq", 0, 1000);
        ui.addSlider("Base Freq", 0, 1000);

//        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 1", 50, 3000));
//        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 2", 50, 3000));
//        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 3", 50, 3000));
//        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 4", 50, 3000));
//        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 5", 50, 3000));
//        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 6", 50, 3000));
//
//        ui.addSpace();
//
//        gainSliders.add((Slider) ui.addSlider("Square Gain 1", createLinear(0, 100, 0, 1)));
//        gainSliders.add((Slider) ui.addSlider("Square Gain 2", createLinear(0, 100, 0, 1)));
//        gainSliders.add((Slider) ui.addSlider("Square Gain 3", createLinear(0, 100, 0, 1)));
//        gainSliders.add((Slider) ui.addSlider("Square Gain 4", createLinear(0, 100, 0, 1)));
//        gainSliders.add((Slider) ui.addSlider("Square Gain 5", createLinear(0, 100, 0, 1)));
//        gainSliders.add((Slider) ui.addSlider("Square Gain 6", createLinear(0, 100, 0, 1)));

        ui.addSpace();

        ui.addMixerChannel("Source Noise");
        ui.addNoiseSampleRate();

        ui.nextColumn();

        // 2 bandpass filters
        ui.addBiquad("Band Pass Low", 80, 10000);
        ui.addBiquad("Band Pass Mid/High", 500, 10000);

        ui.addSpace();

        // Band Pass Input Gains
        ui.addSlider("BP Gain Low", createSquared(0, 100, 0, 1));
        ui.addSlider("BP Gain Mid/High", createSquared(0, 100, 0, 1));

        // 2 shapers
        ui.addSlider("Shaper Mid/High", 1, 100);

        ui.addSpace();

        ui.addSlider("Osc Attack", createSquared(0, 100, 0, 32000));

        ui.addSpace();

        // 3 envelopes
        ui.addSlider("Decay Low", createSquared(0, 100, 1, 16000));
        ui.addSlider("Low Extend", createLinear(0, 100, 0, 32000));
        if(version > 5) {
            ui.addIntSlider("Low Extend Factor", -8,64);
        }
        ui.addSlider("Decay Mid", createSquared(0, 100, 1, 16000));
        ui.addSlider("Mid Extend", createLinear(0, 100, 0, 32000));
        if(version > 5) {
            ui.addIntSlider("Mid Extend Factor", -8,64);
        }

        ui.addSlider("Decay High", createSquared(0, 100, 1, 16000));
        ui.addSlider("High Extend", createLinear(0, 100, 0, 32000));
        if(version > 5) {
            ui.addIntSlider("High Extend Factor", -8,64);
        }

        ui.addSpace();

        ui.addSlider("Noise Attack", createSquared(0, 100, 0, 32000));
        ui.addSlider("Decay Noise", createSquared(0, 100, 1, 8000));
        ui.addSlider("Noise Extend", createLinear(0, 100, 0, 32000));
        if(version > 5) {
            ui.addIntSlider("Noise Extend Factor", -8,64);
        }

        ui.nextColumn();

        ui.addBiquad("LPF Low", 60, 3000);
        ui.addBiquad("HPF Mid", 80, 6000);
        ui.addBiquad("HPF High", 80, 10000);

        ui.addSpace();

        ui.addMixerChannel("Low Out");
        ui.addMixerChannel("Mid Out");
        ui.addMixerChannel("High Out");

        ui.addSpace();

        ui.addBiquad("BPF Noise", 80, 12000);

        ui.addMixerChannel("Noise");

        ui.addSpace();
        ui.addMixerChannel("Main Output");

    }
}
