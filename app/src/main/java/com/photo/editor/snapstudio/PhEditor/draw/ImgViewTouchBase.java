package com.photo.editor.snapstudio.PhEditor.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ImgViewTouchBase extends ImageView implements IDisposable {
    public static final String LOG_TAG = "image";
    protected final float MAX_ZOOM = 2.0f;
    protected final RotateBitmap mBitmapDisplayed = new RotateBitmap(null, 0);
    protected final Matrix mDisplayMatrix = new Matrix();
    protected final float[] mMatrixValues = new float[9];
    protected Matrix mBaseMatrix = new Matrix();
    protected Handler mHandler = new Handler();
    protected float mMaxZoom;
    protected Runnable mOnLayoutRunnable = null;
    protected Matrix mSuppMatrix = new Matrix();
    protected int mThisHeight = -1;
    protected int mThisWidth = -1;
    protected RectF mCenterRect = new RectF();
    private OnBitmapChangedListener mListener;

    public ImgViewTouchBase(Context context) {
        super(context);
        init();
    }

    public ImgViewTouchBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public static float easeOut(float f, float f2, float f3, float f4) {
        float f5 = (f / f4) - 1.0f;
        return (f3 * ((f5 * f5 * f5) + 1.0f)) + f2;
    }

    public void onZoom(float f) {
    }

    public void setOnBitmapChangedListener(OnBitmapChangedListener onBitmapChangedListener) {
        this.mListener = onBitmapChangedListener;
    }

    public void init() {
        setScaleType(ScaleType.MATRIX);
    }

    public void clear() {
        setImageBitmapReset(null, true);
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mThisWidth = i3 - i;
        this.mThisHeight = i4 - i2;
        Runnable runnable = this.mOnLayoutRunnable;
        if (runnable != null) {
            this.mOnLayoutRunnable = null;
            runnable.run();
        }
        if (this.mBitmapDisplayed.getBitmap() != null) {
            getProperBaseMatrix(this.mBitmapDisplayed, this.mBaseMatrix);
            setImageMatrix(Command.Layout, getImageViewMatrix());
        }
    }

    public void setImageBitmapReset(Bitmap bitmap, boolean z) {
        setImageRotateBitmapReset(new RotateBitmap(bitmap, 0), z);
    }

    public void setImageBitmapReset(Bitmap bitmap, int i, boolean z) {
        setImageRotateBitmapReset(new RotateBitmap(bitmap, i), z);
    }

    public void setImageRotateBitmapReset(final RotateBitmap rotateBitmap, final boolean z) {
        Log.d(LOG_TAG, "setImageRotateBitmapReset");
        if (getWidth() <= 0) {
            this.mOnLayoutRunnable = new Runnable() {
                public void run() {
                    ImgViewTouchBase.this.setImageBitmapReset(rotateBitmap.getBitmap(), rotateBitmap.getRotation(), z);
                }
            };
            return;
        }
        if (rotateBitmap.getBitmap() != null) {
            getProperBaseMatrix(rotateBitmap, this.mBaseMatrix);
            setImageBitmap(rotateBitmap.getBitmap(), rotateBitmap.getRotation());
        } else {
            this.mBaseMatrix.reset();
            setImageBitmap(null);
        }
        if (z) {
            this.mSuppMatrix.reset();
        }
        setImageMatrix(Command.Reset, getImageViewMatrix());
        this.mMaxZoom = maxZoom();
        OnBitmapChangedListener onBitmapChangedListener = this.mListener;
        if (onBitmapChangedListener != null) {
            onBitmapChangedListener.onBitmapChanged(rotateBitmap.getBitmap());
        }
    }

    public float maxZoom() {
        if (this.mBitmapDisplayed.getBitmap() == null) {
            return 1.0f;
        }
        return Math.max(((float) this.mBitmapDisplayed.getWidth()) / ((float) this.mThisWidth), ((float) this.mBitmapDisplayed.getHeight()) / ((float) this.mThisHeight)) * 4.0f;
    }

    public RotateBitmap getDisplayBitmap() {
        return this.mBitmapDisplayed;
    }

    public float getMaxZoom() {
        return this.mMaxZoom;
    }

    public void setImageBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap, 0);
    }

    public void setImageBitmap(Bitmap bitmap, int i) {
        super.setImageBitmap(bitmap);
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawable.setDither(true);
        }
        this.mBitmapDisplayed.setBitmap(bitmap);
        this.mBitmapDisplayed.setRotation(i);
    }

    public Matrix getImageViewMatrix() {
        this.mDisplayMatrix.set(this.mBaseMatrix);
        this.mDisplayMatrix.postConcat(this.mSuppMatrix);
        return this.mDisplayMatrix;
    }

    public void getProperBaseMatrix(RotateBitmap rotateBitmap, Matrix matrix) {
        float width = (float) getWidth();
        float height = (float) getHeight();
        float width2 = (float) rotateBitmap.getWidth();
        float height2 = (float) rotateBitmap.getHeight();
        matrix.reset();
        float min = Math.min(Math.min(width / width2, 2.0f), Math.min(height / height2, 2.0f));
        matrix.postConcat(rotateBitmap.getRotateMatrix());
        matrix.postScale(min, min);
        matrix.postTranslate((width - (width2 * min)) / 2.0f, (height - (height2 * min)) / 2.0f);
    }

    public float getValue(Matrix matrix, int i) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[i];
    }

    public RectF getBitmapRect() {
        if (this.mBitmapDisplayed.getBitmap() == null) {
            return null;
        }
        Matrix imageViewMatrix = getImageViewMatrix();
        RectF rectF = new RectF(0.0f, 0.0f, (float) this.mBitmapDisplayed.getBitmap().getWidth(), (float) this.mBitmapDisplayed.getBitmap().getHeight());
        imageViewMatrix.mapRect(rectF);
        return rectF;
    }

    public float getScale(Matrix matrix) {
        return getValue(matrix, 0);
    }

    public float getScale() {
        return getScale(this.mSuppMatrix);
    }

    public void center(boolean z, boolean z2) {
        if (this.mBitmapDisplayed.getBitmap() != null) {
            RectF center = getCenter(z, z2);
            if (!(center.left == 0.0f && center.top == 0.0f)) {
                postTranslate(center.left, center.top);
            }
        }
    }

    public void setImageMatrix(Command command, Matrix matrix) {
        setImageMatrix(matrix);
    }

    protected RectF getCenter(boolean horizontal, boolean vertical) {
        final Drawable drawable = getDrawable();

        if (drawable == null) return new RectF(0, 0, 0, 0);

        RectF rect = getBitmapRect();
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (vertical) {
            int viewHeight = getHeight();
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = getHeight() - rect.bottom;
            }
        }
        if (horizontal) {
            int viewWidth = getWidth();
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }
        mCenterRect.set(deltaX, deltaY, 0, 0);
        return mCenterRect;
    }

    public void postTranslate(float f, float f2) {
        this.mSuppMatrix.postTranslate(f, f2);
        setImageMatrix(Command.Move, getImageViewMatrix());
    }

    public void postScale(float f, float f2, float f3) {
        this.mSuppMatrix.postScale(f, f, f2, f3);
        setImageMatrix(Command.Zoom, getImageViewMatrix());
    }

    public void zoomTo(float f) {
        zoomTo(f, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
    }

    public void zoomTo(float f, float f2) {
        zoomTo(f, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, f2);
    }

    public void zoomTo(float f, float f2, float f3) {
        float f4 = this.mMaxZoom;
        if (f > f4) {
            f = f4;
        }
        postScale(f / getScale(), f2, f3);
        onZoom(getScale());
        center(true, true);
    }

    public void scrollBy(float f, float f2) {
        panBy(f, f2);
    }

    public void panBy(float f, float f2) {
        RectF bitmapRect = getBitmapRect();
        RectF rectF = new RectF(f, f2, 0.0f, 0.0f);
        updateRect(bitmapRect, rectF);
        postTranslate(rectF.left, rectF.top);
        center(true, true);
    }

    public void updateRect(RectF rectF, RectF rectF2) {
        float width = (float) getWidth();
        float height = (float) getHeight();
        if (rectF.top >= 0.0f && rectF.bottom <= height) {
            rectF2.top = 0.0f;
        }
        if (rectF.left >= 0.0f && rectF.right <= width) {
            rectF2.left = 0.0f;
        }
        if (rectF.top + rectF2.top >= 0.0f && rectF.bottom > height) {
            rectF2.top = (float) ((int) (0.0f - rectF.top));
        }
        float f = height - 0.0f;
        if (rectF.bottom + rectF2.top <= f && rectF.top < 0.0f) {
            rectF2.top = (float) ((int) (f - rectF.bottom));
        }
        if (rectF.left + rectF2.left >= 0.0f) {
            rectF2.left = (float) ((int) (0.0f - rectF.left));
        }
        float f2 = width - 0.0f;
        if (rectF.right + rectF2.left <= f2) {
            rectF2.left = (float) ((int) (f2 - rectF.right));
        }
    }

    public void scrollBy(float f, float f2, float f3) {
        final long currentTimeMillis = System.currentTimeMillis();
        Handler handler = this.mHandler;
        final float f4 = f3;
        final float f5 = f;
        final float f6 = f2;
        //C12162 r0 = ;
        handler.post(new Runnable() {
            float old_x = 0.0f;
            float old_y = 0.0f;

            public void run() {
                float min = Math.min(f4, (float) (System.currentTimeMillis() - currentTimeMillis));
                float easeOut = ImgViewTouchBase.easeOut(min, 0.0f, f5, f4);
                float easeOut2 = ImgViewTouchBase.easeOut(min, 0.0f, f6, f4);
                ImgViewTouchBase.this.panBy(easeOut - this.old_x, easeOut2 - this.old_y);
                this.old_x = easeOut;
                this.old_y = easeOut2;
                if (min < f4) {
                    ImgViewTouchBase.this.mHandler.post(this);
                    return;
                }
                RectF center = ImgViewTouchBase.this.getCenter(true, true);
                if (center.left != 0.0f || center.top != 0.0f) {
                    ImgViewTouchBase.this.scrollBy(center.left, center.top);
                }
            }
        });
    }

    public void zoomTo(float f, float f2, float f3, float f4) {
        final long currentTimeMillis = System.currentTimeMillis();
        final float scale = (f - getScale()) / f4;
        final float scale2 = getScale();
        Handler handler = this.mHandler;
        final float f5 = f4;
        final float f6 = f2;
        final float f7 = f3;
        //C12173 r0 = ;
        handler.post(new Runnable() {
            public void run() {
                float min = Math.min(f5, (float) (System.currentTimeMillis() - currentTimeMillis));
                ImgViewTouchBase.this.zoomTo(scale2 + (scale * min), f6, f7);
                if (min < f5) {
                    ImgViewTouchBase.this.mHandler.post(this);
                }
            }
        });
    }

    public void dispose() {
        if (this.mBitmapDisplayed.getBitmap() != null && !this.mBitmapDisplayed.getBitmap().isRecycled()) {
            this.mBitmapDisplayed.getBitmap().recycle();
        }
        clear();
    }


    protected enum Command {
        Center,
        Move,
        Zoom,
        Layout,
        Reset
    }

    public interface OnBitmapChangedListener {
        void onBitmapChanged(Bitmap bitmap);
    }
}
