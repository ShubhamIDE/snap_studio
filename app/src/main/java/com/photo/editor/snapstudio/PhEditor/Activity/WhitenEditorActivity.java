package com.photo.editor.snapstudio.PhEditor.Activity;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.constants.Constants;
import com.photo.editor.snapstudio.PhEditor.draw.ImgViewTouch;
import com.photo.editor.snapstudio.PhEditor.draw.ImgViewTouchAndDraw;
import com.photo.editor.snapstudio.PhEditor.views.GPUImgFilterTool;
import com.yalantis.ucrop.view.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;

public class WhitenEditorActivity extends BaseActivity implements OnTouchListener {
    public static Bitmap whittencolorBitmap;
    public Bitmap bitmap = null;
    public Boolean btnOnclick = Boolean.valueOf(false);
    public String imagePath;
    public boolean isZoomRequired = false;
    protected float f220mX;
    protected float f221mY;
    protected Path tmpPath;
    Context context;
    private Bitmap Colorized = null;
    private int WHITTEN_RESULT_CODE = CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION;
    private int currentMode = 1;
    private int imageViewHeight = 0;
    private int imageViewWidth = 0;
    private boolean isFirstTimeLaunch = false;
    private File isfile;
    private ImgViewTouch mImageView;
    private ImgViewTouch mImageView1;
    private Paint mPaint;
    private Canvas myCombineCanvas = null;
    private Bitmap myCombinedBitmap = null;
    private Context myContext;
    private Canvas pcanvas;
    private Bitmap topImage = null;

    RelativeLayout relativeLayoutWhiten;
    RelativeLayout relativeLayoutEraser;
    RelativeLayout relativeLayoutZoom;
    public static float[] getMatrixValues(Matrix matrix) {
        float[] fArr = new float[9];
        matrix.getValues(fArr);
        return fArr;
    }
    RelativeLayout mainLayout;
    ImageView close, save, imageView, imageView2, imageView3;
    TextView textView, textView2, textView3;

    public void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
//        adsPref = new AdsPref(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_whiten);
        context = this;
//            adsManager = new AdsManager(this);
//            adsManager.initializeAd();
//            adsManager.loadBannerAd(BANNER_HOME);

        String stringExtra = getIntent().getStringExtra("imagePath");
        imagePath = stringExtra;
        try {
            Bitmap decodeFile = BitmapFactory.decodeFile(stringExtra);
            topImage = decodeFile;
            imageViewWidth = decodeFile.getWidth();
            imageViewHeight = topImage.getHeight();
            mImageView = (ImgViewTouchAndDraw) findViewById(R.id.whittenimg);
            mImageView1 = (ImgViewTouchAndDraw) findViewById(R.id.whittenimg1);
            mImageView1.setOnTouchListener(this);
            myContext = getBaseContext();
            Colorized = toColor(topImage);
            initFunction();
            imageView = findViewById(R.id.image_view_whiten);
            imageView2 = findViewById(R.id.image_view_eraser);
            imageView3 = findViewById(R.id.image_view_zoom);
            textView = findViewById(R.id.text_view_whiten);
            textView2 = findViewById(R.id.text_view_eraser);
            textView3 = findViewById(R.id.text_view_zoom);
            relativeLayoutWhiten = findViewById(R.id.relativeLayoutWhiten);
            relativeLayoutEraser = findViewById(R.id.relativeLayoutEraser);
            relativeLayoutZoom = findViewById(R.id.relativeLayoutZoom);
            save = findViewById(R.id.imageViewSaveFinal);
            close = findViewById(R.id.imageViewCloseFinal);
            mainLayout = findViewById(R.id.relative_parent);

            relativeLayoutWhiten.setBackgroundResource(R.drawable.background_item_selected);
            relativeLayoutEraser.setBackgroundResource(R.drawable.background_item);
            relativeLayoutZoom.setBackgroundResource(R.drawable.background_item);
            findViewById(R.id.imageViewSaveFinal).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (btnOnclick.booleanValue()) {
                        WhitenEditorActivity whittenFaceActivity = WhitenEditorActivity.this;
                        whittenFaceActivity.bitmap = whittenFaceActivity.createFinalImage();
                        WhitenEditorActivity whittenFaceActivity2 = WhitenEditorActivity.this;
                        whittenFaceActivity2.imagePath = whittenFaceActivity2.saveBitmap(whittenFaceActivity2.bitmap);
//                        file = new File(whittenFaceActivity2.saveBitmap(whittenFaceActivity2.bitmap));
                    }
                    WhitenEditorActivity whittenFaceActivity3 = WhitenEditorActivity.this;
                    whittenFaceActivity3.SendURL(whittenFaceActivity3.imagePath);
//                    whittenFaceActivity3.SendURL(file);
                }
            });

            findViewById(R.id.imageViewCloseFinal).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onBackPressed();
                    finish();
                }
            });

            findViewById(R.id.relativeLayoutWhiten).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    iWhiten();
                }
            });

            findViewById(R.id.relativeLayoutEraser).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    iEraser();
                }
            });

            findViewById(R.id.relativeLayoutZoom).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    iZoom();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Image not supported ", Toast.LENGTH_LONG).show();
            finish();
        }
        changeTheme();
    }

    private void changeTheme() {
        boolean isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        //   AdUtils.showNativeAd(WhitenEditorActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);

        if (isDarkTheme){
            mainLayout.setBackgroundResource(R.color.blacktwo);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            imageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            imageView2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            imageView3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            textView.setTextColor(getResources().getColor(R.color.blacktwo));
            textView2.setTextColor(getResources().getColor(R.color.blacktwo));
            textView3.setTextColor(getResources().getColor(R.color.blacktwo));

        }else {
            mainLayout.setBackgroundResource(R.color.white);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));


            imageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            imageView2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            imageView3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            textView.setTextColor(getResources().getColor(R.color.white));
            textView2.setTextColor(getResources().getColor(R.color.white));
            textView3.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void iWhiten() {
        relativeLayoutWhiten.setBackgroundResource(R.drawable.background_item_selected);
        relativeLayoutEraser.setBackgroundResource(R.drawable.background_item);
        relativeLayoutZoom.setBackgroundResource(R.drawable.background_item);
        btnOnclick = Boolean.valueOf(true);
        isZoomRequired = false;
        drawMode(1, 1);
    }

    private void iEraser() {
        relativeLayoutWhiten.setBackgroundResource(R.drawable.background_item);
        relativeLayoutEraser.setBackgroundResource(R.drawable.background_item_selected);
        relativeLayoutZoom.setBackgroundResource(R.drawable.background_item);
        btnOnclick = Boolean.valueOf(true);
        isZoomRequired = false;
        drawMode(1, 2);
    }

    private void iZoom() {
        relativeLayoutWhiten.setBackgroundResource(R.drawable.background_item);
        relativeLayoutEraser.setBackgroundResource(R.drawable.background_item);
        relativeLayoutZoom.setBackgroundResource(R.drawable.background_item_selected);
        if (!isZoomRequired) {
            isZoomRequired = true;
        }
    }

    public void SendURL(String str) {
        File file = new File(str);
        if (file.exists()) {
            Uri fromFile = Uri.fromFile(file);
            Intent intent = new Intent();
            intent.setData(fromFile);
            if (btnOnclick.booleanValue()) {
                setResult(WHITTEN_RESULT_CODE, intent);
            } else {
                setResult(0, intent);
            }
            btnOnclick = Boolean.valueOf(false);
            finish();
        }
    }

    public void combinedTopImage(Bitmap bitmap2, Bitmap bitmap3) {
        Bitmap bitmap4 = myCombinedBitmap;
        if (bitmap4 != null) {
            bitmap4.recycle();
            myCombinedBitmap = null;
        }
        myCombinedBitmap = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas();
        myCombineCanvas = canvas;
        canvas.setBitmap(myCombinedBitmap);
        myCombineCanvas.drawBitmap(bitmap2, 0.0f, 0.0f, null);
        myCombineCanvas.drawBitmap(bitmap3, 0.0f, 0.0f, null);
        Bitmap bitmap5 = bitmap;
        if (bitmap5 != null) {
            bitmap5.recycle();
            bitmap = null;
            bitmap = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Config.ARGB_8888);
        }
        Canvas canvas2 = new Canvas();
        pcanvas = canvas2;
        canvas2.setBitmap(bitmap);
        pcanvas.drawBitmap(myCombinedBitmap, 0.0f, 0.0f, null);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        btnOnclick = Boolean.valueOf(true);
        ImgViewTouchAndDraw imageViewTouchAndDraw = (ImgViewTouchAndDraw) view;
        if (isZoomRequired) {
            imageViewTouchAndDraw.setScaleType(ScaleType.MATRIX);
            mImageView.setScaleType(ScaleType.MATRIX);
            try {
                ((ImgViewTouchAndDraw) mImageView).setDrawMode(ImgViewTouchAndDraw.TouchMode.IMAGE);
                ((ImgViewTouchAndDraw) mImageView1).setDrawMode(ImgViewTouchAndDraw.TouchMode.IMAGE);
                mImageView.onTouchEvent(motionEvent);
                return imageViewTouchAndDraw.onTouchEvent(motionEvent);
            } catch (IllegalArgumentException unused) {
                return false;
            }
        } else {
            Matrix matrix = new Matrix();
            Matrix matrix2 = new Matrix(imageViewTouchAndDraw.getImageMatrix());
            matrix.reset();
            float[] matrixValues = getMatrixValues(matrix2);
            matrix2.invert(matrix2);
            float[] matrixValues2 = getMatrixValues(matrix2);
            matrix.postTranslate(-matrixValues[2], -matrixValues[5]);
            matrix.postScale(matrixValues2[0], matrixValues2[4]);
            pcanvas.setMatrix(matrix);
            int action = motionEvent.getAction();
            String str = "onTouchEvent";
            if (action == 0) {
                touch_start(motionEvent.getX(), motionEvent.getY());
                Log.e(str, "000000000000000");
            } else if (action == 1) {
                touch_up();
                Log.e(str, "1111111111111");
            } else if (action == 2) {
                Log.e(str, "2222222222222");
                touch_move(motionEvent.getX(), motionEvent.getY());
            }
            imageViewTouchAndDraw.setImageBitmap(bitmap);
            return true;
        }
    }

    public void drawMode(int i, int i2) {
        if (isFirstTimeLaunch) {
            isFirstTimeLaunch = false;
            if (i2 == 1) {
                mImageView.setImageBitmapReset(Colorized, true);
                mImageView1.setImageBitmapReset(bitmap, true);
            } else if (i2 == 2) {
                mImageView.setImageBitmapReset(topImage, true);
                mImageView1.setImageBitmapReset(bitmap, true);
            }
            return;
        }
        if (i != 0) {
            combinedTopImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap(), ((BitmapDrawable) mImageView1.getDrawable()).getBitmap());
        }
        if (i2 == 1) {
            mImageView.setImageBitmap(Colorized);
            mImageView1.setImageBitmap(bitmap);
            mImageView1.setOnTouchListener(this);
        } else if (i2 == 2) {
            mImageView.setImageBitmap(topImage);
            mImageView1.setImageBitmap(bitmap);
            mImageView1.setOnTouchListener(this);
        }
    }

    public void initFunction() {
        Paint paint = new Paint();
        mPaint = paint;
        paint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeJoin(Join.ROUND);
        mPaint.setStrokeCap(Cap.ROUND);
        mPaint.setStrokeWidth(30.0f);
        mPaint.setMaskFilter(new BlurMaskFilter(15.0f, Blur.NORMAL));
        mPaint.setFilterBitmap(false);
        tmpPath = new Path();
        bitmap = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas();
        pcanvas = canvas;
        canvas.setBitmap(bitmap);
        pcanvas.drawBitmap(topImage, 0.0f, 0.0f, null);
        isFirstTimeLaunch = true;
        drawMode(0, currentMode);
    }

    public Bitmap toColor(Bitmap bitmap2) {
        try {
            GPUImgFilterTool gPUImageFilterTools = new GPUImgFilterTool();
            GPUImage gPUImage = new GPUImage(this);
            gPUImage.setFilter(gPUImageFilterTools.ApplyColorBlending(context));
            gPUImage.setImage(bitmap2);
            return gPUImage.getBitmapWithFilterApplied(bitmap2);
        } catch (Exception | OutOfMemoryError unused) {
            return null;
        }
    }

    private void touch_start(float f, float f2) {
        tmpPath.reset();
        tmpPath.moveTo(f, f2);
        f220mX = f;
        f221mY = f2;
        mPaint.setStrokeWidth(55.0f);
    }

    private void touch_move(float f, float f2) {
        float abs = Math.abs(f - f220mX);
        float abs2 = Math.abs(f2 - f221mY);
        if (abs >= 1.0f || abs2 >= 1.0f) {
            Path path = tmpPath;
            float f3 = f220mX;
            float f4 = f221mY;
            path.quadTo(f3, f4, (f3 + f) / 2.0f, (f4 + f2) / 2.0f);
            pcanvas.drawPath(tmpPath, mPaint);
            tmpPath.reset();
            tmpPath.moveTo((f220mX + f) / 2.0f, (f221mY + f2) / 2.0f);
            f220mX = f;
            f221mY = f2;
        }
    }

    private void touch_up() {
        tmpPath.reset();
    }


    public Bitmap createFinalImage() {
        Bitmap createBitmap = Bitmap.createBitmap(topImage.getWidth(), topImage.getHeight(), Config.ARGB_8888);
        Bitmap bitmap2 = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        Canvas canvas = new Canvas();
        canvas.setBitmap(createBitmap);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, null);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        return createBitmap;
    }

    public void onDestroy() {
        super.onDestroy();
        Bitmap bitmap2 = bitmap;
        if (bitmap2 != null && !bitmap2.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
        Bitmap bitmap3 = whittencolorBitmap;
        if (bitmap3 != null && !bitmap3.isRecycled()) {
            whittencolorBitmap.recycle();
            whittencolorBitmap = null;
            System.gc();
        }
        Bitmap bitmap4 = myCombinedBitmap;
        if (bitmap4 != null && !bitmap4.isRecycled()) {
            myCombinedBitmap.recycle();
            myCombinedBitmap = null;
            System.gc();
        }
        Bitmap bitmap5 = Colorized;
        if (bitmap5 != null && !bitmap5.isRecycled()) {
            Colorized.recycle();
            Colorized = null;
            System.gc();
        }
        Bitmap bitmap6 = topImage;
        if (bitmap6 != null && !bitmap6.isRecycled()) {
            topImage.recycle();
            topImage = null;
            System.gc();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        setResult(0, new Intent());
        btnOnclick = Boolean.valueOf(false);
        finish();
    }

    public String saveBitmap(Bitmap bitmap2) {
        File file = null;
        String folder = Constants.TEMP_FOLDER_NAME;

        ContextWrapper cw = new ContextWrapper(context);
        File pictureFileDir = cw.getDir(folder, Context.MODE_PRIVATE);


        try {
            StringBuilder sb = new StringBuilder();
            sb.append(folder);
            sb.append(".jpg");
            imagePath = sb.toString();
            file = new File(pictureFileDir, imagePath);
            isfile = file;
            if (file.exists()) {
                isfile.delete();
                isfile.createNewFile();
            } else {
                isfile.createNewFile();
            }
            FileOutputStream oStream = new FileOutputStream(file);
            bitmap2.compress(CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        return String.valueOf(file);
    }

}
