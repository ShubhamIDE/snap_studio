package com.photo.editor.snapstudio.PhEditor.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.photo.editor.snapstudio.PhEditor.Activity.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResizeImages extends BaseActivity {
    private int imageheight;
    private int imagwidth;
    private int maxResolution;
    private float orientation;
    private Bitmap originalbm = null;

    public Bitmap getScaledBitamp(String str, int i) {
        this.maxResolution = i;
        this.orientation = getImageOrientation(str);
        getAspectRatio(str);
        if (BitmapTransfer.bitmap != null) {
            this.originalbm = BitmapTransfer.bitmap;
        } else { }
        Bitmap resizedOriginalBitmap = getResizedOriginalBitmap(str);
        this.originalbm = resizedOriginalBitmap;
        return resizedOriginalBitmap;
    }

    private float getImageOrientation(String str) {
        try {
            int attributeInt = new ExifInterface(str).getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, 1);
            if (attributeInt == 6) {
                return 90.0f;
            }
            if (attributeInt == 3) {
                return 180.0f;
            }
            if (attributeInt == 8) {
                return 270.0f;
            }
            return 0.0f;
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private void getAspectRatio(String str) {
        float f;
        float f2;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        float f3 = ((float) options.outWidth) / ((float) options.outHeight);
        if (f3 > 1.0f) {
            f2 = (float) this.maxResolution;
            f = f2 / f3;
        } else {
            float f4 = (float) this.maxResolution;
            float f5 = f4;
            f2 = f3 * f4;
            f = f5;
        }
        this.imagwidth = (int) f2;
        this.imageheight = (int) f;
    }

    private Bitmap getResizedOriginalBitmap(String str) {
        try {
            Options options = new Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(str), null, options);
            int i2 = options.outWidth;
            int i3 = options.outHeight;
            int i4 = this.imagwidth;
            int i5 = this.imageheight;
            while (i2 / 2 > i4) {
                i2 /= 2;
                i3 /= 2;
                i *= 2;
            }
            float f = ((float) i4) / ((float) i2);
            float f2 = ((float) i5) / ((float) i3);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inSampleSize = i;
            options.inScaled = false;
            options.inPreferredConfig = Config.ARGB_8888;
            Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(str), null, options);
            Matrix matrix = new Matrix();
            matrix.postScale(f, f2);
            matrix.postRotate(this.orientation);
            return Bitmap.createBitmap(decodeStream, 0, 0, decodeStream.getWidth(), decodeStream.getHeight(), matrix, true);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }


    public String saveBitmap(Context context, String folder, Bitmap bitmap) throws FileNotFoundException {
        File localFile = saveBitMap(context, bitmap, folder);

        if (localFile != null && localFile.exists()) {
            return localFile.getAbsolutePath();
        }
        return "";

    }


    private File saveBitMap(Context context, Bitmap Final_bitmap, String folder) {


        ContextWrapper cw = new ContextWrapper(context);
        File pictureFileDir = cw.getDir(folder, Context.MODE_PRIVATE);
        String filename = File.separator + System.currentTimeMillis() + ".jpg";

        File pictureFile = new File(pictureFileDir, filename);

        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            Final_bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }

        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    // Toast.makeText(cntx, "Save Image Successfully..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue scanning gallery.");
        }
    }


}
