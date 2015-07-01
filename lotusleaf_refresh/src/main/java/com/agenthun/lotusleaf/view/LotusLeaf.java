package com.agenthun.lotusleaf.view;

/**
 * Created by Agent Henry on 2015/5/2.
 */


public class LotusLeaf {

    enum ShapeType {
        SHAPE_LITTLE,
        SHAPE_BIG
    }

    enum AmplitudeType {
        AMPLITUDE_LITTLE,
        AMPLITUDE_MIDDLE,
        AMPLITUDE_BIG
    }

    float x, y;
    ShapeType shapeType;
    AmplitudeType amplitudeType;
    int rotateAngle;
    int rotateDirection;
    long startTime;
}
