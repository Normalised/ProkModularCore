package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;
import controlP5.ControlP5;

import static com.prokmodular.model.ParameterMapping.createLinear;
import static com.prokmodular.model.ParameterMapping.createNone;
import static com.prokmodular.model.ParameterMapping.createSquared;

public class ClapUI implements ModelUI {

    enum params {
        WhiteNoiseMix, SidNoiseMix, PinkNoiseMix,
        WhiteFilterCutoff, WhiteFilterQ,
        SidNoiseFilterCutoff, SidNoiseFilterQ,
        PinkNoiseFilterCutoff, PinkNoiseFilterQ,
        NoiseSampleRate,
        DistortAmount,
        Rolls, RollTime, RollLowLevel, RollHighStart, RollHighStep, RollAttackTime,
        VerbAttackTime, VerbDecayTime, VerbDecayExtendLevel, VerbDecayExtendFactor,
        MainAttackTime, MainDecayTime, MainDecayExtendLevel, MainDecayExtendFactor,
        MainFilterCutoff, MainFilterRes,
        VerbFilterCutoff, VerbFilterRes,
        MainRollOutLevel, MainBodyOutLevel, VerbRollOutLevel, VerbBodyOutLevel,
        LowShelfCutoff, LowShelfGain, LowShelfSlope,
        HighShelfCutoff, HighShelfGain, HighShelfSlope,
        ReverbFilterCutoff,
        ReverbFilterQ,
        DryLevel, WetLevel,
        ReverbDamping, ReverbRoomSize,
        OutAttackTime, OutDecayTime, OutDecayExtendLevel, OutDecayExtendFactor,
        OutLevel,
        NUM_PARAMS
    };

    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {
        ui.addMixerChannel("White Noise", params.WhiteNoiseMix.ordinal());
        ui.addMixerChannel("Sid Noise", params.SidNoiseMix.ordinal());
        ui.addMixerChannel("Pink Noise", params.PinkNoiseMix.ordinal());

        ui.addSpace();

        ui.addStateVariable("White", 50, 5000, params.WhiteFilterCutoff.ordinal());
        ui.addStateVariable("Sid", 50, 5000, params.SidNoiseFilterCutoff.ordinal());
        ui.addStateVariable("Pink", 50, 5000, params.PinkNoiseFilterCutoff.ordinal());

        ui.addSpace();

        ui.addNoiseSampleRate(params.NoiseSampleRate.ordinal());
        ui.addSlider("Distort", 1, 100, params.DistortAmount.ordinal());

        ui.addSpace();

        ui.addIntSlider("Rolls", 1, 10, params.Rolls.ordinal());
        ui.addIntSlider("Roll Time", createSquared(1, 100, 10, 5000), params.RollTime.ordinal());
        ui.addIntSlider("Roll Low", createLinear(0, 50, 0, 16000), params.RollLowLevel.ordinal());
        ui.addSlider("Roll High Start", createLinear(1, 100, 100, 32000), params.RollHighStart.ordinal());
        ui.addIntSlider("Roll High Step", createLinear(1, 100, 10, 8000), params.RollHighStep.ordinal());
        ui.addSlider("Roll Attack", createSquared(0, 100, 0, 8000), params.RollAttackTime.ordinal());

        ui.nextColumn();

        ui.addADEnvelope("Verb", params.VerbAttackTime.ordinal());
        ui.addADEnvelope("Main", params.MainAttackTime.ordinal());

        ui.addSpace();
        ui.addBiquad("Main", 40, 12000, params.MainFilterCutoff.ordinal());
        ui.addSpace();
        ui.addStateVariable("Verb", 40, 8000, params.VerbFilterCutoff.ordinal());
        ui.addSpace();

        ui.addMixerChannel("Main Roll Level", params.MainRollOutLevel.ordinal());
        ui.addMixerChannel("Main Body Level", params.MainBodyOutLevel.ordinal());
        ui.addMixerChannel("Verb Roll Level", params.VerbRollOutLevel.ordinal());
        ui.addMixerChannel("Verb Body Level", params.VerbBodyOutLevel.ordinal());

        ui.nextColumn();

        ui.addSlider("Low Shelf Cutoff", 400, 15000, params.LowShelfCutoff.ordinal());
        ui.addSlider("Low Shelf Gain", 0, -24, params.LowShelfGain.ordinal());
        ui.addSlider("Low Shelf Slope", createLinear(1, 100, 0.1f, 6f), params.LowShelfSlope.ordinal());
        ui.addSpace();

        ui.addSlider("High Shelf Cutoff", 400, 15000, params.HighShelfCutoff.ordinal());
        ui.addSlider("High Shelf Gain", 0, 24, params.HighShelfGain.ordinal());
        ui.addSlider("High Shelf Slope", createLinear(1, 100, 0.1f, 6f), params.HighShelfSlope.ordinal());
        ui.addSpace();

        if(version > 6) {

            ui.addSlider("Reverb Filter Cutoff", createNone(50, 10000), params.ReverbFilterCutoff.ordinal());
            ui.addSlider("Reverb Filter Res", createLinear(0, 100, 0.7f, 2.0f), params.ReverbFilterQ.ordinal());
            ui.addSpace();
        }

        ui.addMixerChannel("Dry Level", params.DryLevel.ordinal());
        ui.addMixerChannel("Wet Level", params.WetLevel.ordinal());

        ui.addSlider("Damping", createLinear(0, 100, 0f, 1f), params.ReverbDamping.ordinal());
        ui.addSlider("Room Size", createLinear(0, 100, 0f, 1f), params.ReverbRoomSize.ordinal());

        if(version > 1) {
            ui.addSpace();
            ui.addADEnvelope("Out Env", params.OutAttackTime.ordinal());
            ui.addSpace();
        }
        ui.addMixerChannel("Main Output", params.OutLevel.ordinal());
    }

    @Override
    public void createExtraUI(ControlP5 cp5) {

    }

    @Override
    public void removeExtraUI(ControlP5 cp5) {

    }
}
