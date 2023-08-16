package com.photo.editor.snapstudio.PhEditor.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.PhEditor.Adapter.DripColorAdapter;
import com.photo.editor.snapstudio.BuildConfig;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.draw.SplashBrushView;
import com.photo.editor.snapstudio.PhEditor.draw.SplashView;
import com.photo.editor.snapstudio.PhEditor.utils.BitmapTransfer;
import com.photo.editor.snapstudio.PhEditor.views.MyExceptionHandlerPix;
import com.photo.editor.snapstudio.PhEditor.views.SupportedClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SplashActivity extends BaseActivity implements OnSeekBarChangeListener,
        DripColorAdapter.ColorListener {
    public static final int REQUEST_CODE_CAMERA = 0x2;
    public static final int REQUEST_CODE_GALLERY = 0x3;
    public static Bitmap colorBitmap;
    public static Bitmap grayBitmap;
    public String selectedImagePath;
    public String selectedOutputPath;
    public static String drawPath;
    public Uri selectedImageUri;
    public static int displayHight;
    public static int displayWidth;
    public static Vector vector;
    private Runnable runnableCode;
    DripColorAdapter tetxColorAdapter;
    public static SplashBrushView brushView;
    private RelativeLayout relativeLayoutContainer;
    private RelativeLayout linearLayoutColors;
    private ImageView imageViewColor;
    private ImageView imageViewGray;
    private ImageView imageViewManual;
    private ImageView imageViewZoom;
    private TextView textViewSize;
    private TextView textViewOpacity;
    private RecyclerView recyclerViewColor;
    public static SeekBar seekBarOpacity;
    public static SeekBar seekBarSize;
    public static SplashView splashView;
    private List<DripColorAdapter.SquareView> squareViewListSaved = new ArrayList();
    //    AdsPref adsPref;
//    AdsManager adsManager;
    ConstraintLayout mainLayout;
    ImageView close, save;
    TextView textView, opacity_txt, size_txt;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
//        adsPref = new AdsPref(this);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash_effect);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(SplashActivity.this));
//        adsManager = new AdsManager(this);
//        adsManager.initializeAd();
//        adsManager.loadInterstitialAd(REWARDED_SHOW, adsPref.getInterstitialAdInterval());
        relativeLayoutContainer = findViewById(R.id.relativeLayoutContainer);
        brushView = findViewById(R.id.brushView);
        vector = new Vector();
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        displayWidth = point.x;
        displayHight = point.y;
        splashView = findViewById(R.id.drawingImageView);
        if (BitmapTransfer.bitmap != null) {
            colorBitmap = BitmapTransfer.bitmap;
        } else {
        }
        save = findViewById(R.id.imageViewSaveSplash);
        close = findViewById(R.id.imageViewCloseSplash);
        textView = findViewById(R.id.text);
        mainLayout = findViewById(R.id.main_layout);
        opacity_txt = findViewById(R.id.opacity_txt);
        size_txt = findViewById(R.id.size_txt);
        grayBitmap = grayScaleBitmap(colorBitmap);
        imageViewColor = findViewById(R.id.imageViewColor);
        imageViewGray = findViewById(R.id.imageViewGray);
        imageViewManual = findViewById(R.id.imageViewManual);
        imageViewZoom = findViewById(R.id.imageViewZoom);
        textViewSize = findViewById(R.id.textViewSizeValue);
        textViewOpacity = findViewById(R.id.textViewOpacityValue);
        linearLayoutColors = findViewById(R.id.linearLayoutColors);
        seekBarSize = findViewById(R.id.seekBarSize);
        seekBarOpacity = findViewById(R.id.seekBarOpacity);
        seekBarSize.setMax(100);
        seekBarOpacity.setMax(240);
        seekBarSize.setProgress((int) splashView.radius);
        seekBarOpacity.setProgress(splashView.opacity);
        seekBarSize.setOnSeekBarChangeListener(this);
        seekBarOpacity.setOnSeekBarChangeListener(this);
        splashView.initDrawing();
        final Handler handler = new Handler();
        this.runnableCode = new Runnable() {
            public void run() {
                handler.postDelayed(runnableCode, 2000);
            }
        };
        handler.post(this.runnableCode);

        this.recyclerViewColor = findViewById(R.id.recyclerViewColor);
        this.recyclerViewColor.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerViewColor.setAdapter(new DripColorAdapter(this, this));
        this.recyclerViewColor.setVisibility(View.VISIBLE);

        findViewById(R.id.imageViewSaveSplash).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (splashView.drawingBitmap != null) {
                    BitmapTransfer.bitmap = splashView.drawingBitmap;
                }
                Intent intent = new Intent(SplashActivity.this, EditorActivity.class);
                intent.putExtra("MESSAGE", "done");
                setResult(RESULT_OK, intent);
                finish();

//                adsManager.showInterstitialAd();
            }
        });

        findViewById(R.id.imageViewCloseSplash).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        findViewById(R.id.imageViewManual).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewManual.setBackgroundResource(R.drawable.background_selected_color);
                imageViewColor.setBackgroundResource(R.drawable.background_unslelected);
                imageViewZoom.setBackgroundResource(R.drawable.background_unslelected);
                imageViewGray.setBackgroundResource(R.drawable.background_unslelected);
                imageViewManual.setColorFilter(getResources().getColor(R.color.pink));
                imageViewColor.setColorFilter(getResources().getColor(R.color.white));
                imageViewZoom.setColorFilter(getResources().getColor(R.color.white));
                imageViewGray.setColorFilter(getResources().getColor(R.color.white));
                linearLayoutColors.setVisibility(View.VISIBLE);
                splashView.mode = 0;
                splashView.splashBitmap = grayScaleBitmap(colorBitmap);
                splashView.updateRefMetrix();
                splashView.changeShaderBitmap();
                splashView.coloring = -2;

                tetxColorAdapter = (DripColorAdapter) recyclerViewColor.getAdapter();
                if (tetxColorAdapter != null) {
                    tetxColorAdapter.setSelectedColorIndex(0);
                }
                if (tetxColorAdapter != null) {
                    tetxColorAdapter.notifyDataSetChanged();
                }
            }
        });

        findViewById(R.id.imageViewColor).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewColor.setBackgroundResource(R.drawable.background_selected_color);
                imageViewManual.setBackgroundResource(R.drawable.background_unslelected);
                imageViewZoom.setBackgroundResource(R.drawable.background_unslelected);
                imageViewGray.setBackgroundResource(R.drawable.background_unslelected);
                imageViewColor.setColorFilter(getResources().getColor(R.color.pink));
                imageViewManual.setColorFilter(getResources().getColor(R.color.white));
                imageViewZoom.setColorFilter(getResources().getColor(R.color.white));
                imageViewGray.setColorFilter(getResources().getColor(R.color.white));
                linearLayoutColors.setVisibility(View.GONE);
                splashView.mode = 0;
                SplashView splashView = SplashActivity.splashView;
                splashView.splashBitmap = colorBitmap;
                splashView.updateRefMetrix();
                SplashActivity.splashView.changeShaderBitmap();
                SplashActivity.splashView.coloring = -1;
            }
        });

        findViewById(R.id.imageViewGray).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewGray.setBackgroundResource(R.drawable.background_selected_color);
                imageViewColor.setBackgroundResource(R.drawable.background_unslelected);
                imageViewZoom.setBackgroundResource(R.drawable.background_unslelected);
                imageViewManual.setBackgroundResource(R.drawable.background_unslelected);
                imageViewGray.setColorFilter(getResources().getColor(R.color.pink));
                imageViewColor.setColorFilter(getResources().getColor(R.color.white));
                imageViewZoom.setColorFilter(getResources().getColor(R.color.white));
                imageViewManual.setColorFilter(getResources().getColor(R.color.white));
                linearLayoutColors.setVisibility(View.GONE);
                splashView.mode = 0;
                splashView.splashBitmap = grayScaleBitmap(colorBitmap);
                splashView.updateRefMetrix();
                splashView.changeShaderBitmap();
                splashView.coloring = -2;
            }
        });

        findViewById(R.id.imageViewReset).setOnClickListener(view -> {
            imageViewColor.setBackgroundResource(R.drawable.background_selected_color);
            imageViewManual.setBackgroundResource(R.drawable.background_unslelected);
            imageViewZoom.setBackgroundResource(R.drawable.background_unslelected);
            imageViewGray.setBackgroundResource(R.drawable.background_unslelected);
            imageViewColor.setColorFilter(getResources().getColor(R.color.pink));
            imageViewManual.setColorFilter(getResources().getColor(R.color.white));
            imageViewZoom.setColorFilter(getResources().getColor(R.color.white));
            imageViewGray.setColorFilter(getResources().getColor(R.color.white));
            linearLayoutColors.setVisibility(View.GONE);
            SplashActivity.grayBitmap = grayScaleBitmap(SplashActivity.colorBitmap);
            SplashActivity.splashView.initDrawing();
            SplashActivity.splashView.saveScale = 1.0f;
            SplashActivity.splashView.fitScreen();
            splashView.mode = 0;
            SplashActivity.splashView.updatePreviewPaint();
            SplashActivity.splashView.updatePaintBrush();
            SplashActivity.vector.clear();
        });

        findViewById(R.id.imageViewFit).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SplashView touchImageView = SplashActivity.splashView;
                touchImageView.saveScale = 1.0f;
                touchImageView.radius = ((float) (seekBarSize.getProgress() + 10)) / SplashActivity.splashView.saveScale;
                SplashActivity.splashView.fitScreen();
                SplashActivity.splashView.updatePreviewPaint();
            }
        });

        findViewById(R.id.imageViewZoom).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewZoom.setBackgroundResource(R.drawable.background_selected_color);
                imageViewManual.setBackgroundResource(R.drawable.background_unslelected);
                imageViewColor.setBackgroundResource(R.drawable.background_unslelected);
                imageViewGray.setBackgroundResource(R.drawable.background_unslelected);
                imageViewZoom.setColorFilter(getResources().getColor(R.color.pink));
                imageViewManual.setColorFilter(getResources().getColor(R.color.white));
                imageViewColor.setColorFilter(getResources().getColor(R.color.white));
                imageViewGray.setColorFilter(getResources().getColor(R.color.white));
                splashView.mode = 1;
            }
        });
        changeTheme();
    }

    private void changeTheme() {
        boolean isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        //  AdUtils.showNativeAd(SplashActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);

        if (isDarkTheme){
            mainLayout.setBackgroundResource(R.color.blacktwo);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            textView.setTextColor(getResources().getColor(R.color.white));
            textViewOpacity.setTextColor(getResources().getColor(R.color.white));
            textViewSize.setTextColor(getResources().getColor(R.color.white));
            opacity_txt.setTextColor(getResources().getColor(R.color.white));
            size_txt.setTextColor(getResources().getColor(R.color.white));
        }else {
            mainLayout.setBackgroundResource(R.color.white);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            textView.setTextColor(getResources().getColor(R.color.blacktwo));
            textViewOpacity.setTextColor(getResources().getColor(R.color.blacktwo));
            textViewSize.setTextColor(getResources().getColor(R.color.blacktwo));
            opacity_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            size_txt.setTextColor(getResources().getColor(R.color.blacktwo));
        }
    }


    @Override
    public void onColorSelected(int item, DripColorAdapter.SquareView squareView) {
        if (squareView.isColor) {
            SplashActivity colorSplashActivity = SplashActivity.this;
            SplashActivity.grayBitmap = colorSplashActivity.grayScaleBitmap(SplashActivity.colorBitmap);
            Canvas canvas = new Canvas(SplashActivity.grayBitmap);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(new float[]{((float) ((squareView.drawableId >> 16) & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) ((squareView.drawableId >> 8) & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) (squareView.drawableId & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) ((squareView.drawableId >> 24) & 255)) / 256.0f, 0.0f}));
            canvas.drawBitmap(SplashActivity.grayBitmap, 0.0f, 0.0f, paint);
            SplashActivity.splashView.splashBitmap = SplashActivity.grayBitmap;
            SplashActivity.splashView.updateRefMetrix();
            SplashActivity.splashView.changeShaderBitmap();
            SplashActivity.splashView.coloring = squareView.drawableId;
            squareViewListSaved.add(squareView);
        } else {
            SplashActivity colorSplashActivity = SplashActivity.this;
            SplashActivity.grayBitmap = colorSplashActivity.grayScaleBitmap(SplashActivity.colorBitmap);
            Canvas canvas = new Canvas(SplashActivity.grayBitmap);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(new float[]{((float) ((squareView.drawableId >> 16) & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) ((squareView.drawableId >> 8) & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) (squareView.drawableId & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) ((squareView.drawableId >> 24) & 255)) / 256.0f, 0.0f}));
            canvas.drawBitmap(SplashActivity.grayBitmap, 0.0f, 0.0f, paint);
            SplashActivity.splashView.splashBitmap = SplashActivity.grayBitmap;
            SplashActivity.splashView.updateRefMetrix();
            SplashActivity.splashView.changeShaderBitmap();
            SplashActivity.splashView.coloring = squareView.drawableId;
            squareViewListSaved.add(squareView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public Bitmap grayScaleBitmap(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        if (id == R.id.seekBarOpacity) {
            SplashBrushView brushView2 = brushView;
            brushView2.isBrushSize = false;
            brushView2.setShapeRadiusRatio(splashView.radius);
            brushView.brushSize.setPaintOpacity(seekBarOpacity.getProgress());
            brushView.invalidate();
            SplashView splashView = SplashActivity.splashView;
            splashView.opacity = i + 15;
            splashView.updatePaintBrush();
            String value = String.valueOf(i);
            textViewOpacity.setText(value);

        } else if (id == R.id.seekBarSize) {
            String sb = seekBarSize.getProgress() + "";
            Log.wtf("radious :", sb);
            splashView.radius = ((float) (seekBarSize.getProgress() + 10)) / splashView.saveScale;
            splashView.updatePaintBrush();
            String value = String.valueOf(i);
            textViewSize.setText(value);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            selectedImagePath = selectedOutputPath;
            if (SupportedClass.stringIsNotEmpty(selectedImagePath)) {
                File fileImageClick = new File(selectedImagePath);
                if (fileImageClick.exists()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        selectedImageUri = Uri.fromFile(fileImageClick);
                    } else {
                        selectedImageUri = FileProvider.getUriForFile(SplashActivity.this, BuildConfig.APPLICATION_ID + ".provider", fileImageClick);
                    }
                    onPhotoTakenApp();
                }
            }
        } else if (data != null && data.getData() != null) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                selectedImageUri = data.getData();
            } else {
                selectedImagePath = selectedOutputPath;
            }
            if (SupportedClass.stringIsNotEmpty(selectedImagePath)) {
                onPhotoTakenApp();
            }

        } else {
            Log.e("TAG", "");
        }
    }

    public void onPhotoTakenApp() {
        relativeLayoutContainer.post(new Runnable() {
            @Override
            public void run() {
                grayBitmap = grayScaleBitmap(colorBitmap);
                splashView.initDrawing();
                splashView.saveScale = 1.0f;
                splashView.fitScreen();
                splashView.updatePreviewPaint();
                splashView.updatePaintBrush();
                vector.clear();
            }
        });
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint(1);
        paint.setColor(-16711936);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

}
