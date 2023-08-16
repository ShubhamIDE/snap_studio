package com.enrique.stackblur;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NativeBlurProcess {
    static final ExecutorService EXECUTOR;
    static final int EXECUTOR_THREADS;

    /* access modifiers changed from: private */
    public static native void functionToBlur(Bitmap bitmap, int i, int i2, int i3, int i4);

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        EXECUTOR_THREADS = availableProcessors;
        EXECUTOR = Executors.newFixedThreadPool(availableProcessors);
        System.loadLibrary("blur");
    }

    public Bitmap blur(Bitmap original, float radius) {
        Bitmap bitmapOut = original.copy(Bitmap.Config.ARGB_8888, true);
        int cores = EXECUTOR_THREADS;
        ArrayList arrayList = new ArrayList(cores);
        ArrayList arrayList2 = new ArrayList(cores);
        for (int i = 0; i < cores; i++) {
            Bitmap bitmap = bitmapOut;
            int i2 = cores;
            int i3 = i;
            arrayList.add(new NativeTask(bitmap, (int) radius, i2, i3, 1));
            arrayList2.add(new NativeTask(bitmap, (int) radius, i2, i3, 2));
        }
        try {
            ExecutorService executorService = EXECUTOR;
            executorService.invokeAll(arrayList);
            try {
                executorService.invokeAll(arrayList2);
                return bitmapOut;
            } catch (InterruptedException e) {
                return bitmapOut;
            }
        } catch (InterruptedException e2) {
            return bitmapOut;
        }
    }

    private static class NativeTask implements Callable<Void> {
        private final Bitmap _bitmapOut;
        private final int _coreIndex;
        private final int _radius;
        private final int _round;
        private final int _totalCores;

        public NativeTask(Bitmap bitmapOut, int radius, int totalCores, int coreIndex, int round) {
            this._bitmapOut = bitmapOut;
            this._radius = radius;
            this._totalCores = totalCores;
            this._coreIndex = coreIndex;
            this._round = round;
        }

        public Void call() throws Exception {
            NativeBlurProcess.functionToBlur(this._bitmapOut, this._radius, this._totalCores, this._coreIndex, this._round);
            return null;
        }
    }
}
