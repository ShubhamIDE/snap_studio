package com.photo.editor.snapstudio.PhEditor.draw;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

public class RotateBitmap {
    public static final String TAG = "RotateBitmap";
    private Bitmap mBitmap;
    private int mBitmapHeight;
    private int mBitmapWidth;
    private int mHeight;
    private int mRotation;
    private int mWidth;

    public RotateBitmap(Bitmap bitmap, int i) {
        this.mRotation = i % 360;
        setBitmap(bitmap);
    }

    public int getRotation() {
        return this.mRotation % 360;
    }

    public void setRotation(int i) {
        this.mRotation = i;
        invalidate();
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        if (bitmap != null) {
            this.mBitmapWidth = bitmap.getWidth();
            this.mBitmapHeight = bitmap.getHeight();
            invalidate();
        }
    }

    private void invalidate() {
        Matrix matrix = new Matrix();
        int i = this.mBitmapWidth / 2;
        matrix.preTranslate((float) (-i), (float) (-(this.mBitmapHeight / 2)));
        matrix.postRotate((float) this.mRotation);
        float f = (float) i;
        matrix.postTranslate(f, f);
        RectF rectF = new RectF(0.0f, 0.0f, (float) this.mBitmapWidth, (float) this.mBitmapHeight);
        matrix.mapRect(rectF);
        this.mWidth = (int) rectF.width();
        this.mHeight = (int) rectF.height();
    }

    public Matrix getRotateMatrix() {
        Matrix matrix = new Matrix();
        if (this.mRotation != 0) {
            matrix.preTranslate((float) (-(this.mBitmapWidth / 2)), (float) (-(this.mBitmapHeight / 2)));
            matrix.postRotate((float) this.mRotation);
            matrix.postTranslate((float) (this.mWidth / 2), (float) (this.mHeight / 2));
        }
        return matrix;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void recycle() {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.mBitmap = null;
        }
    }
}
