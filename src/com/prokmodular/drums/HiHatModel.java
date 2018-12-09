package com.prokmodular.drums;

import com.prokmodular.model.ModelConfig;
import com.prokmodular.model.ProkModel;

import java.util.ArrayList;
import java.util.List;

public class HiHatModel implements ProkModel {

//    private final ArrayList<Slider> freqSliders;
//    private final ArrayList<Slider> gainSliders;

    private List<Float> frequencyBuffer;

    public HiHatModel() {
//        freqSliders = new ArrayList<>();
//        gainSliders = new ArrayList<>();

        frequencyBuffer = new ArrayList<>(6);

        frequencyBuffer.add(0,205.3f);
        frequencyBuffer.add(1,304.4f);
        frequencyBuffer.add(2,369.6f);
        frequencyBuffer.add(3,522.7f);
        frequencyBuffer.add(4,540.0f);
        frequencyBuffer.add(5,800.0f);
    }

    public void copyFrequencies() {
//        for(int i=0;i<6;i++) {
//            frequencyBuffer.set(i,freqSliders.get(i).getValue());
//        }
    }

    public void pasteFrequencies() {
//        for(int i=0;i<6;i++) {
//            freqSliders.get(i).setValue(frequencyBuffer.get(i));
//        }
    }

    public void resetTo808() {

//        freqSliders.get(0).setValue(205.3f);
//        freqSliders.get(1).setValue(304.4f);
//        freqSliders.get(2).setValue(369.6f);
//        freqSliders.get(3).setValue(522.7f);
//        freqSliders.get(4).setValue(540.0f);
//        freqSliders.get(5).setValue(800.0f);
//
//        for (Slider s : gainSliders) {
//            s.setValue(100.0f);
//        }

    }

    public void resetToDR110() {

//        freqSliders.get(0).setValue(317f);
//        freqSliders.get(1).setValue(465f);
//        freqSliders.get(2).setValue(820f);
//        freqSliders.get(3).setValue(1150f);
//
//        // 5 and 6 are silent.
//        freqSliders.get(4).setValue(540.0f);
//        freqSliders.get(5).setValue(800.0f);
//
//        for (Slider s : gainSliders) {
//            s.setValue(100.0f);
//        }
//
//        gainSliders.get(4).setValue(0f);
//        gainSliders.get(5).setValue(0f);
    }

    @Override
    public ModelConfig getConfig() {
        ModelConfig config = new ModelConfig();

        config.filename = "hihat";
        config.hello = "hihat";
        config.version = 5;

        return config;
    }

}
