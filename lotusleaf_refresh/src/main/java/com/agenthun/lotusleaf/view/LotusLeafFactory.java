package com.agenthun.lotusleaf.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Agent Henry on 2015/5/2.
 */
public class LotusLeafFactory extends LotusLeaf {
    private static final int MAX_LEAFS = 8;
    private static final long LEAF_FLOAT_TIME = 5000;

    Random random = new Random();
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    private int mAddTime;

    public LotusLeaf generateLotusLeaf() {
        LotusLeaf lotusLeaf = new LotusLeaf();

        lotusLeaf.shapeType = random.nextInt(2) == 1 ? ShapeType.SHAPE_BIG : ShapeType.SHAPE_LITTLE;

        int randomType = random.nextInt(3);
        AmplitudeType mAmplitudeType = AmplitudeType.AMPLITUDE_MIDDLE;
        switch (randomType) {
            case 0:
                break;
            case 1:
                mAmplitudeType = AmplitudeType.AMPLITUDE_LITTLE;
                break;
            case 2:
                mAmplitudeType = AmplitudeType.AMPLITUDE_BIG;
                break;
            default:
                break;
        }
        lotusLeaf.amplitudeType = mAmplitudeType;

        lotusLeaf.rotateAngle = random.nextInt(360);
        lotusLeaf.rotateDirection = random.nextInt(2);

        //mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        mAddTime = random.nextInt((int) (mLeafFloatTime));
        lotusLeaf.startTime = System.currentTimeMillis() + mAddTime;

        return lotusLeaf;
    }

    public List<LotusLeaf> generateLotusLeafs() {
        return generateLotusLeafs(MAX_LEAFS);
    }

    public List<LotusLeaf> generateLotusLeafs(int lotusLeafSize) {
        List<LotusLeaf> lotusLeafs = new ArrayList<LotusLeaf>();
        for (int i = 0; i < lotusLeafSize; i++) {
            lotusLeafs.add(generateLotusLeaf());
        }
        return lotusLeafs;
    }
}
