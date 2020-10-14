package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;
import controlP5.ControlP5;

import static com.prokmodular.model.ParameterMapping.createLinear;
import static com.prokmodular.model.ParameterMapping.createNone;
import static com.prokmodular.model.ParameterMapping.createSquared;

public class SnareUI implements ModelUI {

    enum params {

        SineAFreq,
        SineAFrqAttackTime,
        SineAFrqDecayTime,
        SineAFrqEnvAmount,
        SineAFrqDecayExtendLevel,
        SineAFrqDecayExtendFactor,

        SineBFreq,
        SineBFrqAttackTime,
        SineBFrqDecayTime,
        SineBFrqEnvAmount,
        SineBFrqDecayExtendLevel,
        SineBFrqDecayExtendFactor,

        SineAAmpAttackTime,
        SineAAmpDecayTime,
        SineAAmpDecayExtendLevel,
        SineAAmpDecayExtendFactor,

        SineBAmpAttackTime,
        SineBAmpDecayTime,
        SineBAmpDecayExtendLevel,
        SineBAmpDecayExtendFactor,

        SidNoiseClockRate,

        FilterACutoff,
        FilterAQ,

        FilterBCutoff,
        FilterBQ,

        SidNoiseLPFAttackTime,
        SidNoiseLPFDecayTime,
        SidNoiseLPFDecayExtendLevel,
        SidNoiseLPFDecayExtendFactor,

        SidNoiseHPFAttackTime,
        SidNoiseHPFDecayTime,
        SidNoiseHPFDecayExtendLevel,
        SidNoiseHPFDecayExtendFactor,

        SidNoiseLPFMix,
        SidNoiseHPFMix,

        SineAMix,
        SineBMix,

        BodyCutoff,
        BodyQ,

        BodyMix,
        NoiseMix,
        TransientMix,

        DistortAmount,
        DistortAmount2,

        DelayFeedbackLevel1,
        DelayFeedbackLevel2,
        DelayTime1,
        DelayTime2,

        DelayLevel1,
        DelayLevel2,

        DelayFeedback1FilterCutoff,
        DelayFeedback1FilterQ,

        DelayFeedback2FilterCutoff,
        DelayFeedback2FilterQ,

        OutAttackTime, OutDecayTime, OutDecayExtendLevel, OutDecayExtendFactor,

        OutLevel,

        NUM_PARAMS
    };

    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {
        ui.addSineWithEnvelope("Sine A", 32500, params.SineAFreq.ordinal());
        ui.addSpace();
        ui.addSineWithEnvelope("Sine B", 32500, params.SineBFreq.ordinal());
        ui.addSpace();

        ui.addADEnvelope("Amp A", params.SineAAmpAttackTime.ordinal());
        ui.addSpace();

        ui.addADEnvelope("Amp B", params.SineBAmpAttackTime.ordinal());

        ui.nextColumn();

        ui.addNoiseSampleRate(params.SidNoiseClockRate.ordinal());

        ui.addStateVariable("Filter A", 50, 10000, params.FilterACutoff.ordinal());
        ui.addStateVariable("Filter B", 50, 10000, params.FilterBCutoff.ordinal());

        ui.addSpace();

        ui.addShortExpEnv("Sid Noise LPF", params.SidNoiseLPFAttackTime.ordinal());

        ui.addSpace();

        ui.addShortExpEnv("Sid Noise HPF", params.SidNoiseHPFAttackTime.ordinal());

        ui.addSpace();

        ui.addMixerChannel("Sid Noise LPF", params.SidNoiseLPFMix.ordinal());
        ui.addMixerChannel("Sid Noise HPF", params.SidNoiseHPFMix.ordinal());

        ui.addSpace();

        ui.addMixerChannel("Sine A", params.SineAMix.ordinal());
        ui.addMixerChannel("Sine B", params.SineBMix.ordinal());

        ui.nextColumn();

        ui.addBiquad("Body", 50, 5000, params.BodyCutoff.ordinal());

        ui.addSpace();

        ui.addMixerChannel("Body", params.BodyMix.ordinal());
        ui.addMixerChannel("Noise", params.NoiseMix.ordinal());
        ui.addMixerChannel("Transient", params.TransientMix.ordinal());

        ui.addSpace();

        if(version == 1) {
            ui.addMixerChannel("Output Level", params.OutLevel.ordinal());

            ui.addSlider("Distort",1,100, params.DistortAmount.ordinal());
            ui.addSlider("Distort2",1,100, params.DistortAmount2.ordinal());

        } else {

            ui.addSlider("Distort",1,100, params.DistortAmount.ordinal());
            ui.addSlider("Distort2",1,100, params.DistortAmount2.ordinal());

            ui.addSpace();

            ui.addSlider("Feedback A",createLinear(0,100, 0, 32767), params.DelayFeedbackLevel1.ordinal());
            ui.addSlider("Feedback B",createLinear(0,100, 0, 32767), params.DelayFeedbackLevel2.ordinal());

            ui.addSlider("Time A",1,200, params.DelayTime1.ordinal());
            ui.addSlider("Time B", 1,200, params.DelayTime2.ordinal());

            ui.addMixerChannel("Delay Level A", params.DelayLevel1.ordinal());
            ui.addMixerChannel("Delay Level B", params.DelayLevel2.ordinal());
            ui.addSpace();

            ui.addSlider("Feedback Filter A Cutoff", createNone(50, 10000), params.DelayFeedback1FilterCutoff.ordinal());
            ui.addSlider("Feedback Filter A Res", createLinear(0, 100, 0.7f, 2.0f), params.DelayFeedback1FilterQ.ordinal());
            ui.addSlider("Feedback Filter B Cutoff", createNone(40, 4000), params.DelayFeedback2FilterCutoff.ordinal());
            ui.addSlider("Feedback Filter B Res", createLinear(0, 100, 0.7f, 2.0f), params.DelayFeedback2FilterQ.ordinal());

            ui.addSpace();
            ui.addADEnvelope("Out Env", params.OutAttackTime.ordinal());
            ui.addSpace();
            ui.addMixerChannel("Main Output", params.OutLevel.ordinal());
        }

    }

    @Override
    public void createExtraUI(ControlP5 cp5) {

    }

    @Override
    public void removeExtraUI(ControlP5 cp5) {

    }
}
