package com.photo.editor.snapstudio.PhEditor.Editor;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.util.Arrays;
import java.util.List;

public class FaceDetected {
    private static final String TAG = "dlib";

    static {
        try {
            System.loadLibrary("android_dlib");
            jniNativeClassInit();
            Log.d(TAG, "jniNativeClassInit success");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "library not found");
        }
    }

    @SuppressWarnings("unused")
    private long mNativeFaceDetContext;
    private String mLandMarkPath = "";

    @SuppressWarnings("unused")
    public FaceDetected() {
        jniInit(mLandMarkPath);
    }

    public FaceDetected(String landMarkPath) {
        mLandMarkPath = landMarkPath;
        jniInit(mLandMarkPath);
    }

    @Keep
    private native static void jniNativeClassInit();

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detect(@NonNull String path) {
        VisionDetRet[] detRets = jniDetect(path);
        return Arrays.asList(detRets);
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detect(@NonNull Bitmap bitmap) {
        VisionDetRet[] detRets = jniBitmapDetect(bitmap);
        return Arrays.asList(detRets);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    public void release() {
        jniDeInit();
    }

    @Keep
    private synchronized native int jniInit(String landmarkModelPath);

    @Keep
    private synchronized native int jniDeInit();

    @Keep
    private synchronized native VisionDetRet[] jniBitmapDetect(Bitmap bitmap);

    @Keep
    private synchronized native VisionDetRet[] jniDetect(String path);
}
