package com.photo.editor.snapstudio.PhEditor.draw;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.ViewConfiguration;

public class ImgViewTouch extends ImgViewTouchBase {
    static final float MIN_ZOOM = 0.9f;
    protected float mCurrentScaleFactor;
    protected int mDoubleTapDirection;
    protected GestureDetector mGestureDetector;
    protected GestureListener mGestureListener;
    protected ScaleGestureDetector mScaleDetector;
    protected float mScaleFactor;
    protected ScaleListener mScaleListener;
    protected int mTouchSlop;

    public ImgViewTouch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void init() {
        super.init();
        this.mTouchSlop = ViewConfiguration.getTouchSlop();
        this.mGestureListener = new GestureListener();
        this.mScaleListener = new ScaleListener();
        this.mScaleDetector = new ScaleGestureDetector(getContext(), this.mScaleListener);
        this.mGestureDetector = new GestureDetector(getContext(), this.mGestureListener, null);
        this.mCurrentScaleFactor = 1.0f;
        this.mDoubleTapDirection = 1;
    }

    public void setImageRotateBitmapReset(RotateBitmap rotateBitmap, boolean z) {
        super.setImageRotateBitmapReset(rotateBitmap, z);
        this.mScaleFactor = getMaxZoom() / 3.0f;
    }

    public void onZoom(float f) {
        super.onZoom(f);
        if (!this.mScaleDetector.isInProgress()) {
            this.mCurrentScaleFactor = f;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mScaleDetector.onTouchEvent(motionEvent);
        if (!this.mScaleDetector.isInProgress()) {
            this.mGestureDetector.onTouchEvent(motionEvent);
        }
        if ((motionEvent.getAction() & 255) == 1 && getScale() < 1.0f) {
            zoomTo(1.0f, 50.0f);
        }
        return true;
    }

    public float onDoubleTapPost(float f, float f2) {
        if (this.mDoubleTapDirection == 1) {
            float f3 = this.mScaleFactor;
            if ((2.0f * f3) + f <= f2) {
                return f + f3;
            }
            this.mDoubleTapDirection = -1;
            return f2;
        }
        this.mDoubleTapDirection = 1;
        return 1.0f;
    }

    class GestureListener extends SimpleOnGestureListener {
        GestureListener() {
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (motionEvent == null || motionEvent2 == null || motionEvent.getPointerCount() > 1 || motionEvent2.getPointerCount() > 1 || ImgViewTouch.this.mScaleDetector.isInProgress() || ImgViewTouch.this.getScale() == 1.0f) {
                return false;
            }
            ImgViewTouch.this.scrollBy(-f, -f2);
            ImgViewTouch.this.invalidate();
            return super.onScroll(motionEvent, motionEvent2, f, f2);
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (motionEvent.getPointerCount() > 1 || motionEvent2.getPointerCount() > 1 || ImgViewTouch.this.mScaleDetector.isInProgress()) {
                return false;
            }
            float x = motionEvent2.getX() - motionEvent.getX();
            float y = motionEvent2.getY() - motionEvent.getY();
            if (Math.abs(f) > 800.0f || Math.abs(f2) > 800.0f) {
                ImgViewTouch.this.scrollBy(x / 2.0f, y / 2.0f, 300.0f);
                ImgViewTouch.this.invalidate();
            }
            return super.onFling(motionEvent, motionEvent2, f, f2);
        }

        public boolean onDoubleTap(MotionEvent motionEvent) {
            float scale = ImgViewTouch.this.getScale();
            ImgViewTouch imageViewTouch = ImgViewTouch.this;
            float min = Math.min(ImgViewTouch.this.getMaxZoom(), Math.max(imageViewTouch.onDoubleTapPost(scale, imageViewTouch.getMaxZoom()), ImgViewTouch.MIN_ZOOM));
            ImgViewTouch.this.mCurrentScaleFactor = min;
            ImgViewTouch.this.zoomTo(min, motionEvent.getX(), motionEvent.getY(), 200.0f);
            ImgViewTouch.this.invalidate();
            return super.onDoubleTap(motionEvent);
        }
    }

    class ScaleListener extends SimpleOnScaleGestureListener {
        ScaleListener() {
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            scaleGestureDetector.getCurrentSpan();
            scaleGestureDetector.getPreviousSpan();
            float min = Math.min(ImgViewTouch.this.getMaxZoom(), Math.max(ImgViewTouch.this.mCurrentScaleFactor * scaleGestureDetector.getScaleFactor(), ImgViewTouch.MIN_ZOOM));
            ImgViewTouch.this.zoomTo(min, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            ImgViewTouch imageViewTouch = ImgViewTouch.this;
            imageViewTouch.mCurrentScaleFactor = Math.min(imageViewTouch.getMaxZoom(), Math.max(min, ImgViewTouch.MIN_ZOOM));
            ImgViewTouch.this.mDoubleTapDirection = 1;
            ImgViewTouch.this.invalidate();
            return true;
        }
    }
}
