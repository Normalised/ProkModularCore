package com.prokmodular.ui;

import com.prokmodular.model.ParameterMapping;

public interface ModelUIBuilder {
    Object addMixerChannel(String name);

    void addSpace();

    void addStateVariable(String name, int cutoffLow, int cutoffHigh);

    void addNoiseSampleRate();

    Object addSlider(String name, int low, int high);

    Object addIntSlider(String name, int low, int high);

    Object addIntSlider(String name, ParameterMapping mapping);

    Object addSlider(String name, ParameterMapping mapping);

    void nextColumn();

    void addADEnvelope(String name);

    void addBiquad(String name, int cutoffLow, int cutoffHigh);

    void addSineWithEnvelope(String name);

    void addTriModWithEnvelope(String name);

    Object addTunableSlider(String name, int low, int high);

    void addSineWithEnvelope(String name, int freqDecaySamples);

    void addShortExpEnv(String name);
}
