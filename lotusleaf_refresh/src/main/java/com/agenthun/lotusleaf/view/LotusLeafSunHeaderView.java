package com.agenthun.lotusleaf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import in.srain.cube.views.ptr.indicator.PtrTensionIndicator;

/**
 * Created by Agent Henry on 2015/5/2.
 */
public class LotusLeafSunHeaderView extends View implements PtrUIHandler {

    private LotusLeafSunDrawable lotusLeafSunDrawable;
    private PtrFrameLayout mPtrFrameLayout;
    private PtrTensionIndicator mPtrTensionIndicator;

    public LotusLeafSunHeaderView(Context context) {
        super(context);
        init();
    }

    public LotusLeafSunHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LotusLeafSunHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        lotusLeafSunDrawable = new LotusLeafSunDrawable(getContext(), this);
    }

    public void setUp(PtrFrameLayout ptrFrameLayout) {
        mPtrFrameLayout = ptrFrameLayout;
        mPtrTensionIndicator = new PtrTensionIndicator();
        mPtrFrameLayout.setPtrIndicator(mPtrTensionIndicator);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = lotusLeafSunDrawable.getTotalDragDistance() * 5 / 4;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        lotusLeafSunDrawable.setBounds(pl, pt, pl + right - left, pt + bottom - top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        lotusLeafSunDrawable.draw(canvas);
        float percent = mPtrTensionIndicator.getOverDragPercent();
    }

    @Override
    public void onUIReset(PtrFrameLayout ptrFrameLayout) {
        lotusLeafSunDrawable.resetOriginals();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout ptrFrameLayout) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        lotusLeafSunDrawable.start();
        float percent = mPtrTensionIndicator.getOverDragPercent();
        lotusLeafSunDrawable.offsetTopAndBottom(mPtrTensionIndicator.getCurrentPosY());
        lotusLeafSunDrawable.setPercent(percent);
        invalidate();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout ptrFrameLayout) {
        float percent = mPtrTensionIndicator.getOverDragPercent();
        lotusLeafSunDrawable.stop();
        lotusLeafSunDrawable.offsetTopAndBottom(mPtrTensionIndicator.getCurrentPosY());
        lotusLeafSunDrawable.setPercent(percent);
        invalidate();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout ptrFrameLayout, boolean b, byte b2, PtrIndicator ptrIndicator) {
        float percent = mPtrTensionIndicator.getOverDragPercent();
        lotusLeafSunDrawable.offsetTopAndBottom(mPtrTensionIndicator.getCurrentPosY());
        lotusLeafSunDrawable.setPercent(percent);
        invalidate();
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        if (drawable == lotusLeafSunDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(drawable);
        }
    }
}
