package com.agenthun.lotusleaf.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.agenthun.lotusleaf.R;
import com.agenthun.lotusleaf.UiUtils;

import java.util.List;
import java.util.Random;

/**
 * Created by Agent Henry on 2015/5/2.
 */
public class LotusLeafSunDrawable extends Drawable implements Animatable {

    private static final LinearInterpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final int ANIMATION_DURATION = 1000;
    private static final float SCALE_START_PERCENT = 0.3f;
    private static final float SUN_FINAL_SCALE = 0.85f;
    private static final float SUN_INITIAL_ROTATE_GROWTH = 1.2f;
    private static final float SUN_FINAL_ROTATE_GROWTH = 1.5f;

    private static final float LOTUSLEAF_RATIO = 0.32f;
    private static final int MIDDLE_AMPLITUDE = 40;
    private static final int AMPLITUDE_DISPARITY = 60;
    private static final int AMPLITUDE_POWER_FACTOR = 500;
    private static final long LEAF_FLOAT_TIME = 9000;
    private static final long LEAF_ROTATE_TIME = 8000;

    private static final float POND_RATIO = 0.5f;
    private static final float POND_INITIAL_SCALE = 1.2f;
    private static final float POND_FINAL_SCALE = 1.3f;

    private View mParent;
    private Matrix mMatrix;
    private Animation mAnimation;

    private int mTop;
    private int mScreenWidth;

    private Bitmap mSun;
    private int mSunSize = 100;
    private float mSunLeftOffset;
    private float mSunTopOffset;

    private Bitmap mLotusLeafLittle;
    private Bitmap mLotusLeafBig;
    private int mLotusLeafLittleWidth = 74;
    private int mLotusLeafLittleHeight = 50;
    private int mLotusLeafBigWidth = 87;
    private int mLotusLeafBigHeight = 78;
    private LotusLeafFactory mLotusLeafFactory;
    private List<LotusLeaf> mLotusLeafs;
    private float mLotusLeafLeftOffset;
    private float mLotusLeafTopOffset;

    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;
    private int mAmplitudePowerFactor = AMPLITUDE_POWER_FACTOR;
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    private long mLeafRotateTime = LEAF_ROTATE_TIME;
    private float mLocationOffsetY;

    private Bitmap mPond;
    private int mPondLength;
    private float mPondInitialTopOffset;
    private float mPondFinalTopOffset;
    private float mPondMoveOffset;

    private float mPercent = 0.0f;
    private float mRotate = 0.0f;

    private boolean isRefreshing = false;

    private Context mContext;
    private int mTotalDragDistance;

    public LotusLeafSunDrawable(Context context, View parent) {
        mContext = context;
        mParent = parent;

        mMatrix = new Matrix();

        initiateDimens();
        createBitmaps();
        mLotusLeafFactory = new LotusLeafFactory();
        mLotusLeafs = mLotusLeafFactory.generateLotusLeafs();
        setupAnimations();
    }

    private Context getContext() {
        return mContext;
    }

    private void initiateDimens() {
        mScreenWidth = UiUtils.getScreenWidthPixels(mContext);
        mTotalDragDistance = UiUtils.dipToPx(mContext, 190);

        mSunLeftOffset = 0.25f * (float) mScreenWidth;
        mSunTopOffset = mTotalDragDistance * 0.35f;

        mLotusLeafLeftOffset = 0.61f * (float) mScreenWidth;
        mLotusLeafTopOffset = LOTUSLEAF_RATIO * mTotalDragDistance;

        mPondLength = (int) (POND_RATIO * mScreenWidth);
        mPondInitialTopOffset = (mTotalDragDistance - mPondLength * POND_INITIAL_SCALE) + mTotalDragDistance * 0.42f;
        mPondFinalTopOffset = (mTotalDragDistance - mPondLength * POND_FINAL_SCALE) + mTotalDragDistance * 0.42f;
        mPondMoveOffset = UiUtils.dipToPx(mContext, 10);

        mTop = 0;
    }

    private void createBitmaps() {
        mSun = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.sun);
        mSun = Bitmap.createScaledBitmap(mSun, mSunSize, mSunSize, true);

        mLotusLeafLittle = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.lotus_leaf_little);
        mLotusLeafLittle = Bitmap.createScaledBitmap(mLotusLeafLittle, mLotusLeafLittleWidth, mLotusLeafLittleHeight, true);

        mLotusLeafBig = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.lotus_leaf_big);
        mLotusLeafBig = Bitmap.createScaledBitmap(mLotusLeafBig, mLotusLeafBigWidth, mLotusLeafBigHeight, true);

        mPond = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.background);
        mPond = Bitmap.createScaledBitmap(mPond, mScreenWidth, (int) (mScreenWidth * POND_RATIO), true);
    }

    public void offsetTopAndBottom(int offset) {
        mTop = offset;
        invalidateSelf();
    }

    private void drawSun(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = mPercent;
        if (dragPercent > 1.0f) {
            dragPercent = (dragPercent + 9.0f) / 10;
        }

        float sunRadius = (float) mSunSize / 2.0f;
        float sunRotateGrowth = SUN_INITIAL_ROTATE_GROWTH;

        float offsetX = mSunLeftOffset;
        float offsetY = mSunTopOffset + (mTotalDragDistance / 5) * (1.0f - dragPercent);

        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            float sunScale = 1.0f - (1.0f - SUN_FINAL_SCALE) * scalePercent;
            sunRotateGrowth += (SUN_FINAL_ROTATE_GROWTH - SUN_INITIAL_ROTATE_GROWTH) * scalePercent;

            matrix.preTranslate(offsetX + (sunRadius - sunRadius * sunScale), offsetY * (2.0f - sunScale));
            matrix.preScale(sunScale, sunScale);
            offsetX += sunRadius;
            offsetY = offsetY * (2.0f - sunScale) + sunRadius * sunScale;
        } else {
            matrix.postTranslate(offsetX, offsetY);
            offsetX += sunRadius;
            offsetY += sunRadius;
        }

        float r = (isRefreshing ? -360 : 360) * mRotate * (isRefreshing ? 1 : sunRotateGrowth);
        matrix.postRotate(r, offsetX, offsetY);

        canvas.drawBitmap(mSun, matrix, null);
    }

    private void drawLotusLeaf(Canvas canvas) {
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();

        float dragPercent = Math.min(1f, Math.abs(mPercent));

        float offsetX;
        float offsetY;
        Matrix matrix = mMatrix;

        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;

        mLocationOffsetY = mTotalDragDistance / 2.1f;
/*        for (LotusLeaf lotusLeaf : mLotusLeafs) {
            matrix.reset();

            float rotateFraction = ((currentTime - lotusLeaf.startTime) % mLeafRotateTime) / (float) mLeafRotateTime;
            int angle = (int) (rotateFraction * 360);
            getLeafLocation(lotusLeaf, currentTime);

            offsetX = mLotusLeafLeftOffset + lotusLeaf.x;
            offsetY = mLotusLeafTopOffset + lotusLeaf.y + (mTotalDragDistance / 2) * dragPercent;
            matrix.postTranslate(offsetX, offsetY);

            float r = lotusLeaf.rotateDirection == 0 ? (angle + lotusLeaf.rotateAngle) : (-angle + lotusLeaf.rotateAngle);
            matrix.postRotate(r, offsetX + mLotusLeafLittle.getWidth() / 2, offsetY + mLotusLeafLittle.getHeight() / 2);

            canvas.drawBitmap(mLotusLeafLittle, matrix, null);
        }*/
        for (LotusLeaf lotusLeaf : mLotusLeafs) {
            if (currentTime > lotusLeaf.startTime && lotusLeaf.startTime != 0) {
                getLeafLocation(lotusLeaf, currentTime);

                //Matrix matrix = mMatrix;
                matrix.reset();

                offsetX = mLotusLeafLeftOffset + lotusLeaf.x;
                offsetY = mLotusLeafTopOffset + lotusLeaf.y + mLocationOffsetY * dragPercent;
                matrix.postTranslate(offsetX, offsetY);

                float rotateFraction = ((currentTime - lotusLeaf.startTime) % mLeafRotateTime) / (float) mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                float r = lotusLeaf.rotateDirection == 0 ? (angle + lotusLeaf.rotateAngle) : (-angle + lotusLeaf.rotateAngle);
                Bitmap bitmap = lotusLeaf.shapeType == LotusLeaf.ShapeType.SHAPE_BIG ? mLotusLeafBig : mLotusLeafLittle;
                matrix.postRotate(r, offsetX + bitmap.getWidth() / 2, offsetY + bitmap.getHeight() / 2);

                canvas.drawBitmap(bitmap, matrix, null);
            } else {
                continue;
            }
        }
    }

    private void drawPond(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        int y = Math.max(0, mTop - mTotalDragDistance);
        float dragPercent = Math.min(1f, Math.abs(mPercent));

        float pondScale;
        float pondTopOffset;
        float pondMoveOffset;
        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            pondScale = POND_INITIAL_SCALE + (POND_FINAL_SCALE - POND_INITIAL_SCALE) * scalePercent;
            pondTopOffset = mPondInitialTopOffset - (mPondFinalTopOffset - mPondInitialTopOffset) * scalePercent;
            pondMoveOffset = mPondMoveOffset * (1.0f - scalePercent);
        } else {
            float scalePercent = dragPercent / SCALE_START_PERCENT;
            pondScale = POND_INITIAL_SCALE;
            pondTopOffset = mPondInitialTopOffset;
            pondMoveOffset = mPondMoveOffset * scalePercent;
        }

        float offsetX = -(mScreenWidth * pondScale - mScreenWidth) / 2.0f;
        float offsetY = y + pondTopOffset - mPondLength * (pondScale - 1.0f) / 2 + pondMoveOffset;

        matrix.postScale(pondScale, pondScale);
        matrix.postTranslate(offsetX, offsetY);
        canvas.drawBitmap(mPond, matrix, null);
    }

    private void getLeafLocation(LotusLeaf lotusleaf, float percent) {
        lotusleaf.x = getLeafLocationX(lotusleaf);
        lotusleaf.y = (int) (mPondLength * (1.0f - LOTUSLEAF_RATIO) * percent);
    }

    private void getLeafLocation(LotusLeaf lotusleaf, long currentTime) {
        long intervalTime = currentTime - lotusleaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            lotusleaf.startTime = System.currentTimeMillis() + new Random().nextInt((int) mLeafFloatTime);
        }

        float fraction = (float) intervalTime / mLeafFloatTime;
        lotusleaf.x = getLeafLocationX(lotusleaf);
        lotusleaf.y = (int) (mPondLength * (1.0f - LOTUSLEAF_RATIO) * fraction);
    }

    private float getLeafLocationX(LotusLeaf lotusleaf) {
        float w = (float) ((float) 2 * Math.PI / (mPondLength * (1.0f - LOTUSLEAF_RATIO)));
        float a = mMiddleAmplitude;
        switch (lotusleaf.amplitudeType) {
            case AMPLITUDE_LITTLE:
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case AMPLITUDE_MIDDLE:
                a = mMiddleAmplitude;
                break;
            case AMPLITUDE_BIG:
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        return (int) (a * Math.sin(w * lotusleaf.y)*Math.exp(lotusleaf.y/mAmplitudePowerFactor));
    }

    private void setupAnimations() {
        mAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                setRotate(interpolatedTime);
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);
    }

    public void setPercent(float percent) {
        mPercent = percent;
        setRotate(percent);
    }

    public void setRotate(float rotate) {
        mRotate = rotate;
        mParent.invalidate();
        invalidateSelf();
    }

    public void resetOriginals() {
        setPercent(0);
        setRotate(0);
    }

    @Override
    public void start() {
        mAnimation.reset();
        isRefreshing = true;
        mParent.startAnimation(mAnimation);
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
        isRefreshing = false;
        resetOriginals();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.translate(0, mTotalDragDistance - mTop);

        drawPond(canvas);
        drawSun(canvas);
        drawLotusLeaf(canvas);

        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }
}
