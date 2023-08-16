package com.photo.editor.snapstudio.PhEditor.helper;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.photo.editor.snapstudio.PhEditor.Editor.Landmark;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

public class BeautyHelper {
    public static int[] colorPaletteBase = {-3082, -274484, -1261154, -2177866, -3103333, -1988751, Color.parseColor("#FDE8DD"), Color.parseColor("#F4D6D1"), Color.parseColor("#FFD0BC"), Color.parseColor("#FFFAFA"), Color.parseColor("#FCD8C5"), Color.parseColor("#EFC1AA"), Color.parseColor("#FCCDAC"), Color.parseColor("#EFB69A"), Color.parseColor("#EBA991"), Color.parseColor("#D69B7E"), Color.parseColor("#FCD4A9"), Color.parseColor("#FAE9CD"), Color.parseColor("#FFE6B1"), Color.parseColor("#C8A173"), Color.parseColor("#C18F71"), Color.parseColor("#B18265"), Color.parseColor("#B36A33"), Color.parseColor("#FDE8DD"), Color.parseColor("#F4D6D1"), Color.parseColor("#FFD0BC"), Color.parseColor("#FFFAFA"), Color.parseColor("#FCD8C5"), Color.parseColor("#EFC1AA"), Color.parseColor("#FCCDAC"), Color.parseColor("#EFB69A"), Color.parseColor("#EBA991"), Color.parseColor("#D69B7E"), Color.parseColor("#FCD4A9"), Color.parseColor("#FAE9CD"), Color.parseColor("#FFE6B1"), Color.parseColor("#C8A173"), Color.parseColor("#C18F71"), Color.parseColor("#B18265"), Color.parseColor("#B36A33"), -1144447, -2384766, -6791885, -4491697, -8169654, -10211050, -10535896, -12838650, -1988737};
    public static int[] colorPaletteBlush = {-251761, -1142822, -6553596, -65536, -2543280, -1624490, -4177570, -1352083, -1082488, -2924936, -2526840, -1930340, -6083036};
    public static int[] colorPaletteEyeColor = {-16736513, -16726273, Color.parseColor("#3B312F"), Color.parseColor("#352B2F"), Color.parseColor("#bb746b"), Color.parseColor("#974e47"), Color.parseColor("#c26d59"), Color.parseColor("#b26458"), Color.parseColor("#da8c7f"), Color.parseColor("#e35d6a"), Color.parseColor("#ff7373"), Color.parseColor("#40e0d0"), Color.parseColor("#f6546a"), Color.parseColor("#00ced1"), Color.parseColor("#ff1493"), Color.parseColor("#ff5ab3"), Color.parseColor("#ff72be"), Color.parseColor("#de0f0d"), Color.parseColor("#0dde79"), Color.parseColor("#3B312F"), Color.parseColor("#352B2F"), Color.parseColor("#bb746b"), Color.parseColor("#974e47"), Color.parseColor("#c26d59"), Color.parseColor("#b26458"), Color.parseColor("#da8c7f"), Color.parseColor("#e35d6a"), Color.parseColor("#ff7373"), Color.parseColor("#40e0d0"), Color.parseColor("#f6546a"), Color.parseColor("#00ced1"), Color.parseColor("#ff1493"), Color.parseColor("#ff5ab3"), Color.parseColor("#ff72be"), Color.parseColor("#de0f0d"), Color.parseColor("#0dde79"), -8804330, -4349163, -1340927, -10937663, -3815995, -15066598, -8570600, -13421815};
    public static int[] colorPaletteLip = {-3800785, Color.parseColor("#bb746b"), Color.parseColor("#974e47"), Color.parseColor("#c26d59"), Color.parseColor("#b26458"), Color.parseColor("#da8c7f"), Color.parseColor("#e35d6a"), Color.parseColor("#ff7373"), Color.parseColor("#40e0d0"), Color.parseColor("#f6546a"), Color.parseColor("#00ced1"), Color.parseColor("#ff1493"), Color.parseColor("#ff5ab3"), Color.parseColor("#ff72be"), Color.parseColor("#de0f0d"), Color.parseColor("#0dde79"), -51814, -2866599, -492098, -1016670, -2414307, -5308404, -10018014, -1424566, -3043354, -1703893, -7466410, -5240204, -5027445, -5483914, -4246940, -1071694, -2079108, -11204045, -14946595, -44719, -174040, -1409187, -2653841, -2714742, -12310990, -14848, -8841899, -4496545, -4818593, -7077376, -5826257, -2226104, -2024616, -15015915, -5546921, -5552813, -1893255, -2279839, -6539208, -9754313, -500667, -16164064, Color.parseColor("#723890"), -5131086};
    private static int alpha = 255;

    public static void brightenEyes(Canvas canvas, Canvas canvas2, Bitmap bitmap, Path path, Rect rect, Paint paint, Paint paint2, Paint paint3, int i) {
        Rect rect2 = rect;
        Bitmap createBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
        Canvas canvas3 = canvas2;
        canvas2.setBitmap(createBitmap);
        changeBitmapContrast(canvas3, bitmap, path, i, rect, paint2, paint);
        if (createBitmap != null && !createBitmap.isRecycled()) {
            canvas.drawBitmap(createBitmap, (float) rect2.left, (float) rect2.top, paint3);
        }
        createBitmap.recycle();
    }

    public static void enlargeEyes(Canvas canvas, Bitmap bitmap, List<Landmark> list, Rect rect, int i, Paint paint) {
        bulgeBitmap(canvas, list, bitmap, rect, i, paint);
    }

    public static Bitmap removeDarkCircles(Canvas canvas, Bitmap bitmap, Path path, Paint paint, Paint paint2, int i) {
        paint.setAlpha(i * 5);
        paint2.setAntiAlias(false);
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        canvas.setBitmap(createBitmap);
        canvas.drawPath(path, paint);
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint2);
        }
        return createBitmap;
    }

    public static Path drawPath(List<Landmark> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Path path = new Path();
        path.moveTo(((Landmark) list.get(0)).getPosition().x, ((Landmark) list.get(0)).getPosition().y);
        for (int i = 1; i < list.size(); i++) {
            path.lineTo(((Landmark) list.get(i)).getPosition().x, ((Landmark) list.get(i)).getPosition().y);
        }
        path.lineTo(((Landmark) list.get(0)).getPosition().x, ((Landmark) list.get(0)).getPosition().y);
        path.close();
        return path;
    }

    private static void changeBitmapContrast(Canvas canvas, Bitmap bitmap, Path path, int i, Rect rect, Paint paint, Paint paint2) {
        float f = (((float) i) / 200.0f) + 1.0f;
        paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f})));
        Rect rect2 = new Rect(0, 0, rect.width(), rect.height());
        paint2.setMaskFilter(new BlurMaskFilter(5.0f, Blur.NORMAL));
        canvas.drawPath(path, paint2);
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, rect, rect2, paint);
        }
    }

    public static Rect getEyeRectanglePositions(List<Landmark> list, List<Landmark> list2, Bitmap bitmap) {
        List<Landmark> list3 = list2;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        if (list == null || list.size() == 0) {
            return new Rect();
        }
        int i = 0;
        int i2 = 0;
        for (Landmark landmark : list) {
            if (landmark.getPosition().y > ((float) i)) {
                i = (int) landmark.getPosition().y;
            }
            if (landmark.getPosition().y < ((float) height)) {
                height = (int) landmark.getPosition().y;
            }
            if (landmark.getPosition().x > ((float) i2)) {
                i2 = (int) landmark.getPosition().x;
            }
            if (landmark.getPosition().x < ((float) width)) {
                width = (int) landmark.getPosition().x;
            }
        }
        double d = 0.0d;
        if (!(list3 == null || list2.size() == 0)) {
            d = Math.sqrt(Math.pow((double) (((Landmark) list3.get(0)).getPosition().y - ((Landmark) list3.get(1)).getPosition().y), 2.0d) + Math.pow((double) (((Landmark) list3.get(0)).getPosition().x - ((Landmark) list3.get(1)).getPosition().x), 2.0d));
        }
        double d2 = (double) width;
        double d3 = (double) (width - i2);
        Double.isNaN(d3);
        double d4 = d3 * 0.2d;
        Double.isNaN(d2);
        int i3 = (int) (d2 + d4);
        double d5 = (double) height;
        double d6 = (double) (height - i);
        Double.isNaN(d6);
        double d7 = d6 * 0.1d;
        Double.isNaN(d5);
        int i4 = (int) (d5 + d7);
        double d8 = (double) i2;
        int i5 = i2 - width;
        int i6 = i;
        double d9 = (double) i5;
        Double.isNaN(d9);
        double d10 = d9 * 0.2d;
        Double.isNaN(d8);
        int i7 = (int) (d8 + d10);
        double d11 = d / 2.0d;
        double d12 = (double) ((i6 + height) / 2);
        Double.isNaN(d12);
        Rect rect = new Rect(i3, i4, i7, (int) (d11 + d12));
        rect.left = rect.left >= 0 ? rect.left : 0;
        rect.top = rect.top >= 0 ? rect.top : 0;
        rect.right = rect.right > bitmap.getWidth() ? bitmap.getWidth() : rect.right;
        rect.bottom = rect.bottom > bitmap.getHeight() ? bitmap.getHeight() : rect.bottom;
        return rect;
    }

    private static void bulgeBitmap(Canvas canvas, List<Landmark> list, Bitmap bitmap, Rect rect, int i, Paint paint) {
        double d;
        int i2;
        int i3;
        int i4;
        int i5;
        List<Landmark> list2 = list;
        Rect rect2 = rect;
        if (list2 != null && list.size() != 0) {
            double d2 = (double) i;
            Double.isNaN(d2);
            double d3 = d2 / 125.0d;
            int abs = ((int) Math.abs(((Landmark) list2.get(0)).getPosition().x - ((Landmark) list2.get(list.size() / 2)).getPosition().x)) / 2;
            int i6 = 0;
            int i7 = 0;
            for (Landmark landmark : list) {
                i6 = (int) (((float) i6) + landmark.getPosition().x);
                i7 = (int) (((float) i7) + landmark.getPosition().y);
            }
            int size = (i6 / list.size()) - rect2.left;
            int size2 = (i7 / list.size()) - rect2.top;
            int width = rect.width();
            int height = rect.height();
            int i8 = width * height;
            int[] iArr = new int[i8];
            int[] iArr2 = new int[i8];
            int[] iArr3 = iArr2;
            int[] iArr4 = iArr;
            int i9 = i8;
            int i10 = height;
            int i11 = width;
            bitmap.getPixels(iArr, 0, width, rect2.left, rect2.top, width, i10);
            int i12 = 0;
            while (i12 < i9) {
                int i13 = i12 % i11;
                int i14 = i12 / i11;
                int i15 = i13 - size;
                int i16 = i14 - size2;
                double d4 = (double) ((i15 * i15) + (i16 * i16));
                int i17 = i11;
                if (d4 < ((double) (abs * abs))) {
                    double sqrt = Math.sqrt(d4);
                    double d5 = (double) abs;
                    Double.isNaN(d5);
                    double d6 = sqrt / d5;
                    i2 = i9;
                    i3 = abs;
                    double atan2 = Math.atan2((double) i16, (double) i15);
                    double pow = Math.pow(d6, d3) * sqrt;
                    double d7 = (double) i13;
                    double cos = Math.cos(atan2) * pow;
                    d = d3;
                    double d8 = (double) size;
                    Double.isNaN(d8);
                    double d9 = cos + d8;
                    Double.isNaN(d7);
                    double d10 = d9 - d7;
                    Double.isNaN(d7);
                    i13 = (int) (d7 + d10);
                    double d11 = (double) i14;
                    double sin = Math.sin(atan2) * pow;
                    double d12 = (double) size2;
                    Double.isNaN(d12);
                    double d13 = sin + d12;
                    Double.isNaN(d11);
                    double d14 = d13 - d11;
                    Double.isNaN(d11);
                    i14 = (int) (d11 + d14);
                } else {
                    d = d3;
                    i2 = i9;
                    i3 = abs;
                }
                if (i13 >= 0) {
                    i5 = i17;
                    if (i13 >= i5 || i14 < 0) {
                        i4 = i10;
                    } else {
                        i4 = i10;
                        if (i14 < i4) {
                            iArr3[i12] = iArr4[(i14 * i5) + i13];
                        }
                    }
                } else {
                    i4 = i10;
                    i5 = i17;
                }
                i12++;
                i11 = i5;
                i10 = i4;
                abs = i3;
                i9 = i2;
                d3 = d;
            }
            int i18 = i11;
            Bitmap bitmap3 = bitmap;
            int[] iArr5 = iArr3;
            int i19 = i18;
            bitmap3.setPixels(iArr5, 0, i19, rect2.left, rect2.top, i18, i10);
            if (bitmap3 != null && !bitmap.isRecycled()) {
                canvas.drawBitmap(bitmap3, 0.0f, 0.0f, paint);
            }
        }
    }

    public static int getAverageColor(Bitmap bitmap, Rect rect) {
        if (bitmap == null || rect.width() == 0 || rect.height() == 0 || rect.height() * rect.width() < 0 || rect.height() + 10 > bitmap.getHeight()) {
            return 0;
        }
        int width = rect.width();
        boolean hasAlpha = bitmap.hasAlpha();
        int i = width * 10;
        int[] iArr = new int[i];
        bitmap.getPixels(iArr, 0, width, rect.left, rect.bottom, width, 10);
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < 10; i6++) {
            for (int i7 = 0; i7 < width; i7++) {
                int i8 = iArr[(i6 * width) + i7];
                int i9 = i8 >> 16;
                int i10 = alpha;
                i3 += i9 & i10;
                i4 += (i8 >> 8) & i10;
                i5 += i10 & i8;
                if (hasAlpha) {
                    i2 += i8 >>> 24;
                }
            }
        }
        return Color.argb(hasAlpha ? i2 / i : alpha, i3 / i, i4 / i, i5 / i);
    }

    public static Path drawPathForDarkCircle(List<Landmark> list) {
        Path path = new Path();
        path.moveTo(((Landmark) list.get(0)).getPosition().x, ((Landmark) list.get(0)).getPosition().y);
        for (int i = 1; i < list.size(); i++) {
            path.lineTo(((Landmark) list.get(i)).getPosition().x, ((Landmark) list.get(i)).getPosition().y);
        }
        path.lineTo(((Landmark) list.get(0)).getPosition().x, ((Landmark) list.get(0)).getPosition().y);
        path.close();
        return path;
    }

    public static void featherBitmap(Canvas canvas, Path path, Rect rect, Bitmap bitmap, Bitmap bitmap2, Paint paint) {
        int width = rect.width();
        int height = rect.height();
        canvas.setBitmap(bitmap2);
        Path path2 = new Path();
        path.offset((float) (-rect.left), (float) (-rect.top), path2);
        paint.setMaskFilter(new BlurMaskFilter(10.0f, Blur.NORMAL));
        canvas.drawPath(path2, paint);
        Paint paint2 = new Paint();
        paint2.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, rect, new Rect(0, 0, width, height), paint2);
        }
    }

    public static void colorBlendMultiply(int[] iArr, int[] iArr2) {
        for (int i = 0; i < iArr2.length; i++) {
            if (iArr2[i] != 0) {
                iArr2[i] = Color.argb(Color.alpha(iArr2[i]), (Color.red(iArr[i]) * Color.red(iArr2[i])) / alpha, (Color.green(iArr[i]) * Color.green(iArr2[i])) / alpha, (Color.blue(iArr[i]) * Color.blue(iArr2[i])) / alpha);
            }
        }
    }

    public static void eyeColorBlendMultiply(int[] iArr, int i) {
        int red = Color.red(i);
        int green = Color.green(i);
        int blue = Color.blue(i);
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (iArr[i2] != 0) {
                iArr[i2] = Color.argb(Color.alpha(iArr[i2]), red, green, blue);
            }
        }
    }

    public static void colorBlendBase(int[] iArr, int[] iArr2, int i, int i2, int i3) {
        int[] iArr3 = iArr2;
        int i4 = i;
        for (int i5 = 0; i5 < iArr3.length; i5++) {
            if (iArr3[i5] != 0) {
                float f = ((float) (i5 % i4)) / ((float) i4);
                float f2 = ((float) (i5 / i4)) / ((float) i2);
                int red = Color.red(iArr[i5]);
                int green = Color.green(iArr[i5]);
                int blue = Color.blue(iArr[i5]);
                int red2 = Color.red(iArr3[i5]);
                int green2 = Color.green(iArr3[i5]);
                int blue2 = Color.blue(iArr3[i5]);
                if (f > 0.2f && f < 0.8f) {
                    f = 0.2f;
                } else if (((double) f) >= 0.8d) {
                    f = 1.0f - f;
                }
                if (f2 > 0.2f && f2 < 0.8f) {
                    f2 = 0.2f;
                } else if (((double) f2) >= 0.8d) {
                    f2 = 1.0f - f2;
                }
                int i6 = (int) (f * 3000.0f * f2);
                int i7 = alpha;
                int min = Math.min(i7, ((red * i3) * red2) / i7);
                int i8 = alpha;
                int min2 = Math.min(i8, ((green * i3) * green2) / i8);
                int i9 = alpha;
                iArr3[i5] = Color.argb(i6, min, min2, Math.min(i9, ((blue * i3) * blue2) / i9));
            } else {
                int i10 = i2;
            }
        }
    }

    public static Rect computeBounds(Path path, Bitmap bitmap) {
        RectF rectF = new RectF();
        path.computeBounds(rectF, false);
        float f = 0.0f;
        rectF.left = rectF.left >= 0.0f ? rectF.left : 0.0f;
        rectF.top = rectF.top >= 0.0f ? rectF.top : 0.0f;
        rectF.right = rectF.right > ((float) bitmap.getWidth()) ? (float) bitmap.getWidth() : rectF.right;
        rectF.right = rectF.right >= 0.0f ? rectF.right : 0.0f;
        rectF.bottom = rectF.bottom > ((float) bitmap.getHeight()) ? (float) bitmap.getHeight() : rectF.bottom;
        if (rectF.bottom >= 0.0f) {
            f = rectF.bottom;
        }
        rectF.bottom = f;
        return new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    public static Rect computeBoundsWithRadius(Path path, Bitmap bitmap, int i) {
        RectF rectF = new RectF();
        path.computeBounds(rectF, false);
        float f = (float) i;
        rectF.left -= f;
        rectF.top -= f;
        rectF.right += f;
        rectF.bottom += f;
        float f2 = 0.0f;
        rectF.left = rectF.left >= 0.0f ? rectF.left : 0.0f;
        rectF.top = rectF.top >= 0.0f ? rectF.top : 0.0f;
        rectF.right = rectF.right > ((float) bitmap.getWidth()) ? (float) bitmap.getWidth() : rectF.right;
        rectF.right = rectF.right >= 0.0f ? rectF.right : 0.0f;
        rectF.bottom = rectF.bottom > ((float) bitmap.getHeight()) ? (float) bitmap.getHeight() : rectF.bottom;
        if (rectF.bottom >= 0.0f) {
            f2 = rectF.bottom;
        }
        rectF.bottom = f2;
        return new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    static class C06871 implements Comparator {
        C06871() {
        }

        public int compare(Object obj, Object obj2) {
            return ((Comparable) ((Entry) obj).getValue()).compareTo(((Entry) obj2).getValue());
        }
    }
}
