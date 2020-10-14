package com.prokmodular.drums;

import com.prokmodular.model.ModelUI;
import com.prokmodular.ui.ModelUIBuilder;
import controlP5.ControlP5;

public class KickUI implements ModelUI {

    enum params3 {
        SineAFreq, SineAFrqAttackTime, SineAFrqDecayTime, SineAFrqEnvAmount, SineAFrqDecayExtendLevel, SineAFrqDecayExtendFactor,
        SineBFreq, SineBFrqAttackTime, SineBFrqDecayTime, SineBFrqEnvAmount, SineBFrqDecayExtendLevel, SineBFrqDecayExtendFactor,

        AmpAAttackTime, AmpADecayTime, AmpADecayExtendLevel, AmpADecayExtendFactor,
        AmpBAttackTime, AmpBDecayTime, AmpBDecayExtendLevel, AmpBDecayExtendFactor,

        TriAFreq, TriAPulseWidth, TriAFrqAttackTime, TriAFrqDecayTime, TriAFrqEnvAmount, TriAFrqDecayExtendLevel, TriAFrqDecayExtendFactor,
        TriBFreq, TriBPulseWidth, TriBFrqAttackTime, TriBFrqDecayTime, TriBFrqEnvAmount, TriBFrqDecayExtendLevel, TriBFrqDecayExtendFactor,

        TriAAttackTime, TriADecayTime, TriADecayExtendLevel, TriADecayExtendFactor,
        TriBAttackTime, TriBDecayTime, TriBDecayExtendLevel, TriBDecayExtendFactor,

        OscCutoff, OscQ, OscDistort,

        NoiseCutoff, NoiseQ, NoiseAttackTime, NoiseDecayTime, NoiseDecayExtendLevel, NoiseDecayExtendFactor,
        SineAOutMix, SineBOutMix, TriAOutMix, TriBOutMix,
        OscOutMix, NoiseOutMix, TransientMix,
        DistortAmount,
        OutAttackTime, OutDecayTime, OutDecayExtendLevel, OutDecayExtendFactor,
        OutLevel,
        NUM_PARAMS
    };

    enum params5 {
        SineAFreq, SineAFrqAttackTime, SineAFrqDecayTime, SineAFrqEnvAmount, SineAFrqDecayExtendLevel, SineAFrqDecayExtendFactor,
        SineBFreq, SineBFrqAttackTime, SineBFrqDecayTime, SineBFrqEnvAmount, SineBFrqDecayExtendLevel, SineBFrqDecayExtendFactor,

        AmpAAttackTime, AmpADecayTime, AmpADecayExtendLevel, AmpADecayExtendFactor,
        AmpBAttackTime, AmpBDecayTime, AmpBDecayExtendLevel, AmpBDecayExtendFactor,

        TriAFreq, TriAPulseWidth, TriAFrqAttackTime, TriAFrqDecayTime, TriAFrqEnvAmount, TriAFrqDecayExtendLevel, TriAFrqDecayExtendFactor,
        TriBFreq, TriBPulseWidth, TriBFrqAttackTime, TriBFrqDecayTime, TriBFrqEnvAmount, TriBFrqDecayExtendLevel, TriBFrqDecayExtendFactor,

        TriAAttackTime, TriADecayTime, TriADecayExtendLevel, TriADecayExtendFactor,
        TriBAttackTime, TriBDecayTime, TriBDecayExtendLevel, TriBDecayExtendFactor,

        TriAPhaseModAmount, TriBPhaseModAmount,

        OscCutoff, OscQ, OscDistort,

        NoiseCutoff, NoiseQ, NoiseAttackTime, NoiseDecayTime, NoiseDecayExtendLevel, NoiseDecayExtendFactor,
        SineAOutMix, SineBOutMix, TriAOutMix, TriBOutMix,
        OscOutMix, NoiseOutMix, TransientMix,
        DistortAmount,
        OutAttackTime, OutDecayTime, OutDecayExtendLevel, OutDecayExtendFactor,
        OutLevel,
        NUM_PARAMS
    };

    @Override
    public void createUI(ModelUIBuilder ui, int firmwareVersion, int version) {

        if(version > 3) {
            ui.addSineWithEnvelope("Sine A", params5.SineAFreq.ordinal());
            ui.addSpace();

            ui.addSineWithEnvelope("Sine B", params5.SineBFreq.ordinal());
            ui.addSpace();

            ui.addADEnvelope("Amp A", params5.AmpAAttackTime.ordinal());
            ui.addSpace();

            ui.addADEnvelope("Amp B", params5.AmpBAttackTime.ordinal());
            ui.nextColumn();

            ui.addTriModWithEnvelope("Tri A", params5.TriAFreq.ordinal());
            ui.addSpace();

            ui.addTriModWithEnvelope("Tri B", params5.TriBFreq.ordinal());
            ui.addSpace();

            ui.addADEnvelope("Tri Amp A", params5.TriAAttackTime.ordinal());
            ui.addSpace();

            ui.addADEnvelope("Tri Amp B", params5.TriBAttackTime.ordinal());
            ui.nextColumn();

            ui.addSlider("Tri A Phase Mod", 0,10000, params5.TriAPhaseModAmount.ordinal());
            ui.addSlider("Tri B Phase Mod", 0,10000, params5.TriBPhaseModAmount.ordinal());

            ui.addBiquad("OSC", 50, 6500, params5.OscCutoff.ordinal());
            ui.addSlider("OSC Distort", 0, 100, params5.OscDistort.ordinal());

            ui.addSpace();

            ui.addBiquad("Noise", 50, 3500, params5.NoiseCutoff.ordinal());

            ui.addSpace();

            ui.addADEnvelope("Noise", params5.NoiseAttackTime.ordinal());

            ui.addMixerChannel("Sin A", params5.SineAOutMix.ordinal());
            ui.addMixerChannel("Sin B", params5.SineBOutMix.ordinal());
            ui.addMixerChannel("Tri A", params5.TriAOutMix.ordinal());
            ui.addMixerChannel("Tri B", params5.TriBOutMix.ordinal());

            ui.addSpace();

            ui.addMixerChannel("OSCs", params5.OscOutMix.ordinal());
            ui.addMixerChannel("Noise", params5.NoiseOutMix.ordinal());
            ui.addMixerChannel("Transient", params5.TransientMix.ordinal());
            ui.addSlider("Distort", 1, 100, params5.DistortAmount.ordinal());
            ui.addADEnvelope("Out", params5.OutAttackTime.ordinal());
            ui.addMixerChannel("Main Output", params5.OutLevel.ordinal());

        } else {
            ui.addSineWithEnvelope("Sine A", params3.SineAFreq.ordinal());
            ui.addSpace();

            ui.addSineWithEnvelope("Sine B", params3.SineBFreq.ordinal());
            ui.addSpace();

            ui.addADEnvelope("Amp A", params3.AmpAAttackTime.ordinal());
            ui.addSpace();

            ui.addADEnvelope("Amp B", params3.AmpBAttackTime.ordinal());
            ui.nextColumn();

            ui.addTriModWithEnvelope("Tri A", params3.TriAFreq.ordinal());
            ui.addSpace();

            ui.addTriModWithEnvelope("Tri B", params3.TriBFreq.ordinal());
            ui.addSpace();

            ui.addADEnvelope("Tri Amp A", params3.TriAAttackTime.ordinal());
            ui.addSpace();

            ui.addADEnvelope("Tri Amp B", params3.TriBAttackTime.ordinal());
            ui.nextColumn();

            ui.addBiquad("OSC", 50, 6500, params3.OscCutoff.ordinal());
            ui.addSlider("OSC Distort", 0, 100, params3.OscDistort.ordinal());

            ui.addSpace();

            ui.addBiquad("Noise", 50, 3500, params3.NoiseCutoff.ordinal());

            ui.addSpace();

            ui.addADEnvelope("Noise", params3.NoiseAttackTime.ordinal());

            ui.addMixerChannel("Sin A", params3.SineAOutMix.ordinal());
            ui.addMixerChannel("Sin B", params3.SineBOutMix.ordinal());
            ui.addMixerChannel("Tri A", params3.TriAOutMix.ordinal());
            ui.addMixerChannel("Tri B", params3.TriBOutMix.ordinal());

            ui.addSpace();

            ui.addMixerChannel("OSCs", params3.OscOutMix.ordinal());
            ui.addMixerChannel("Noise", params3.NoiseOutMix.ordinal());
            ui.addMixerChannel("Transient", params3.TransientMix.ordinal());
            ui.addSlider("Distort", 1, 100, params3.DistortAmount.ordinal());
            ui.addADEnvelope("Out", params3.OutAttackTime.ordinal());
            ui.addMixerChannel("Main Output", params3.OutLevel.ordinal());

        }

    }

    @Override
    public void createExtraUI(ControlP5 cp5) {

    }

    @Override
    public void removeExtraUI(ControlP5 cp5) {

    }
}
