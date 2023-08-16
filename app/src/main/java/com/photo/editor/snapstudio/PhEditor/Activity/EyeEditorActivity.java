package com.photo.editor.snapstudio.PhEditor.Activity;

//import static com.photo.editor.snapstudio.ads.Constant.BANNER_HOME;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.PhEditor.Adapter.ColorListAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.ItemEyesAdapter;
import com.photo.editor.snapstudio.PhEditor.Editor.Landmark;
import com.photo.editor.snapstudio.PhEditor.Editor.MakeUpView;
import com.photo.editor.snapstudio.PhEditor.Editor.MakeUpView.BEAUTY_MODE;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.helper.BeautyHelper;
import com.photo.editor.snapstudio.PhEditor.listener.RecyclerItemClickListener;
import com.photo.editor.snapstudio.PhEditor.listener.RecyclerItemClickListener.OnItemClickListener;
import com.photo.editor.snapstudio.PhEditor.tools.ToolEditor;
import com.photo.editor.snapstudio.PhEditor.views.FaceDetect;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class EyeEditorActivity extends BaseActivity implements OnClickListener, OnTouchListener, ItemEyesAdapter.OnItemEyeSelected {
    //final String TAG = EditLastAct.class.getSimpleName();
    public BEAUTY_MODE beautyMode = BEAUTY_MODE.NONE;
    public BEAUTY_MODE beautyModePrev = BEAUTY_MODE.NONE;
    public Context context = this;
    public MakeUpView makeUpView;
    public Bitmap sourceBitmap;
    int EYE_RESULT_CODE = 100;
    int INDEX_AUTO_FILTER = 3;
    int FOOTER_MODE_PANEL_ACNE = INDEX_AUTO_FILTER;
    int INDEX_COLOR = 5;
    int INDEX_EDIT = 1;
    int FOOTER_MODE_PANEL = INDEX_EDIT;
    int INDEX_FX = 2;
    int MAX_PROGRESS = 35;
    Activity activity = this;
    LinearLayout beautyLayout;
    ImageView beforeAfterEyeLayout;
    LinearLayout bottmLayout;
    ImageView cancelbtn;
    int colorindex = 0;
    int currentProgress = 23;
    ImageView doneBtn;
    ImageView doneFinal;
    int eyeClick = 0;
    SeekBar seekBarSize;
    List<Landmark> faceLandmarks = new ArrayList();
    int[] faceRect;
    boolean isFaceDetected = true;
    boolean isFaceDetecting = true;
    String resultPath;
    Animation slide_down;
    Animation slide_right_in;
    Animation slide_up;
    TextView textViewTitle;
    private ColorListAdapter mAdapter;
    private Context mContext;
    private RecyclerView mRecyclerView;
    ConstraintLayout constraintLayout;
    RecyclerView recyclerViewTools;
    private final ItemEyesAdapter mEditingToolsAdapter = new ItemEyesAdapter(this, this);
    public ToolEditor currentMode = ToolEditor.NONE;

    RelativeLayout mainLayout;
    ImageView close, save;
//    AdsPref adsPref;
//    AdsManager adsManager;
    public void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
//        adsPref = new AdsPref(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_eyes);
        getWindowManager();
        mContext = this;
//        adsManager = new AdsManager(this);
//        adsManager.initializeAd();
//        adsManager.loadBannerAd(BANNER_HOME);

        String stringExtra = getIntent().getStringExtra("imagePath");
        recyclerViewTools = findViewById(R.id.recycler_view_tools);
        this.recyclerViewTools.setLayoutManager(new GridLayoutManager(EyeEditorActivity.this.getApplicationContext(), 4));
        this.recyclerViewTools.setAdapter(this.mEditingToolsAdapter);
        this.recyclerViewTools.setHasFixedSize(true);

        mainLayout = findViewById(R.id.main_layout);
        close = findViewById(R.id.imageViewCloseFinal);
        save = findViewById(R.id.imageViewSaveFinal);
        beautyLayout = (LinearLayout) findViewById(R.id.beautyLayout);
        textViewTitle =  findViewById(R.id.textViewTitle);
        constraintLayout =  findViewById(R.id.constraintLayout);
        ImageView imageView = (ImageView) findViewById(R.id.beforeAfterEyeLayout);
        beforeAfterEyeLayout = imageView;
        imageView.setOnTouchListener(this);
        doneBtn =  findViewById(R.id.imageViewSave);
        doneFinal =findViewById(R.id.imageViewSaveFinal);
        cancelbtn =  findViewById(R.id.imageViewClose);
        doneBtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        doneFinal.setOnClickListener(this);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide_right_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);
        try {
            sourceBitmap = BitmapFactory.decodeStream(new FileInputStream(new File(stringExtra)));
            LayoutParams layoutParams = new LayoutParams(sourceBitmap.getWidth(), sourceBitmap.getHeight());
            layoutParams.addRule(13, -1);
            beautyLayout.setLayoutParams(layoutParams);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView = recyclerView;
            recyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            ColorListAdapter colorListAdapter = new ColorListAdapter(mContext, BeautyHelper.colorPaletteEyeColor, false);
            mAdapter = colorListAdapter;
            mRecyclerView.setAdapter(colorListAdapter);
            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    colorindex = i;
                    if (eyeClick == 1) {
                        EyeEditorActivity eyeActivity = EyeEditorActivity.this;
                        new EyeColorAsyncTask(eyeActivity.makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    }
                }
            }));

            close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            MakeUpView makeUpView2 = new MakeUpView(this, sourceBitmap);
            makeUpView = makeUpView2;
            beautyLayout.addView(makeUpView2);
            new FaceDetectionTask(makeUpView, false).execute(new Void[0]);
            seekBarSize = (SeekBar) findViewById(R.id.seekBarSize);
            bottmLayout = (LinearLayout) findViewById(R.id.bottmLayout);
            seekBarSize.setMax(MAX_PROGRESS);
            seekBarSize.setProgress(currentProgress);
            seekBarSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    currentProgress = i;
                }
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int i = eyeClick;
                    if (i == 1) {
                        EyeEditorActivity eyeActivity = EyeEditorActivity.this;
                        new EyeColorAsyncTask(eyeActivity.makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } else if (i == 2) {
                        EyeEditorActivity eyeActivity2 = EyeEditorActivity.this;
                        new EnlargeEyeAsyncTask(eyeActivity2.makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } else if (i == 3) {
                        EyeEditorActivity eyeActivity3 = EyeEditorActivity.this;
                        new BrightenEyeAsyncTask(eyeActivity3.makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    } else if (i == 4) {
                        EyeEditorActivity eyeActivity4 = EyeEditorActivity.this;
                        new DarkCircleTask(eyeActivity4.makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    }
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
        //    AdUtils.showNativeAd(EyeEditorActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);

        if (isDarkTheme){
            mainLayout.setBackgroundResource(R.color.blacktwo);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        }else {
            mainLayout.setBackgroundResource(R.color.white);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));

        }
    }


    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                if (recyclerViewTools.getVisibility() != View.VISIBLE) {
                    makeUpView.filterCanvas.drawBitmap(makeUpView.sessionBitmap, 0.0f, 0.0f, null);
                    makeUpView.invalidate();
                } else {
                    makeUpView.filterCanvas.drawBitmap(makeUpView.savedSessionBitmap, 0.0f, 0.0f, null);
                    makeUpView.invalidate();
                }
            }
        } else if (recyclerViewTools.getVisibility() != View.VISIBLE) {
            makeUpView.filterCanvas.drawBitmap(makeUpView.savedSessionBitmap, 0.0f, 0.0f, null);
            makeUpView.invalidate();
        } else {
            makeUpView.filterCanvas.drawBitmap(sourceBitmap, 0.0f, 0.0f, null);
            makeUpView.invalidate();
        }
        return true;
    }

    public void onToolEyeSelected(ToolEditor toolType) {
        this.currentMode = toolType;
        if (beforeAfterEyeLayout.getVisibility() == View.GONE) {
            beforeAfterEyeLayout.setVisibility(View.VISIBLE);
            beforeAfterEyeLayout.startAnimation(slide_right_in);
        }
        switch (toolType) {
            case EYES_COLOR:
                textViewTitle.setText("COLOR");
                seekBarSize.setProgress(15);
                constraintLayout.setVisibility(View.GONE);
                if (mRecyclerView.getVisibility() == View.GONE || mRecyclerView.getVisibility() == View.GONE) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                ColorListAdapter colorListAdapter = new ColorListAdapter(mContext, BeautyHelper.colorPaletteEyeColor, false);
                mAdapter = colorListAdapter;
                mRecyclerView.setAdapter(colorListAdapter);
                recyclerViewTools.setVisibility(View.GONE);
                bottmLayout.setVisibility(View.VISIBLE);
                if (seekBarSize.getVisibility() == View.GONE) {
                    seekBarSize.setVisibility(View.VISIBLE);
                }
                bottmLayout.startAnimation(slide_up);
                beautyMode = BEAUTY_MODE.APPLY;
                eyeClick = 1;
                colorindex = 5;
                new EyeColorAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                break;
            case EYES_BRIGHTEN:
                constraintLayout.setVisibility(View.GONE);
                textViewTitle.setText("BRIGHTEN");
                seekBarSize.setProgress(15);
                if (mRecyclerView.getVisibility() == View.VISIBLE) {
                    mRecyclerView.setVisibility(View.GONE);
                }
                if (seekBarSize.getVisibility() == View.GONE) {
                    seekBarSize.setVisibility(View.VISIBLE);
                }
                bottmLayout.setVisibility(View.VISIBLE);
                recyclerViewTools.setVisibility(View.GONE);
                bottmLayout.startAnimation(slide_up);
                eyeClick = 3;
                new BrightenEyeAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                break;
            case EYES_DARK:
                constraintLayout.setVisibility(View.GONE);
                textViewTitle.setText("DARK CIRCLE");
                seekBarSize.setProgress(15);
                eyeClick = 4;
                if (mRecyclerView.getVisibility() == View.VISIBLE) {
                    mRecyclerView.setVisibility(View.GONE);
                }
                bottmLayout.setVisibility(View.VISIBLE);
                if (seekBarSize.getVisibility() == View.GONE) {
                    seekBarSize.setVisibility(View.VISIBLE);
                }
                recyclerViewTools.setVisibility(View.GONE);
                bottmLayout.startAnimation(slide_up);
                new DarkCircleTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                break;
            case EYES_ENLARGE:
                constraintLayout.setVisibility(View.GONE);
                textViewTitle.setText("ENLARGE");
                seekBarSize.setProgress(15);
                if (mRecyclerView.getVisibility() == View.VISIBLE) {
                    mRecyclerView.setVisibility(View.GONE);
                }
                bottmLayout.setVisibility(View.VISIBLE);
                if (seekBarSize.getVisibility() == View.GONE) {
                    seekBarSize.setVisibility(View.VISIBLE);
                }
                recyclerViewTools.setVisibility(View.GONE);
                bottmLayout.startAnimation(slide_up);
                eyeClick = 2;
                new EnlargeEyeAsyncTask(makeUpView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                break;
        }
    }

    public void onClick(View view) {
        if (beforeAfterEyeLayout.getVisibility() == View.GONE) {
            beforeAfterEyeLayout.setVisibility(View.VISIBLE);
            beforeAfterEyeLayout.startAnimation(slide_right_in);
        }
        int id = view.getId();
        if (id != R.id.imageViewClose) {
            switch (id) {
                case R.id.imageViewSave:
                    constraintLayout.setVisibility(View.VISIBLE);
                    MakeUpView makeUpView2 = makeUpView;
                    makeUpView2.savedSessionBitmap = BitmapFactory.decodeFile(makeUpView2.saveBitmap());
                    bottmLayout.setVisibility(View.GONE);
                    recyclerViewTools.setVisibility(View.VISIBLE);
                    recyclerViewTools.startAnimation(slide_up);
                    seekBarSize.setMax(MAX_PROGRESS);
                    return;
                case R.id.imageViewSaveFinal:
                    seekBarSize.setMax(MAX_PROGRESS);
                    resultPath = makeUpView.saveBitmap();
                    Intent intent = new Intent();
                    intent.putExtra("filePath", resultPath);
                    intent.setData(Uri.fromFile(new File(resultPath)));
                    setResult(EYE_RESULT_CODE, intent);
                    finish();
                    return;
                case R.id.imageViewCloseFinal:
                    onBackPressed();
                    return;
                default:
            }
        } else {
            seekBarSize.setMax(MAX_PROGRESS);
            if (recyclerViewTools.getVisibility() == View.VISIBLE) {
                finish();
                return;
            }
            constraintLayout.setVisibility(View.VISIBLE);
            makeUpView.filterCanvas.drawBitmap(makeUpView.savedSessionBitmap, 0.0f, 0.0f, null);
            bottmLayout.setVisibility(View.GONE);
            recyclerViewTools.setVisibility(View.VISIBLE);
            recyclerViewTools.startAnimation(slide_up);
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public class BrightenEyeAsyncTask extends AsyncTask<Void, Void, Void> {
        MakeUpView f208bv;
        public BrightenEyeAsyncTask(MakeUpView makeUpView) {
            f208bv = makeUpView;
        }
        public Void doInBackground(Void... voidArr) {
            f208bv.progress = currentProgress;
            f208bv.sessionCanvas.drawBitmap(f208bv.savedSessionBitmap, 0.0f, 0.0f, null);
            if (f208bv.eyeLeftPath != null) {
                BeautyHelper.brightenEyes(f208bv.sessionCanvas, f208bv.tmpCanvas, f208bv.savedSessionBitmap, f208bv.eyeLeftPath, f208bv.brightLeftRect, f208bv.BpaintPath, f208bv.BrectPaint, f208bv.paintBtm, f208bv.progress * INDEX_FX);
            }
            if (f208bv.eyeRightPath != null) {
                BeautyHelper.brightenEyes(f208bv.sessionCanvas, f208bv.tmpCanvas, f208bv.savedSessionBitmap, f208bv.eyeRightPath, f208bv.brightRightRect, f208bv.BpaintPath, f208bv.BrectPaint, f208bv.paintBtm, f208bv.progress * INDEX_FX);
            }
            return null;
        }
        public void onPostExecute(Void voidR) {
            f208bv.applyFilter();
            f208bv.invalidate();
        }
    }

    public class DarkCircleTask extends AsyncTask<Void, Void, Void> {
        MakeUpView darkMuv;
        public DarkCircleTask(MakeUpView makeUpView) {
            darkMuv = makeUpView;
        }
        public Void doInBackground(Void... voidArr) {
            darkMuv.sessionCanvas.drawBitmap(darkMuv.savedSessionBitmap, 0.0f, 0.0f, null);
            Bitmap removeDarkCircles = BeautyHelper.removeDarkCircles(darkMuv.tmpCanvas, darkMuv.leftEyeBag, darkMuv.leftEyeBagPath, darkMuv.BpaintPath, darkMuv.BrectPaint, currentProgress);
            if (removeDarkCircles != null && !removeDarkCircles.isRecycled()) {
                darkMuv.sessionCanvas.drawBitmap(removeDarkCircles, (float) darkMuv.leftRectBag.left, (float) darkMuv.leftRectBag.top, null);
                Bitmap removeDarkCircles2 = BeautyHelper.removeDarkCircles(darkMuv.tmpCanvas, darkMuv.rightEyeBag, darkMuv.rightEyeBagPath, darkMuv.BpaintPath, darkMuv.BrectPaint, currentProgress);
                if (removeDarkCircles2 != null && !removeDarkCircles2.isRecycled()) {
                    darkMuv.sessionCanvas.drawBitmap(removeDarkCircles2, (float) darkMuv.rightRectBag.left, (float) darkMuv.rightRectBag.top, null);
                    removeDarkCircles2.recycle();
                }
            }
            return null;
        }
        public void onPostExecute(Void voidR) {
            darkMuv.applyFilter();
            darkMuv.invalidate();
        }
    }
    public class EnlargeEyeAsyncTask extends AsyncTask<Void, Void, Void> {
        MakeUpView eyeMuv;
        public EnlargeEyeAsyncTask(MakeUpView makeUpView) {
            eyeMuv = makeUpView;
        }
        public Void doInBackground(Void... voidArr) {
            eyeMuv.progress = currentProgress;
            eyeMuv.sessionCanvas.drawBitmap(eyeMuv.savedSessionBitmap, 0.0f, 0.0f, null);
            if (!eyeMuv.enlargeLeftRect.isEmpty()) {
                BeautyHelper.enlargeEyes(eyeMuv.sessionCanvas, eyeMuv.sessionBitmap, eyeMuv.eyeLeftLandmarks, eyeMuv.enlargeLeftRect, eyeMuv.progress, eyeMuv.paintBtm);
            }
            if (!eyeMuv.enlargeRightRect.isEmpty()) {
                BeautyHelper.enlargeEyes(eyeMuv.sessionCanvas, eyeMuv.sessionBitmap, eyeMuv.eyeRightLandmarks, eyeMuv.enlargeRightRect, eyeMuv.progress, eyeMuv.paintBtm);
            }
            return null;
        }
        public void onPostExecute(Void voidR) {
            eyeMuv.applyFilter();
            eyeMuv.invalidate();
        }
    }
    public class EyeColorAsyncTask extends AsyncTask<Void, Void, Void> {
        MakeUpView eyeColorMuv;
        public EyeColorAsyncTask(MakeUpView makeUpView) {
            eyeColorMuv = makeUpView;
        }
        public Void doInBackground(Void... voidArr) {
            eyeColorMuv.sessionCanvas.drawBitmap(eyeColorMuv.savedSessionBitmap, 0.0f, 0.0f, null);
            if (eyeColorMuv.eyeLensMaskPix == null) {
                return null;
            }
            BeautyHelper.eyeColorBlendMultiply(eyeColorMuv.eyeLensMaskPix, BeautyHelper.colorPaletteEyeColor[colorindex]);
            eyeColorMuv.eyeLensMask.setPixels(eyeColorMuv.eyeLensMaskPix, 0, eyeColorMuv.eyeLensMask.getWidth(), 0, 0, eyeColorMuv.eyeLensMask.getWidth(), eyeColorMuv.eyeLensMask.getHeight());
            if (eyeColorMuv.eyeLens != null && !eyeColorMuv.eyeLens.isRecycled()) {
                eyeColorMuv.eyeLensCanvas = new Canvas(eyeColorMuv.eyeLens);
                new Paint().setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
                if (eyeColorMuv.eyeLensMask != null && !eyeColorMuv.eyeLensMask.isRecycled()) {
                    eyeColorMuv.eyeLensCanvas.drawBitmap(eyeColorMuv.eyeLensMask, 0.0f, 0.0f, null);
                    if (eyeColorMuv.eyeColorLeftBp != null && !eyeColorMuv.eyeColorLeftBp.isRecycled()) {
                        Canvas canvas = new Canvas(eyeColorMuv.eyeColorLeftBp);
                        canvas.drawBitmap(eyeColorMuv.savedSessionBitmap, eyeColorMuv.leftMaskRect, new Rect(0, 0, eyeColorMuv.leftMaskRect.width(), eyeColorMuv.leftMaskRect.height()), null);
                        if (!(eyeColorMuv.eyeLeftLandmarks == null || eyeColorMuv.eyeLeftLandmarks.size() == 0)) {
                            MakeUpView makeUpView = eyeColorMuv;
                            makeUpView.eyeLeftPath = BeautyHelper.drawPath(makeUpView.eyeLeftLandmarks);
                            eyeColorMuv.eyeLeftPath.offset((float) (-eyeColorMuv.leftMaskRect.left), (float) (-eyeColorMuv.leftMaskRect.top));
                            eyeColorMuv.eyeLeftPath.setFillType(FillType.INVERSE_EVEN_ODD);
                            Paint paint = new Paint();
                            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                            canvas.drawPath(eyeColorMuv.eyeLeftPath, paint);
                            Paint paint2 = new Paint();
                            paint2.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                            if (!(eyeColorMuv.eyeLens == null || eyeColorMuv.eyeLens.isRecycled() || eyeColorMuv.pupilLeft == null)) {
                                canvas.drawBitmap(eyeColorMuv.eyeLens, (float) ((eyeColorMuv.pupilLeft.x - (makeUpView.eyeRadius / INDEX_FX)) - eyeColorMuv.leftMaskRect.left), (float) ((eyeColorMuv.pupilLeft.y - (makeUpView.eyeRadius / INDEX_FX)) - eyeColorMuv.leftMaskRect.top), paint2);
                                eyeColorMuv.sessionCanvas.drawBitmap(eyeColorMuv.savedSessionBitmap, 0.0f, 0.0f, null);
                                Paint paint3 = new Paint();
                                eyeColorMuv.progress = currentProgress;
                                paint3.setAlpha(eyeColorMuv.progress * INDEX_COLOR);
                                paint3.setMaskFilter(new BlurMaskFilter(2.0f, Blur.NORMAL));
                                if (eyeColorMuv.eyeColorLeftBp != null && !eyeColorMuv.eyeColorLeftBp.isRecycled()) {
                                    eyeColorMuv.sessionCanvas.drawBitmap(eyeColorMuv.eyeColorLeftBp, (float) eyeColorMuv.leftMaskRect.left, (float) eyeColorMuv.leftMaskRect.top, paint3);
                                    if (eyeColorMuv.eyeColorRightBp != null && !eyeColorMuv.eyeColorRightBp.isRecycled()) {
                                        Canvas canvas2 = new Canvas(eyeColorMuv.eyeColorRightBp);
                                        canvas2.drawBitmap(eyeColorMuv.savedSessionBitmap, eyeColorMuv.rightMaskRect, new Rect(0, 0, eyeColorMuv.rightMaskRect.width(), eyeColorMuv.rightMaskRect.height()), null);
                                        if (!(eyeColorMuv.eyeRightLandmarks == null || eyeColorMuv.eyeRightLandmarks.size() == 0)) {
                                            MakeUpView makeUpView2 = eyeColorMuv;
                                            makeUpView2.eyeRightPath = BeautyHelper.drawPath(makeUpView2.eyeRightLandmarks);
                                            eyeColorMuv.eyeRightPath.offset((float) (-eyeColorMuv.rightMaskRect.left), (float) (-eyeColorMuv.rightMaskRect.top));
                                            eyeColorMuv.eyeRightPath.setFillType(FillType.INVERSE_EVEN_ODD);
                                            canvas2.drawPath(eyeColorMuv.eyeRightPath, paint);
                                            if (!(eyeColorMuv.eyeLens == null || eyeColorMuv.eyeLens.isRecycled() || eyeColorMuv.pupilRight == null)) {
                                                canvas2.drawBitmap(eyeColorMuv.eyeLens, (float) ((eyeColorMuv.pupilRight.x - (makeUpView.eyeRadius / INDEX_FX)) - eyeColorMuv.rightMaskRect.left), (float) ((eyeColorMuv.pupilRight.y - (makeUpView.eyeRadius / INDEX_FX)) - eyeColorMuv.rightMaskRect.top), paint2);
                                                if (eyeColorMuv.eyeColorRightBp != null && !eyeColorMuv.eyeColorRightBp.isRecycled()) {
                                                    eyeColorMuv.sessionCanvas.drawBitmap(eyeColorMuv.eyeColorRightBp, (float) eyeColorMuv.rightMaskRect.left, (float) eyeColorMuv.rightMaskRect.top, paint3);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
        public void onPostExecute(Void voidR) {
            eyeColorMuv.applyFilter();
            eyeColorMuv.invalidate();
        }
    }
    public class FaceDetectionTask extends AsyncTask<Void, Void, Boolean> {
        MakeUpView faceMuv;
        boolean state;
        public FaceDetectionTask(MakeUpView makeUpView, boolean z) {
            faceMuv = makeUpView;
            state = z;
        }
        public Boolean doInBackground(Void... voidArr) {
            try {
                faceLandmarks = BeautyActivity.faceLandmarks;
                Log.e("landmark", String.valueOf(faceLandmarks.size()));
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
                faceMuv.face = new FaceDetect(faceLandmarks, faceRect);
                faceMuv.eyeLeftLandmarks.addAll(faceMuv.face.getLeftEyeLandmarks());
                faceMuv.eyeRightLandmarks.addAll(faceMuv.face.getRightEyeLandmarks());
                faceMuv.faceRect = faceMuv.face.getFaceRect();
                faceMuv.faceRect.left = Math.max(0, faceMuv.faceRect.left - (faceMuv.faceRect.width() / 8));
                faceMuv.faceRect.top = Math.max(0, faceMuv.faceRect.top - (faceMuv.faceRect.height() / INDEX_FX));
                faceMuv.faceRect.right = Math.max(0, Math.min(sourceBitmap.getWidth(), faceMuv.faceRect.right + (faceMuv.faceRect.width() / 8)));
                faceMuv.faceRect.bottom = Math.max(0, Math.min(sourceBitmap.getHeight(), faceMuv.faceRect.bottom + (faceMuv.faceRect.height() / 8)));
            } catch (Exception unused) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Could not find eyes...", Toast.LENGTH_SHORT).show();
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
                if (!makeUpView.isInitEyeColor) {
                    makeUpView.EyeColorInit();
                }
                if (!makeUpView.isInitEnlarge) {
                    makeUpView.EnlargeEyesInit();
                }
                if (!makeUpView.isInitBrighten) {
                    makeUpView.BrightenEyesInit();
                }
                if (!makeUpView.isInitDarkCircle) {
                    makeUpView.DarkCirclesInit();
                }
            }
        }
    }
}
