package com.prokmodular.drums;

import com.prokmodular.ui.ModelUIBuilder;
import com.prokmodular.model.ModelUI;
import controlP5.Button;
import controlP5.CallbackEvent;
import controlP5.ControlP5;
import controlP5.Slider;

import java.util.ArrayList;
import java.util.List;

import static com.prokmodular.model.ParameterMapping.createLinear;
import static com.prokmodular.model.ParameterMapping.createSquared;

public class HiHatUI implements ModelUI {

    enum params {
        BaseFrequency,
        SqFrq1, SqFrq2, SqFrq3, SqFrq4, SqFrq5, SqFrq6,
        SqGain1, SqGain2, SqGain3, SqGain4, SqGain5, SqGain6,
        SrcNoise, NoiseSampleRate,
        BPLow, BPLowQ,
        BPMidHigh, BPMidHighQ,
        BPGainLow, BPGainHi,

        ShpMidHi,
        SquaresAttackTime,

        DcyLow, DcyExtendLow, DcyLowFactor,
        DcyMid, DcyExtendMid, DcyMidFactor,
        DcyHi, DcyExtendHi, DcyHiFactor,

        NoiseAttackTime, DcyNoise, DcyExtendNoise, DcyNoiseFactor,

        HPFLow, HPFLowQ,
        HPFMid, HPFMidQ,
        HPFHi, HPFHiQ,

        MixLow, MixMid, MixHi,
        HPFNoise, HPFNoiseQ,
        MixNoise,

        FlangeOffset, FlangeDepth, FlangeRate, FlangeFeedback, FlangeMix,
        FlangeCutoff, FlangeRes,
        OutLevel,
        NUM_PARAMS
    };

    private final ArrayList<Slider> freqSliders;
    private final ArrayList<Slider> gainSliders;

    private List<Float> frequencyBuffer;
    private Button copyFrqButton;
    private Button pasteFrqButton;

    public HiHatUI() {
        freqSliders = new ArrayList<>();
        gainSliders = new ArrayList<>();

        frequencyBuffer = new ArrayList<>(6);

        frequencyBuffer.add(0,205.3f);
        frequencyBuffer.add(1,304.4f);
        frequencyBuffer.add(2,369.6f);
        frequencyBuffer.add(3,522.7f);
        frequencyBuffer.add(4,540.0f);
        frequencyBuffer.add(5,800.0f);
    }

    public void copyFrequencies() {
        for(int i=0;i<6;i++) {
            frequencyBuffer.set(i,freqSliders.get(i).getValue());
        }
    }

    public void pasteFrequencies() {
        for(int i=0;i<6;i++) {
            freqSliders.get(i).setValue(frequencyBuffer.get(i));
        }
    }

    public void resetTo808() {

        freqSliders.get(0).setValue(205.3f);
        freqSliders.get(1).setValue(304.4f);
        freqSliders.get(2).setValue(369.6f);
        
        freqSliders.get(3).setValue(522.7f);
        freqSliders.get(4).setValue(540.0f);
        freqSliders.get(5).setValue(800.0f);

        for (Slider s : gainSliders) {
            s.setValue(100.0f);
        }

    }

    public void resetToDR110() {

        freqSliders.get(0).setValue(317f);
        freqSliders.get(1).setValue(465f);
        freqSliders.get(2).setValue(820f);
        freqSliders.get(3).setValue(1150f);

        // 5 and 6 are silent.
        freqSliders.get(4).setValue(540.0f);
        freqSliders.get(5).setValue(800.0f);

        for (Slider s : gainSliders) {
            s.setValue(100.0f);
        }

        gainSliders.get(4).setValue(0f);
        gainSliders.get(5).setValue(0f);
    }

    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {

        //ui.addKnob("Base Freq", 0, 1000);
        ui.addSlider("Base Freq", 0, 1000, params.BaseFrequency.ordinal());

        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 1", 30, 5000, params.SqFrq1.ordinal()));
        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 2", 30, 5000, params.SqFrq2.ordinal()));
        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 3", 30, 5000, params.SqFrq3.ordinal()));
        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 4", 30, 5000, params.SqFrq4.ordinal()));
        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 5", 30, 5000, params.SqFrq5.ordinal()));
        freqSliders.add((Slider) ui.addTunableSlider("Square Freq 6", 30, 5000, params.SqFrq6.ordinal()));

        ui.addSpace();

        gainSliders.add((Slider) ui.addSlider("Square Gain 1", createLinear(0, 100, 0, 1), params.SqGain1.ordinal()));
        gainSliders.add((Slider) ui.addSlider("Square Gain 2", createLinear(0, 100, 0, 1), params.SqGain2.ordinal()));
        gainSliders.add((Slider) ui.addSlider("Square Gain 3", createLinear(0, 100, 0, 1), params.SqGain3.ordinal()));
        gainSliders.add((Slider) ui.addSlider("Square Gain 4", createLinear(0, 100, 0, 1), params.SqGain4.ordinal()));
        gainSliders.add((Slider) ui.addSlider("Square Gain 5", createLinear(0, 100, 0, 1), params.SqGain5.ordinal()));
        gainSliders.add((Slider) ui.addSlider("Square Gain 6", createLinear(0, 100, 0, 1), params.SqGain6.ordinal()));

        ui.addSpace();

        ui.addMixerChannel("Source Noise", params.SrcNoise.ordinal());
        ui.addNoiseSampleRate(params.NoiseSampleRate.ordinal());

        ui.nextColumn();

        // 2 bandpass filters
        ui.addBiquad("Band Pass Low", 80, 10000, params.BPLow.ordinal());
        ui.addBiquad("Band Pass Mid/High", 500, 10000, params.BPMidHigh.ordinal());

        ui.addSpace();

        // Band Pass Input Gains
        ui.addSlider("BP Gain Low", createSquared(0, 100, 0, 1), params.BPGainLow.ordinal());
        ui.addSlider("BP Gain Mid/High", createSquared(0, 100, 0, 1), params.BPGainHi.ordinal());

        // 2 shapers
        ui.addSlider("Shaper Mid/High", 1, 100, params.ShpMidHi.ordinal());

        ui.addSpace();

        ui.addSlider("Osc Attack", createSquared(0, 100, 0, 32000), params.SquaresAttackTime.ordinal());

        ui.addSpace();

        // 3 envelopes
        ui.addSlider("Decay Low", createSquared(0, 100, 1, 16000), params.DcyLow.ordinal());
        ui.addSlider("Low Extend", createLinear(0, 100, 0, 32000), params.DcyExtendLow.ordinal());
        if(version > 5) {
            ui.addIntSlider("Low Extend Factor", -8,64, params.DcyLowFactor.ordinal());
        }
        ui.addSlider("Decay Mid", createSquared(0, 100, 1, 16000), params.DcyMid.ordinal());
        ui.addSlider("Mid Extend", createLinear(0, 100, 0, 32000), params.DcyExtendMid.ordinal());
        if(version > 5) {
            ui.addIntSlider("Mid Extend Factor", -8,64, params.DcyMidFactor.ordinal());
        }

        ui.addSlider("Decay High", createSquared(0, 100, 1, 16000), params.DcyHi.ordinal());
        ui.addSlider("High Extend", createLinear(0, 100, 0, 32000), params.DcyExtendHi.ordinal());
        if(version > 5) {
            ui.addIntSlider("High Extend Factor", -8,64, params.DcyHiFactor.ordinal());
        }

        ui.addSpace();

        ui.addSlider("Noise Attack", createSquared(0, 100, 0, 32000), params.NoiseAttackTime.ordinal());
        ui.addSlider("Decay Noise", createSquared(0, 100, 1, 8000), params.DcyNoise.ordinal());
        ui.addSlider("Noise Extend", createLinear(0, 100, 0, 32000), params.DcyExtendNoise.ordinal());
        if(version > 5) {
            ui.addIntSlider("Noise Extend Factor", -8,64, params.DcyNoiseFactor.ordinal());
        }

        ui.nextColumn();

        ui.addBiquad("LPF Low", 60, 3000, params.HPFLow.ordinal());
        ui.addBiquad("HPF Mid", 80, 6000, params.HPFMid.ordinal());
        ui.addBiquad("HPF High", 80, 10000, params.HPFHi.ordinal());

        ui.addSpace();

        ui.addMixerChannel("Low Out", params.MixLow.ordinal());
        ui.addMixerChannel("Mid Out", params.MixMid.ordinal());
        ui.addMixerChannel("High Out", params.MixHi.ordinal());

        ui.addSpace();

        ui.addBiquad("BPF Noise", 80, 12000, params.HPFNoise.ordinal());

        ui.addMixerChannel("Noise", params.MixNoise.ordinal());

        if(version > 6) {
            ui.addSpace();

            ui.addSlider("Flange Offset", createLinear(0, 100, 0, 1), params.FlangeOffset.ordinal());
            ui.addSlider("Flange Depth", createLinear(0, 100, 0, 1), params.FlangeDepth.ordinal());
            ui.addSlider("Flange Rate", createLinear(0, 100, 0, 5), params.FlangeRate.ordinal());
            ui.addSlider("Flange Feedback", createLinear(0, 100, 0, 32767), params.FlangeFeedback.ordinal());
            ui.addSlider("Flange Mix", createLinear(0, 100, 0, 1), params.FlangeMix.ordinal());
        }

        if(version > 7) {
            ui.addStateVariable("Flange Filter", 100,12000, params.FlangeCutoff.ordinal());
        }
        ui.addSpace();
        ui.addMixerChannel("Main Output", params.OutLevel.ordinal());

    }

    public void createExtraUI(ControlP5 cp5) {
        copyFrqButton = cp5.addButton("CopyFrqs")
                .setValue(0)
                .setSize(50, 20)
                .setLabel("Copy Frqs")
                .setPosition(500, 600)
                .setColorBackground(0xFF007700)
                .onRelease((CallbackEvent theEvent) -> {
                    copyFrequencies();
                });

        pasteFrqButton = cp5.addButton("PasteFrqs")
                .setValue(0)
                .setSize(50, 20)
                .setLabel("Paste Frqs")
                .setPosition(500 + 60, 600)
                .setColorBackground(0xFF007777)
                .onRelease((CallbackEvent theEvent) -> {
                    pasteFrequencies();
                });
    }

    public void removeExtraUI(ControlP5 cp5) {
        cp5.remove("CopyFrqs");
        cp5.remove("PasteFrqs");
    }
}
