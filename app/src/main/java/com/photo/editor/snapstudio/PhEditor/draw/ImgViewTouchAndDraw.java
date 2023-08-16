package com.photo.editor.snapstudio.PhEditor.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ImgViewTouchAndDraw extends ImgViewTouch {
    protected static final float TOUCH_TOLERANCE = 4.0f;
    protected static Bitmap mCopy;
    protected Canvas mCanvas;
    protected Matrix mIdentityMatrix = new Matrix();
    protected Matrix mInvertedMatrix = new Matrix();
    protected Paint mPaint;
    protected TouchMode mTouchMode = TouchMode.DRAW;
    protected float f222mX;
    protected float f223mY;
    protected Path tmpPath = new Path();
    private OnDrawStartListener mDrawListener;

    public ImgViewTouchAndDraw(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public static float[] getMatrixValues(Matrix matrix) {
        float[] fArr = new float[9];
        matrix.getValues(fArr);
        return fArr;
    }

    public static Bitmap getOverlayBitmap() {
        return mCopy;
    }

    public void setOnDrawStartListener(OnDrawStartListener onDrawStartListener) {
        this.mDrawListener = onDrawStartListener;
    }


    public void init() {
        super.init();
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setFilterBitmap(false);
        this.mPaint.setColor(-65536);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setStrokeWidth(10.0f);
        this.tmpPath = new Path();
    }

    public TouchMode getDrawMode() {
        return this.mTouchMode;
    }

    public void setDrawMode(TouchMode touchMode) {
        if (touchMode != this.mTouchMode) {
            this.mTouchMode = touchMode;
            onDrawModeChanged();
        }
    }


    public void onDrawModeChanged() {
        if (this.mTouchMode == TouchMode.DRAW) {
            Matrix matrix = new Matrix(getImageMatrix());
            this.mInvertedMatrix.reset();
            float[] matrixValues = getMatrixValues(matrix);
            matrix.invert(matrix);
            float[] matrixValues2 = getMatrixValues(matrix);
            this.mInvertedMatrix.postTranslate(-matrixValues[2], -matrixValues[5]);
            this.mInvertedMatrix.postScale(matrixValues2[0], matrixValues2[4]);
            this.mCanvas.setMatrix(this.mInvertedMatrix);
        }
    }

    public Paint getPaint() {
        return this.mPaint;
    }

    public void setPaint(Paint paint) {
        this.mPaint.set(paint);
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCopy != null) {
            int saveCount = canvas.getSaveCount();
            canvas.save();
            canvas.drawBitmap(mCopy, getImageMatrix(), null);
            canvas.restoreToCount(saveCount);
        }
    }

    public void commit(Canvas canvas) {
        canvas.drawBitmap(getDisplayBitmap().getBitmap(), new Matrix(), null);
        canvas.drawBitmap(mCopy, new Matrix(), null);
    }

    private void touch_start(float f, float f2) {
        this.tmpPath.reset();
        this.tmpPath.moveTo(f, f2);
        this.f222mX = f;
        this.f223mY = f2;
        OnDrawStartListener onDrawStartListener = this.mDrawListener;
        if (onDrawStartListener != null) {
            onDrawStartListener.onDrawStart();
        }
    }

    private void touch_move(float f, float f2) {
        float abs = Math.abs(f - this.f222mX);
        float abs2 = Math.abs(f2 - this.f223mY);
        if (abs >= 4.0f || abs2 >= 4.0f) {
            Path path = this.tmpPath;
            float f3 = this.f222mX;
            float f4 = this.f223mY;
            path.quadTo(f3, f4, (f3 + f) / 2.0f, (f4 + f2) / 2.0f);
            this.tmpPath.reset();
            this.tmpPath.moveTo((this.f222mX + f) / 2.0f, (this.f223mY + f2) / 2.0f);
            this.f222mX = f;
            this.f223mY = f2;
        }
    }

    private void touch_up() {
        this.tmpPath.reset();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mTouchMode == TouchMode.DRAW && motionEvent.getPointerCount() == 1) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int action = motionEvent.getAction();
            String str = "onTouchEvent";
            if (action == 0) {
                Log.e(str, "t000000000000000");
                touch_start(x, y);
                invalidate();
            } else if (action == 1) {
                Log.e(str, "t11111111111111");
                touch_up();
                invalidate();
            } else if (action == 2) {
                Log.e(str, "t2222222222222222");
                touch_move(x, y);
                invalidate();
            }
            return true;
        }
        return this.mTouchMode == TouchMode.IMAGE ? super.onTouchEvent(motionEvent) : false;
    }

    public enum TouchMode {
        IMAGE,
        DRAW
    }

    public interface OnDrawStartListener {
        void onDrawStart();
    }
}
