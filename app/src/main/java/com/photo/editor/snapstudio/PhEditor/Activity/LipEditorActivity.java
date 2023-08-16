package com.photo.editor.snapstudio.PhEditor.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.PhEditor.Activity.BeautyActivity.Options;
import com.photo.editor.snapstudio.PhEditor.Adapter.ColorListAdapter;
import com.photo.editor.snapstudio.PhEditor.Editor.Landmark;
import com.photo.editor.snapstudio.PhEditor.Editor.MakeUpView;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.helper.BeautyHelper;
import com.photo.editor.snapstudio.PhEditor.listener.RecyclerItemClickListener;
import com.photo.editor.snapstudio.PhEditor.listener.RecyclerItemClickListener.OnItemClickListener;
import com.photo.editor.snapstudio.PhEditor.views.FaceDetect;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class LipEditorActivity extends BaseActivity implements OnTouchListener {
    public int LIPS_RESULT_CODE = 200;
    public Options SELECTED_OPTION;
    public MakeUpView makeUpView;
    int MAX_PROGRESS = 25;
    int colorProgress = 15;
    int colorindex = 0;
    Context context = this;
    List<Landmark> faceLandmarks = new ArrayList();
    int[] faceRect;
    boolean isFaceDetected = true;
    boolean isFaceDetecting = true;
    boolean isMouthOpen = true;
    int measuredHeight = 0;
    int measuredWidth = 0;
    Bitmap sourceBitmap;
    private ColorListAdapter colorListAdapter;
    private ImageView compareView;
    private TextView textViewTitle;
    RelativeLayout mainLayout;
    ImageView close, save;
    boolean isDarkTheme;
//    AdsPref adsPref;
//    AdsManager adsManager;
    public void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
//        adsPref = new AdsPref(this);
        super.onCreate(bundle);
        WindowManager windowManager = getWindowManager();
        faceLandmarks.clear();
        setContentView(R.layout.activity_lips);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
//        adsManager = new AdsManager(this);
//        adsManager.initializeAd();
//        adsManager.loadBannerAd(BANNER_HOME);
        isDarkTheme = getIntent().getBooleanExtra("isdark", false);
        String stringExtra = getIntent().getStringExtra("imagePath");
        SELECTED_OPTION = (Options) getIntent().getSerializableExtra("isBlushChecked");

        save = findViewById(R.id.imageViewSave);
        close = findViewById(R.id.imageViewClose);
        mainLayout = findViewById(R.id.main_layout);
        changeTheme();

        try {
            sourceBitmap = BitmapFactory.decodeStream(new FileInputStream(new File(stringExtra)));
            makeUpView = new MakeUpView(this, sourceBitmap);
            ImageView imageView = (ImageView) findViewById(R.id.beforeAfteLipLayout);
            compareView = imageView;
            imageView.setOnTouchListener(this);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            if (SELECTED_OPTION.equals(Options.LIPCOLOR)) {
                textViewTitle.setText("LIPS");
                colorListAdapter = new ColorListAdapter(context, BeautyHelper.colorPaletteLip, false);
            } else if (SELECTED_OPTION.equals(Options.BLUSH)) {
                colorListAdapter = new ColorListAdapter(context, BeautyHelper.colorPaletteBlush, false);
                textViewTitle.setText("CHEEK");
            } else if (SELECTED_OPTION.equals(Options.FOUNDATION)) {
                colorListAdapter = new ColorListAdapter(context, BeautyHelper.colorPaletteBase, false);
                textViewTitle.setText("FACE");
            }
            recyclerView.setAdapter(colorListAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    colorindex = i;
                    if (SELECTED_OPTION.equals(Options.LIPCOLOR)) {
                        new LipColorAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } else if (SELECTED_OPTION.equals(Options.BLUSH)) {
                        new BlushAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } else if (SELECTED_OPTION.equals(Options.FOUNDATION)) {
                       new BaseTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    }
                }
            }));
            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarSize);
            seekBar.setMax(MAX_PROGRESS);
            seekBar.setProgress(colorProgress);
            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    colorProgress = i;
                }
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (SELECTED_OPTION.equals(Options.LIPCOLOR)) {
                        new LipColorAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } else if (SELECTED_OPTION.equals(Options.BLUSH)) {
                       new BlushAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } else if (SELECTED_OPTION.equals(Options.FOUNDATION)) {
                        new BaseTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    }
                }
            });
            findViewById(R.id.imageViewSave).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    String saveBitmap = makeUpView.saveBitmap();
                    Intent intent = new Intent();
                    intent.setData(Uri.fromFile(new File(saveBitmap)));
                    intent.putExtra("filePath", saveBitmap);
                    setResult(LIPS_RESULT_CODE, intent);
                    finish();
                }
            });

            findViewById(R.id.imageViewClose).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onBackPressed();
                    finish();
                }
            });

            ((LinearLayout) findViewById(R.id.beautyView)).addView(makeUpView);
            new FaceDetectionTask(makeUpView, false).execute(new Void[0]);
            if (VERSION.SDK_INT >= 13) {
                Point point = new Point();
                windowManager.getDefaultDisplay().getSize(point);
                measuredWidth = point.x;
                measuredHeight = point.y;
                return;
            }
            Display defaultDisplay = windowManager.getDefaultDisplay();
            measuredWidth = defaultDisplay.getWidth();
            measuredHeight = defaultDisplay.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Image not supported ", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void changeTheme() {
//        isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        //  AdUtils.showNativeAd(LipEditorActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);
        if (isDarkTheme){
            mainLayout.setBackgroundResource(R.color.blacktwo);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            textViewTitle.setTextColor(getResources().getColor(R.color.white));
        }else {
            mainLayout.setBackgroundResource(R.color.white);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            textViewTitle.setTextColor(getResources().getColor(R.color.blacktwo));
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            makeUpView.filterCanvas.drawBitmap(sourceBitmap, 0.0f, 0.0f, null);
            makeUpView.invalidate();
        } else if (action == 1) {
            makeUpView.filterCanvas.drawBitmap(makeUpView.sessionBitmap, 0.0f, 0.0f, null);
            makeUpView.invalidate();
        } else if (action == 2) {
            makeUpView.filterCanvas.drawBitmap(sourceBitmap, 0.0f, 0.0f, null);
            makeUpView.invalidate();
        }
        return true;
    }

    public class BaseTask extends AsyncTask<Void, Void, Void> {
        MakeUpView baseMuv;

        public BaseTask(MakeUpView makeUpView) {
            baseMuv = makeUpView;
        }

        public void onPreExecute() {
        }

        public Void doInBackground(Void... voidArr) {
            if (!(baseMuv.baseDrawPaint == null || baseMuv.border == null)) {
                baseMuv.sessionCanvas.drawBitmap(baseMuv.savedSessionBitmap, 0.0f, 0.0f, null);
                baseMuv.progress = colorProgress;
                baseMuv.baseDrawPaint.setAlpha(baseMuv.progress * 10);
                int[] iArr = new int[(baseMuv.faceRect.width() * baseMuv.faceRect.height())];
                baseMuv.basePaint.setColor(BeautyHelper.colorPaletteBase[colorindex]);
                baseMuv.baseCanvas.drawOval(new RectF(0.0f, 0.0f, (float) baseMuv.faceRect.width(), (float) baseMuv.faceRect.height()), baseMuv.basePaint);
                Paint paint = new Paint();
                paint.setColor(-1);
                paint.setMaskFilter(new BlurMaskFilter(10.0f, Blur.NORMAL));
                baseMuv.baseCanvas.drawPath(baseMuv.baseEyePathLeft, paint);
                baseMuv.baseCanvas.drawPath(baseMuv.baseEyePathRight, paint);
                baseMuv.baseCanvas.drawPath(baseMuv.baseMouthPath, paint);
                paint.setMaskFilter(new BlurMaskFilter((float) (baseMuv.faceRect.width() / 12), Blur.NORMAL));
                baseMuv.baseCanvas.drawPath(baseMuv.border, paint);
                baseMuv.baseCanvas.drawPath(baseMuv.border1, paint);
                baseMuv.baseCanvas.drawPath(baseMuv.border2, paint);
                baseMuv.baseCanvas.drawPath(baseMuv.border3, paint);
                baseMuv.savedSessionBitmap.getPixels(iArr, 0, baseMuv.faceRect.width(), baseMuv.faceRect.left, baseMuv.faceRect.top, baseMuv.faceRect.width(), baseMuv.faceRect.height());
                baseMuv.baseBitmap.getPixels(baseMuv.basePix, 0, baseMuv.faceRect.width(), 0, 0, baseMuv.faceRect.width(), baseMuv.faceRect.height());
                BeautyHelper.colorBlendBase(iArr, baseMuv.basePix, baseMuv.faceRect.width(), baseMuv.faceRect.height(), colorindex == 0 ? 2 : 1);
                baseMuv.baseBitmap.setPixels(baseMuv.basePix, 0, baseMuv.faceRect.width(), 0, 0, baseMuv.faceRect.width(), baseMuv.faceRect.height());
                if (baseMuv.baseBitmap != null && !baseMuv.baseBitmap.isRecycled()) {
                    baseMuv.sessionCanvas.drawBitmap(baseMuv.baseBitmap, (float) baseMuv.faceRect.left, (float) baseMuv.faceRect.top, baseMuv.baseDrawPaint);
                }
            }
            return null;
        }
        public void onPostExecute(Void voidR) {
            baseMuv.applyFilter();
            baseMuv.invalidate();
        }
    }

    public class BlushAsyncTask extends AsyncTask<Void, Void, Void> {
        MakeUpView blushMuv;
        public BlushAsyncTask(MakeUpView makeUpView) {
            blushMuv = makeUpView;
        }

        public void onPreExecute() {
        }

        public Void doInBackground(Void... voidArr) {
            if (blushMuv.blushDrawPaint != null) {
                blushMuv.sessionCanvas.drawBitmap(blushMuv.savedSessionBitmap, 0.0f, 0.0f, null);
                blushMuv.progress = colorProgress;
                blushMuv.blushDrawPaint.setAlpha(blushMuv.progress * 6);
                int width = blushMuv.blushLeftRect.width() * blushMuv.blushLeftRect.height();
                int[] iArr = new int[width];
                blushMuv.blushPaint.setColor(BeautyHelper.colorPaletteBlush[colorindex]);
                blushMuv.blushLeftCanvas.drawPath(blushMuv.blushLeftPath, blushMuv.blushPaint);
                blushMuv.savedSessionBitmap.getPixels(iArr, 0, blushMuv.blushLeftRect.width(), blushMuv.blushLeftRect.left, blushMuv.blushLeftRect.top, blushMuv.blushLeftRect.width(), blushMuv.blushLeftRect.height());
                blushMuv.blushLeftBitmap.getPixels(blushMuv.blushLeftPix, 0, blushMuv.blushLeftRect.width(), 0, 0, blushMuv.blushLeftRect.width(), blushMuv.blushLeftRect.height());
                blushMuv.blushLeftBitmap.setPixels(blushMuv.blushLeftPix, 0, blushMuv.blushLeftRect.width(), 0, 0, blushMuv.blushLeftRect.width(), blushMuv.blushLeftRect.height());
                if (blushMuv.blushLeftBitmap != null && !blushMuv.blushLeftBitmap.isRecycled()) {
                    blushMuv.sessionCanvas.drawBitmap(blushMuv.blushLeftBitmap, (float) blushMuv.blushLeftRect.left, (float) blushMuv.blushLeftRect.top, blushMuv.blushDrawPaint);
                    for (int i = 0; i < width; i++) {
                        iArr[i] = 0;
                    }
                    blushMuv.blushLeftBitmap.setPixels(iArr, 0, blushMuv.blushLeftRect.width(), 0, 0, blushMuv.blushLeftRect.width(), blushMuv.blushLeftRect.height());
                    int width2 = blushMuv.blushRightRect.width() * blushMuv.blushRightRect.height();
                    int[] iArr2 = new int[width2];
                    blushMuv.blushRightCanvas.drawPath(blushMuv.blushRightPath, blushMuv.blushPaint);
                    blushMuv.savedSessionBitmap.getPixels(iArr2, 0, blushMuv.blushRightRect.width(), blushMuv.blushRightRect.left, blushMuv.blushRightRect.top, blushMuv.blushRightRect.width(), blushMuv.blushRightRect.height());
                    blushMuv.blushRightBitmap.getPixels(blushMuv.blushRightPix, 0, blushMuv.blushRightRect.width(), 0, 0, blushMuv.blushRightRect.width(), blushMuv.blushRightRect.height());
                    blushMuv.blushRightBitmap.setPixels(blushMuv.blushRightPix, 0, blushMuv.blushRightRect.width(), 0, 0, blushMuv.blushRightRect.width(), blushMuv.blushRightRect.height());
                    if (blushMuv.blushRightBitmap != null && !blushMuv.blushRightBitmap.isRecycled()) {
                        blushMuv.sessionCanvas.drawBitmap(blushMuv.blushRightBitmap, (float) blushMuv.blushRightRect.left, (float) blushMuv.blushRightRect.top, blushMuv.blushDrawPaint);
                        for (int i2 = 0; i2 < width2; i2++) {
                            iArr2[i2] = 0;
                        }
                        blushMuv.blushRightBitmap.setPixels(iArr2, 0, blushMuv.blushRightRect.width(), 0, 0, blushMuv.blushRightRect.width(), blushMuv.blushRightRect.height());
                    }
                }
            }
            return null;
        }


        public void onPostExecute(Void voidR) {
            blushMuv.applyFilter();
            blushMuv.invalidate();
        }
    }

    public class FaceDetectionTask extends AsyncTask<Void, Void, Boolean> {
        MakeUpView faceDetectMuv;
        boolean state;
        public FaceDetectionTask(MakeUpView makeUpView, boolean z) {
            faceDetectMuv = makeUpView;
            state = z;
        }


        public Boolean doInBackground(Void... voidArr) {
            try {
                faceLandmarks = BeautyActivity.faceLandmarks;
                int[] iArr = BeautyActivity.faceRects;
                faceRect = new int[4];
                int i = 0;
                for (int i2 = 0; i2 < iArr.length / 4; i2++) {
                    int i3 = i2 * 4;
                    int i4 = i3 + 2;
                    int i5 = i3 + 3;
                    int i6 = i3 + 1;
                    int i7 = (iArr[i4] - iArr[i3]) * (iArr[i5] - iArr[i6]);
                    if (i7 > i) {
                        faceRect[0] = Math.max(0, iArr[i3]);
                        faceRect[1] = Math.min(iArr[i6], sourceBitmap.getWidth());
                        faceRect[2] = Math.max(0, iArr[i4]);
                        faceRect[3] = Math.min(iArr[i5], sourceBitmap.getHeight());
                        i = i7;
                    }
                }
                faceDetectMuv.face = new FaceDetect(faceLandmarks, faceRect);
                if (SELECTED_OPTION.equals(Options.LIPCOLOR)) {
                    faceDetectMuv.mouthOutLandmarks.addAll(faceDetectMuv.face.getOuterMouthLandmarks());
                    faceDetectMuv.mouthInLandmarks.addAll(faceDetectMuv.face.getInnerMouthLandmarks());
                } else if (SELECTED_OPTION.equals(Options.BLUSH)) {
                    faceDetectMuv.slimLeftLandmarks.addAll(faceDetectMuv.face.getLeftSlimLandmarks());
                    faceDetectMuv.slimRightLandmarks.addAll(faceDetectMuv.face.getRightSlimLandmarks());
                } else if (SELECTED_OPTION.equals(Options.FOUNDATION)) {
                    faceDetectMuv.mouthOutLandmarks.addAll(faceDetectMuv.face.getOuterMouthLandmarks());
                    faceDetectMuv.mouthInLandmarks.addAll(faceDetectMuv.face.getInnerMouthLandmarks());
                    faceDetectMuv.eyeRightLandmarks.addAll(faceDetectMuv.face.getRightEyeLandmarks());
                    faceDetectMuv.eyeLeftLandmarks.addAll(faceDetectMuv.face.getLeftEyeLandmarks());
                }
                faceDetectMuv.faceRect = faceDetectMuv.face.getFaceRect();
                faceDetectMuv.faceRect.left = Math.max(0, faceDetectMuv.faceRect.left - (faceDetectMuv.faceRect.width() / 8));
                faceDetectMuv.faceRect.top = Math.max(0, faceDetectMuv.faceRect.top - (faceDetectMuv.faceRect.height() / 2));
                faceDetectMuv.faceRect.right = Math.max(0, Math.min(sourceBitmap.getWidth(), faceDetectMuv.faceRect.right + (faceDetectMuv.faceRect.width() / 8)));
                faceDetectMuv.faceRect.bottom = Math.max(0, Math.min(sourceBitmap.getHeight(), faceDetectMuv.faceRect.bottom + (faceDetectMuv.faceRect.height() / 8)));
            } catch (Exception unused) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (SELECTED_OPTION.equals(Options.LIPCOLOR)) {
                            Toast.makeText(context, "Could not find lips...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Could not find face...", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
            }
            return Boolean.valueOf(true);
        }
        public void onPreExecute() {
            isFaceDetecting = true;
        }
        public void onPostExecute(Boolean bool) {
            if (bool.booleanValue()) {
                isFaceDetected = true;
                isFaceDetecting = false;
                isFaceDetected = false;
                isMouthOpen = true;
                if (isFaceDetecting) {
                    return;
                }
                if (SELECTED_OPTION.equals(Options.LIPCOLOR)) {
                    if (!makeUpView.isInitLip) {
                        makeUpView.LipInit();
                    }
                    new LipColorAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                } else if (SELECTED_OPTION.equals(Options.BLUSH)) {
                    if (!makeUpView.isInitBlush) {
                        makeUpView.BlushInit(1);
                    }
                    new BlushAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                } else if (SELECTED_OPTION.equals(Options.FOUNDATION)) {
                    if (!makeUpView.isInitBase) {
                        makeUpView.baseInit();
                    }
                    new BaseTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                }
            }
        }
    }

    public class LipColorAsyncTask extends AsyncTask<Void, Void, Void> {
        MakeUpView lipColorMuv;
        public LipColorAsyncTask(MakeUpView makeUpView) {
            lipColorMuv = makeUpView;
        }
        public void onPreExecute() {
        }
        public Void doInBackground(Void... voidArr) {
            if (!(lipColorMuv.lipOuterPaint == null || lipColorMuv.lipOutPath == null)) {
                lipColorMuv.lipOuterPaint.setColor(BeautyHelper.colorPaletteLip[colorindex]);
                lipColorMuv.progress = colorProgress;
                lipColorMuv.lipCanvas.drawPath(lipColorMuv.lipOutPath, lipColorMuv.lipOuterPaint);
                if (isMouthOpen) {
                    lipColorMuv.lipCanvas.drawPath(lipColorMuv.lipInPath, lipColorMuv.lipInnerPaint);
                }
                int width = lipColorMuv.lipRect.width() * lipColorMuv.lipRect.height();
                int[] iArr = new int[width];
                lipColorMuv.lipMaskBitmap.getPixels(iArr, 0, lipColorMuv.lipRect.width(), 0, 0, lipColorMuv.lipRect.width(), lipColorMuv.lipRect.height());
                BeautyHelper.colorBlendMultiply(lipColorMuv.lipPixels, iArr);
                lipColorMuv.lipMaskBitmap.setPixels(iArr, 0, lipColorMuv.lipRect.width(), 0, 0, lipColorMuv.lipRect.width(), lipColorMuv.lipRect.height());
                lipColorMuv.sessionCanvas.drawBitmap(lipColorMuv.savedSessionBitmap, 0.0f, 0.0f, lipColorMuv.paintBtm);
                lipColorMuv.lipDrawPaint.setAlpha(lipColorMuv.progress * 6);
                if (lipColorMuv.lipMaskBitmap != null && !lipColorMuv.lipMaskBitmap.isRecycled()) {
                    lipColorMuv.sessionCanvas.drawBitmap(lipColorMuv.lipMaskBitmap, (float) lipColorMuv.lipRect.left, (float) lipColorMuv.lipRect.top, lipColorMuv.lipDrawPaint);
                    for (int i = 0; i < width; i++) {
                        iArr[i] = 0;
                    }
                    lipColorMuv.lipMaskBitmap.setPixels(iArr, 0, lipColorMuv.lipRect.width(), 0, 0, lipColorMuv.lipRect.width(), lipColorMuv.lipRect.height());
                }
            }
            return null;
        }


        public void onPostExecute(Void voidR) {
            lipColorMuv.applyFilter();
            lipColorMuv.invalidate();
        }
    }
}
