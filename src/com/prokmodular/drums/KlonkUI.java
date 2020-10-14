package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;
import controlP5.ControlP5;

import static com.prokmodular.model.ParameterMapping.createLinear;

public class KlonkUI implements ModelUI {

    enum params {

        SineAFreq,
        SineAFrqAttackTime,
        SineAFrqDecayTime,
        SineAFrqEnvAmount,
        SineAFrqDecayExtendLevel,
        SineAFrqDecayExtendFactor,

        SineBFreqRatio,
        SineBFrqAttackTime,
        SineBFrqDecayTime,
        SineBFrqEnvAmount,
        SineBFrqDecayExtendLevel,
        SineBFrqDecayExtendFactor,

        SineCFreqRatio,
        SineCFrqAttackTime,
        SineCFrqDecayTime,
        SineCFrqEnvAmount,
        SineCFrqDecayExtendLevel,
        SineCFrqDecayExtendFactor,

        SineAAmpAttackTime,
        SineAAmpDecayTime,
        SineAAmpDecayExtendLevel,
        SineAAmpDecayExtendFactor,

        SineBAmpAttackTime,
        SineBAmpDecayTime,
        SineBAmpDecayExtendLevel,
        SineBAmpDecayExtendFactor,

        SineCAmpAttackTime,
        SineCAmpDecayTime,
        SineCAmpDecayExtendLevel,
        SineCAmpDecayExtendFactor,

        SineCPhaseModAmount,

        SineAMix,
        SineBMix,
        SineCMix,

        BodyFilterCutoff,
        BodyFilterQ,

        NoiseSampleRate,
        NoiseCutoff,
        NoiseQ,
        NoiseAttackTime,
        NoiseDecayTime,
        NoiseDecayExtendLevel,
        NoiseDecayExtendFactor,

        SineABReverbSendLevel,
        SineBodyReverbSendLevel,
        NoiseReverbSendLevel,

        ReverbDamping,
        ReverbRoomSize,

        ReverbAttackTime,
        ReverbDecayTime,
        ReverbDecayExtendLevel,
        ReverbDecayExtendFactor,

        BodyFilterMix,
        TransientMix,
        NoiseMix,
        ReverbMix,

        PreDistortAttackTime,
        PreDistortDecayTime,
        PreDistortDecayExtendLevel,
        PreDistortDecayExtendFactor,

        DistortAmount,

        DistortFilterCutoff,
        DistortFilterQ,

        DistortFilterMix,
        DryMix,
        OutputReverbMix,

        OutLevel,

        ReverbFilterCutoff,
        ReverbFilterQ,

        NUM_PARAMS
    };

    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {
        ui.addSineWithEnvelope("Sine A", params.SineAFreq.ordinal());
        ui.addSpace();
        ui.addSineRatioWithEnvelope("Sine B",params.SineBFreqRatio.ordinal());
        ui.addSpace();
        ui.addSineRatioWithEnvelope("Sine C",params.SineCFreqRatio.ordinal());
        ui.addSpace();
        ui.addADEnvelope("Amp A", params.SineAAmpAttackTime.ordinal());

        ui.nextColumn();

        ui.addADEnvelope("Amp B", params.SineBAmpAttackTime.ordinal());
        ui.addSpace();
        ui.addADEnvelope("Amp C", params.SineCAmpAttackTime.ordinal());
        ui.addSlider("Tri A Phase Mod", 0,10000, params.SineCPhaseModAmount.ordinal());
        ui.addSpace();

        ui.addMixerChannel("Sin A", params.SineAMix.ordinal());
        ui.addMixerChannel("Sin B", params.SineBMix.ordinal());
        ui.addMixerChannel("Sin C", params.SineCMix.ordinal());
        ui.addSpace();
        ui.addBiquad("Body Filter", 50, 6500, params.BodyFilterCutoff.ordinal());
        ui.addSpace();

        ui.addNoiseSampleRate(params.NoiseSampleRate.ordinal());
        ui.addBiquad("Noise Filter", 50, 6500, params.NoiseCutoff.ordinal());
        ui.addADEnvelope("Noise Envelope", params.NoiseAttackTime.ordinal());

        ui.nextColumn();

        ui.addMixerChannel("AB Send", params.SineABReverbSendLevel.ordinal());
        ui.addMixerChannel("Body Send", params.SineBodyReverbSendLevel.ordinal());
        ui.addMixerChannel("Noise Send", params.NoiseReverbSendLevel.ordinal());
        ui.addSlider("Damping", createLinear(0, 100, 0f, 1f), params.ReverbDamping.ordinal());
        ui.addSlider("Room Size", createLinear(0, 100, 0f, 1f), params.ReverbRoomSize.ordinal());
        ui.addADEnvelope("Reverb Envelope", params.ReverbAttackTime.ordinal());
        ui.addBiquad("Reverb Filter", 50, 6500, params.ReverbFilterCutoff.ordinal());
        ui.addSpace();
        ui.addMixerChannel("Body Mix", params.BodyFilterMix.ordinal());
        ui.addMixerChannel("Transient Mix", params.TransientMix.ordinal());
        ui.addMixerChannel("Noise Mix", params.NoiseMix.ordinal());
        ui.addMixerChannel("Reverb Mix", params.ReverbMix.ordinal());
        ui.addSpace();

        ui.addADEnvelope("Distort Env", params.PreDistortAttackTime.ordinal());
        ui.addSlider("Distort", 0, 100, params.DistortAmount.ordinal());
        ui.addBiquad("Distort Filter", 50, 3500, params.DistortFilterCutoff.ordinal());
        ui.addMixerChannel("Distort Mix", params.DistortFilterMix.ordinal());
        ui.addMixerChannel("Dry Mix", params.DryMix.ordinal());
        ui.addMixerChannel("Reverb Direct", params.OutputReverbMix.ordinal());

        ui.addSpace();

        ui.addMixerChannel("Main Output", params.OutLevel.ordinal());

    }

    @Override
    public void createExtraUI(ControlP5 cp5) {

    }

    @Override
    public void removeExtraUI(ControlP5 cp5) {

    }
}
