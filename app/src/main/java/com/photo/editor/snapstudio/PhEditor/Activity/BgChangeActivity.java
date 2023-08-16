package com.photo.editor.snapstudio.PhEditor.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.PhEditor.Adapter.BgAdapter;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.crop.MLCropAsyncTask;
import com.photo.editor.snapstudio.PhEditor.crop.MLOnCropTaskCompleted;
import com.photo.editor.snapstudio.PhEditor.listener.LayoutItemListener;
import com.photo.editor.snapstudio.PhEditor.listener.MultiTouchListener;
import com.photo.editor.snapstudio.PhEditor.support.Constants;
import com.photo.editor.snapstudio.PhEditor.utils.BitmapTransfer;
import com.photo.editor.snapstudio.PhEditor.utils.ColorFilterGenerator;
import com.photo.editor.snapstudio.PhEditor.utils.DripUtils;
import com.photo.editor.snapstudio.PhEditor.utils.ImageUtils;
import com.photo.editor.snapstudio.PhEditor.views.MyExceptionHandlerPix;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BgChangeActivity extends BaseActivity implements LayoutItemListener {
    public static Bitmap eraserResultBmp;
    private static Bitmap faceBitmap;
    private Bitmap OverLayBackground = null;
    private Bitmap cutBitmap;
    public int count = 0;
    boolean isFirstTime = true;
    private Bitmap selectedBit;
    private Context mContext;
    private int wing = 15;
    private RecyclerView recyclerViewStyle;
    private ArrayList<String> wingsEffect = new ArrayList<String>();
    private ImageView imageViewWings, imageViewBackground;
    private RelativeLayout mContentRootView;
    public LinearLayout ll_img_brightness;
    public LinearLayout linearLayoutAdjust;
    private LinearLayout ll_img_contrast;
    private LinearLayout ll_img_exposure;
    private LinearLayout ll_img_hue;
    private LinearLayout ll_img_saturation;
    public SeekBar seekbar_adjust_color;
    private ImageView img_brightness;
    private ImageView img_contrast;
    private ImageView img_exposure;
    private ImageView img_hue;
    private ImageView img_saturation;
    private BgAdapter wingsAdapter;
    String bgurl;
    public View indicator_brightness;

    public View indicator_contrast;

    public View indicator_exposure;

    public View indicator_hue;

    public View indicator_saturation;

    public LinearLayout horizontal_scroll_view;
    ConstraintLayout constraintLayoutAdjust, mainLayout;
    ImageView close, save, imageViewClean;
    TextView textView, brightness_txt, contrast_txt, saturation_txt, exposure_txt, hue_txt;

    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_background_change);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(BgChangeActivity.this));
        mContext = this;
        bgurl = getIntent().getStringExtra("url");
        selectedBit = faceBitmap;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                imageViewBackground.post(new Runnable() {
                    public void run() {
                        if (isFirstTime && selectedBit != null) {
                            isFirstTime = false;
                            //create bitmap for image
                            initBMPNew();
                        }
                    }
                });
            }
        }, 1000);

        // wingsEffect.add("none");

        for (int i = 1; i <= wing; i++) {
            wingsEffect.add("b" + i);
        }
        Init();
        imageViewBackground.setOnTouchListener(new MultiTouchListener(this, true));
        findViewById(R.id.imageViewCloseWings).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.imageViewSaveWings).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                new saveFile().execute();
            }
        });

        findViewById(R.id.imageViewEraser).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EraseActivity.b = selectedBit;
                Intent intent = new Intent(BgChangeActivity.this, EraseActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_BG);
                startActivityForResult(intent, 1024);
            }
        });

        setAdjustment();
        changeTheme();
    }

    private void initBMPNew() {
        if (faceBitmap != null) {
            selectedBit = ImageUtils.getBitmapResize(mContext, faceBitmap, imageViewBackground.getWidth(), imageViewBackground.getHeight());
            mContentRootView.setLayoutParams(new LinearLayout.LayoutParams(imageViewWings.getWidth(), imageViewWings.getHeight()));
            setStartWings();
        }
    }

    private void Init() {
        mContentRootView = findViewById(R.id.mContentRootView);
        imageViewWings = findViewById(R.id.imageViewWings);
        imageViewBackground = findViewById(R.id.imageViewBackground);

        if (!bgurl.isEmpty()) {
            Picasso.get().load(bgurl).into(imageViewWings);
        } else {
            imageViewWings.setImageResource(R.drawable.b1);
        }
        /*;*/
//        }
        recyclerViewStyle = (RecyclerView) findViewById(R.id.recyclerViewStyle);
        recyclerViewStyle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        wingsAdapter = new BgAdapter(mContext);
        wingsAdapter.setMenuItemClickLister(this);
        recyclerViewStyle.setAdapter(wingsAdapter);
        wingsAdapter.addData(wingsEffect);


        imageViewBackground.post(new Runnable() {
            public void run() {
                initBMPNew();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (eraserResultBmp != null) {
                cutBitmap = eraserResultBmp;
                imageViewBackground.setImageBitmap(cutBitmap);
            }
        }
    }


    public void onLayoutListClick(View view, int i) {
        Bitmap bitmapFromAsset = DripUtils.getBitmapFromAsset(this, "bg/" + this.wingsAdapter.getItemList().get(i) + ".png");
        if (!"none".equals(wingsAdapter.getItemList().get(i))) {
            OverLayBackground = bitmapFromAsset;
            imageViewWings.setImageBitmap(OverLayBackground);
            return;
        }
        this.OverLayBackground = null;
    }

    public void setStartWings() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final ProgressBar progressBar2 = progressBar;
        new CountDownTimer(5000, 1000) {
            public void onFinish() {
            }

            public void onTick(long j) {
                int unused = count = count + 1;
                if (progressBar2.getProgress() <= 90) {
                    progressBar2.setProgress(count * 5);
                }
            }
        }.start();

        new MLCropAsyncTask(new MLOnCropTaskCompleted() {
            public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int left, int top) {
                int[] iArr = {0, 0, selectedBit.getWidth(), selectedBit.getHeight()};
                int width = selectedBit.getWidth();
                int height = selectedBit.getHeight();
                int i = width * height;
                selectedBit.getPixels(new int[i], 0, width, 0, 0, width, height);
                int[] iArr2 = new int[i];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                createBitmap.setPixels(iArr2, 0, width, 0, 0, width, height);
                cutBitmap = ImageUtils.getMask(mContext, selectedBit, createBitmap, width, height);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap, cutBitmap.getWidth(), cutBitmap.getHeight(), false);
                cutBitmap = resizedBitmap;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Palette p = Palette.from(cutBitmap).generate();
                        if (p.getDominantSwatch() == null) {
                            Toast.makeText(BgChangeActivity.this, getString(R.string.txt_not_detect_human), Toast.LENGTH_SHORT).show();
                        }
                        imageViewBackground.setImageBitmap(cutBitmap);
                    }
                });


            }
        }, this, progressBar).execute(new Void[0]);
    }


    private class saveFile extends android.os.AsyncTask<String, Bitmap, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public Bitmap getBitmapFromView(View view) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            mContentRootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = getBitmapFromView(mContentRootView);
            try {
                return bitmap;
            } catch (Exception e) {
                return null;
            } finally {
                mContentRootView.setDrawingCacheEnabled(false);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                BitmapTransfer.bitmap = bitmap;
            }
            Intent intent = new Intent(BgChangeActivity.this, EditorActivity.class);
            intent.putExtra("MESSAGE", "done");
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    private void changeTheme() {
        boolean isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        // AdUtils.showNativeAd(BgChangeActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);

        if (isDarkTheme) {
            mainLayout.setBackgroundResource(R.color.blacktwo);
            imageViewClean.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            textView.setTextColor(getResources().getColor(R.color.white));
            img_brightness.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            img_contrast.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            img_hue.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            img_saturation.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            img_exposure.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            saturation_txt.setTextColor(getResources().getColor(R.color.white));
            hue_txt.setTextColor(getResources().getColor(R.color.white));
            exposure_txt.setTextColor(getResources().getColor(R.color.white));
            contrast_txt.setTextColor(getResources().getColor(R.color.white));
            brightness_txt.setTextColor(getResources().getColor(R.color.white));

        } else {
            mainLayout.setBackgroundResource(R.color.white);
            imageViewClean.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            textView.setTextColor(getResources().getColor(R.color.blacktwo));
            img_brightness.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            img_contrast.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            img_hue.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            img_saturation.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            img_exposure.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            saturation_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            hue_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            exposure_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            contrast_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            brightness_txt.setTextColor(getResources().getColor(R.color.blacktwo));

        }
    }


    public void setAdjustment() {
        this.mainLayout = findViewById(R.id.main_layout);
        save = findViewById(R.id.imageViewSaveWings);
        imageViewClean = findViewById(R.id.imageViewClean);
        close = findViewById(R.id.imageViewCloseWings);
        textView = findViewById(R.id.textViewTitle);
        brightness_txt = findViewById(R.id.brightness_txt);
        contrast_txt = findViewById(R.id.contrast_txt);
        exposure_txt = findViewById(R.id.exposure_txt);
        hue_txt = findViewById(R.id.hue_txt);
        saturation_txt = findViewById(R.id.saturation_txt);
        this.linearLayoutAdjust = findViewById(R.id.linearLayoutAdjust);
        this.constraintLayoutAdjust = findViewById(R.id.constraintAdjust);
        this.horizontal_scroll_view = findViewById(R.id.linearLayoutAdj);
        this.img_brightness = (ImageView) findViewById(R.id.img_brightness);
        this.img_contrast = (ImageView) findViewById(R.id.img_contrast);
        this.img_saturation = (ImageView) findViewById(R.id.img_saturation);
        this.img_exposure = (ImageView) findViewById(R.id.img_exposure);
        this.img_hue = (ImageView) findViewById(R.id.img_hue);
        this.seekbar_adjust_color = (SeekBar) findViewById(R.id.seekbar_adjust);
        this.ll_img_brightness = (LinearLayout) findViewById(R.id.ll_img_brightness);
        this.ll_img_contrast = (LinearLayout) findViewById(R.id.ll_img_contrast);
        this.ll_img_saturation = (LinearLayout) findViewById(R.id.ll_img_saturation);
        this.ll_img_exposure = (LinearLayout) findViewById(R.id.ll_img_exposure);
        this.ll_img_hue = (LinearLayout) findViewById(R.id.ll_img_hue);
        this.indicator_brightness = findViewById(R.id.indicator_brightness);
        this.indicator_contrast = findViewById(R.id.indicator_contrast);
        this.indicator_saturation = findViewById(R.id.indicator_saturation);
        this.indicator_exposure = findViewById(R.id.indicator_exposure);
        this.indicator_hue = findViewById(R.id.indicator_hue);

        this.findViewById(R.id.imageViewClean).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewBackground.setColorFilter(null);
            }
        });

        this.seekbar_adjust_color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @SuppressLint("WrongConstant")
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                int i2 = 0;
                if (indicator_brightness.getVisibility() == 0) {
                    if (i != 100) {
                        i2 = i - 100;
                    }
                    imageViewBackground.setColorFilter(ColorFilterGenerator.adjustBrightness(i2));
                } else if (indicator_contrast.getVisibility() == 0) {
                    if (i != 100) {
                        i2 = i - 100;
                    }
                    imageViewBackground.setColorFilter(ColorFilterGenerator.adjustContrast((float) i2));
                } else if (indicator_saturation.getVisibility() == 0) {
                    if (i != 100) {
                        i2 = i - 100;
                    }
                    imageViewBackground.setColorFilter(ColorFilterGenerator.adjustSaturation(i2));
                } else if (indicator_exposure.getVisibility() == 0) {
                    if (i != 100) {
                        i2 = i - 100;
                    }
                    imageViewBackground.setColorFilter(ColorFilterGenerator.adjustExposure((float) i2));
                } else if (indicator_hue.getVisibility() == 0) {
                    if (i != 100) {
                        i2 = i - 100;
                    }
                    imageViewBackground.setColorFilter(ColorFilterGenerator.adjustHue((float) i2));
                }
            }
        });

        this.ll_img_brightness.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                saveEffect();
                resetMainIndicatorView();
                indicator_brightness.setVisibility(0);
                horizontal_scroll_view.requestChildFocus(ll_img_brightness, ll_img_brightness);
                adjustSeekbarMaxAndProgress();
                constraintLayoutAdjust.setVisibility(0);
                if (seekbar_adjust_color.getVisibility() != 0) {
                    seekbar_adjust_color.setVisibility(0);
                }
            }
        });
        this.ll_img_contrast.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                saveEffect();
                resetMainIndicatorView();
                indicator_contrast.setVisibility(0);
                horizontal_scroll_view.requestChildFocus(indicator_contrast, indicator_contrast);
                adjustSeekbarMaxAndProgress();
                constraintLayoutAdjust.setVisibility(0);
                if (seekbar_adjust_color.getVisibility() != 0) {
                    seekbar_adjust_color.setVisibility(0);
                }
            }
        });
        this.ll_img_saturation.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                saveEffect();
                resetMainIndicatorView();
                indicator_saturation.setVisibility(0);
                horizontal_scroll_view.requestChildFocus(indicator_saturation, indicator_saturation);
                adjustSeekbarMaxAndProgress();
                constraintLayoutAdjust.setVisibility(0);
                if (seekbar_adjust_color.getVisibility() != 0) {
                    seekbar_adjust_color.setVisibility(0);
                }
            }
        });
        this.ll_img_exposure.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                saveEffect();
                resetMainIndicatorView();
                indicator_exposure.setVisibility(0);
                horizontal_scroll_view.requestChildFocus(indicator_exposure, indicator_exposure);
                adjustSeekbarMaxAndProgress();
                constraintLayoutAdjust.setVisibility(0);
                if (seekbar_adjust_color.getVisibility() != 0) {
                    seekbar_adjust_color.setVisibility(0);
                }
            }
        });
        this.ll_img_hue.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                saveEffect();
                resetMainIndicatorView();
                indicator_hue.setVisibility(0);
                horizontal_scroll_view.requestChildFocus(indicator_hue, indicator_hue);
                adjustSeekbarMaxAndProgress();
                constraintLayoutAdjust.setVisibility(0);
                if (seekbar_adjust_color.getVisibility() != 0) {
                    seekbar_adjust_color.setVisibility(0);
                }
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void saveEffect() {
        if (this.indicator_brightness.getVisibility() == 0) {
            Bitmap cacheBitMap1 = getCacheBitMap(imageViewBackground);
            imageViewBackground.setColorFilter((ColorFilter) null);
            imageViewBackground.setImageBitmap(cacheBitMap1);
            this.seekbar_adjust_color.setVisibility(8);
        } else if (this.indicator_contrast.getVisibility() == 0) {
            Bitmap cacheBitMap_2 = getCacheBitMap(imageViewBackground);
            imageViewBackground.setColorFilter((ColorFilter) null);
            imageViewBackground.setImageBitmap(cacheBitMap_2);
            this.seekbar_adjust_color.setVisibility(8);
        } else if (this.indicator_saturation.getVisibility() == 0) {
            Bitmap cacheBitMap_3 = getCacheBitMap(imageViewBackground);
            imageViewBackground.setColorFilter((ColorFilter) null);
            imageViewBackground.setImageBitmap(cacheBitMap_3);
            this.seekbar_adjust_color.setVisibility(8);
        } else if (this.indicator_exposure.getVisibility() == 0) {
            Bitmap cacheBitMap_4 = getCacheBitMap(imageViewBackground);
            imageViewBackground.setColorFilter((ColorFilter) null);
            imageViewBackground.setImageBitmap(cacheBitMap_4);
            this.seekbar_adjust_color.setVisibility(8);
        } else if (this.indicator_hue.getVisibility() == 0) {
            Bitmap cacheBitMap_5 = getCacheBitMap(imageViewBackground);
            imageViewBackground.setColorFilter((ColorFilter) null);
            imageViewBackground.setImageBitmap(cacheBitMap_5);
            this.seekbar_adjust_color.setVisibility(8);
        }
    }

    @SuppressLint("WrongConstant")
    public void resetMainIndicatorView() {
        this.indicator_brightness.setVisibility(4);
        this.indicator_contrast.setVisibility(4);
        this.indicator_exposure.setVisibility(4);
        this.indicator_hue.setVisibility(4);
        this.indicator_saturation.setVisibility(4);
    }

    @SuppressLint("WrongConstant")
    public void adjustSeekbarMaxAndProgress() {
        if (this.indicator_brightness.getVisibility() == 0) {
            this.seekbar_adjust_color.setMax(200);
            this.seekbar_adjust_color.setProgress(100);
        } else if (this.indicator_contrast.getVisibility() == 0) {
            this.seekbar_adjust_color.setMax(200);
            this.seekbar_adjust_color.setProgress(100);
        } else if (this.indicator_saturation.getVisibility() == 0) {
            this.seekbar_adjust_color.setMax(200);
            this.seekbar_adjust_color.setProgress(100);
        } else if (this.indicator_exposure.getVisibility() == 0) {
            this.seekbar_adjust_color.setMax(200);
            this.seekbar_adjust_color.setProgress(100);
        } else if (this.indicator_hue.getVisibility() == 0) {
            this.seekbar_adjust_color.setMax(200);
            this.seekbar_adjust_color.setProgress(100);
        }
    }

    public static Bitmap getCacheBitMap(ImageView imageView) {
        try {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap createBitmap = Bitmap.createBitmap(imageView.getDrawingCache());
            imageView.destroyDrawingCache();
            imageView.setDrawingCacheEnabled(false);
            return createBitmap;
        } catch (Exception unused) {
            return null;
        }
    }
}
