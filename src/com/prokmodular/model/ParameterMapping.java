package com.prokmodular.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by martin on 28/07/2017 at 14:26
 */

// None means what comes in goes out
enum ParameterMappingType {
    NONE, LINEAR, SQUARE
}

public class ParameterMapping {

    final Logger logger = LoggerFactory.getLogger(ParameterMapping.class);
    private ParameterMappingType type = ParameterMappingType.NONE;

    public float inLow = 0;
    public float inHigh = 100.0f;

    public float outLow = 0;
    public float outHigh = 100.0f;

    private float inRange;
    private float outRange;

    public static ParameterMapping createNone(float inLow, float inHigh) {
        return new ParameterMapping(inLow, inHigh);
    }

    public static ParameterMapping createLinear(float inLow, float inHigh, float outLow, float outHigh) {
        return new ParameterMapping(inLow, inHigh, outLow, outHigh, ParameterMappingType.LINEAR);
    }

    public static ParameterMapping createSquared(float inLow, float inHigh, float outLow, float outHigh) {
        return new ParameterMapping(inLow, inHigh, outLow, outHigh, ParameterMappingType.SQUARE);
    }

    ParameterMapping(float inLow, float inHigh) {
        // Type will be NONE and there is no way to change it.
        this.inLow = inLow;
        this.inHigh = inHigh;
        this.inRange = inHigh - inLow;
    }

    ParameterMapping(float inLow, float inHigh, float outLow, float outHigh, ParameterMappingType mappingType) {

        type = mappingType;

        this.inLow = inLow;
        this.inHigh = inHigh;
        this.outLow = outLow;
        this.outHigh = outHigh;

        inRange = inHigh - inLow;
        outRange = outHigh - outLow;
    }

    public void setRange() {

    }

    public float toModule(float in) {
        if(type == ParameterMappingType.NONE) {
            return in;
        }

        if(in < inLow) in = inLow;
        if(in > inHigh) in = inHigh;

        float dIn = (in - inLow) / inRange;

        if(type == ParameterMappingType.LINEAR) {
            return outLow + (outRange * dIn);
        } else if(type == ParameterMappingType.SQUARE) {
            float dInSquared = dIn * dIn;
            return outLow + (outRange * dInSquared);
        }

        return in;
    }

    public float fromModule(float val) {
        if(type == ParameterMappingType.NONE) {
            return val;
        }

        if(val < outLow) val = outLow;
        if(val > outHigh) val = outHigh;

        float dv = (val - outLow) / outRange;

        if(type == ParameterMappingType.LINEAR) {
            val = inLow + (inRange * dv);
        } else if(type == ParameterMappingType.SQUARE) {
            double dvsqrt = Math.sqrt(dv);
            val = (float) (inLow + (inRange * dvsqrt));
        }

        if(val < inLow) {
            logger.debug("Mapped param underflow " + val + " : " + inLow);
        }
        if(val > inHigh) {
            logger.debug("Mapped param overflow " + val + " : " + inHigh);
        }
        return val;
    }

    public int getInputRange() {
        return (int) Math.floor(inHigh - inLow);
    }
}
