package com.prokmodular.ui;

import com.prokmodular.model.ParameterMapping;

public interface ModelUIBuilder {
    Object addMixerChannel(String name, int id);

    void addSpace();

    void addStateVariable(String name, int cutoffLow, int cutoffHigh, int id);

    void addNoiseSampleRate(int id);

    Object addSlider(String name, int low, int high, int id);

    Object addIntSlider(String name, int low, int high, int id);

    Object addIntSlider(String name, ParameterMapping mapping, int id);

    Object addSlider(String name, ParameterMapping mapping, int id);

    void nextColumn();

    void addADEnvelope(String name, int id);

    void addBiquad(String name, int cutoffLow, int cutoffHigh, int id);

    void addSineWithEnvelope(String name, int id);

    void addSineRatioWithEnvelope(String name, int id);

    void addTriModWithEnvelope(String name, int id);

    Object addTunableSlider(String name, int low, int high, int id);

    void addSineWithEnvelope(String name, int freqDecaySamples, int id);

    void addShortExpEnv(String name, int id);
}
