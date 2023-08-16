package com.photo.editor.snapstudio.PhEditor.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
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
import com.photo.editor.snapstudio.PhEditor.Adapter.WingsAdapter;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.crop.MLCropAsyncTask;
import com.photo.editor.snapstudio.PhEditor.crop.MLOnCropTaskCompleted;
import com.photo.editor.snapstudio.PhEditor.listener.LayoutItemListener;
import com.photo.editor.snapstudio.PhEditor.listener.MultiTouchListener;
import com.photo.editor.snapstudio.PhEditor.support.Constants;
import com.photo.editor.snapstudio.PhEditor.support.MyExceptionHandlerPix;
import com.photo.editor.snapstudio.PhEditor.utils.BitmapTransfer;
import com.photo.editor.snapstudio.PhEditor.utils.ImageUtils;

import java.util.ArrayList;

public class WingActivity extends BaseActivity implements LayoutItemListener {

    public static Bitmap resultBmp;
    private static Bitmap faceBitmap;
    private Bitmap selectedBitmap;
    private Bitmap cutBitmap;
    public int count = 0;
    private int wingsCount = 20;
    boolean isFirst = true;
    private Context context;
    private WingsAdapter wingsAdapter;
    private RecyclerView recyclerViewWings;
    private ArrayList<String> wingsList = new ArrayList<String>();
    private ImageView imageViewCover;
    private ImageView imageViewWings;
    private ImageView imageViewBackground;
    private RelativeLayout relativeLayoutRootView;
    ConstraintLayout mainLayout;
    ImageView close, save;
    TextView textView;
    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }
//    AdsPref adsPref;
//    AdsManager adsManager;
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
//        adsPref = new AdsPref(this);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_wing);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(WingActivity.this));
        context = this;
        selectedBitmap = faceBitmap;
    //        adsManager = new AdsManager(this);
    //        adsManager.initializeAd();
    //        adsManager.loadBannerAd(BANNER_HOME);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                imageViewBackground.post(new Runnable() {
                    public void run() {
                        if (isFirst && selectedBitmap != null) {
                            isFirst = false;
                            initBitmap();
                        }
                    }
                });
            }
        }, 1000);

        wingsList.add("none");
        for (int i = 1; i <= wingsCount; i++) {
            wingsList.add("wing_" + i);
        }
        initViews();
        changeTheme();

    }

    private void initBitmap() {
        if (faceBitmap != null) {
            selectedBitmap = ImageUtils.getBitmapResize(context, faceBitmap, imageViewBackground.getWidth(), imageViewBackground.getHeight());
            relativeLayoutRootView.setLayoutParams(new LinearLayout.LayoutParams(selectedBitmap.getWidth(), selectedBitmap.getHeight()));
            if (selectedBitmap != null && imageViewCover != null) {
                imageViewCover.setImageBitmap(selectedBitmap);
            }
            setStartWings();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (resultBmp != null) {
                cutBitmap = resultBmp;
                imageViewBackground.setImageBitmap(cutBitmap);
            }
        }
    }

    private void changeTheme() {
        boolean isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        //     AdUtils.showNativeAd(WingActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);
        if (isDarkTheme){
            mainLayout.setBackgroundResource(R.color.blacktwo);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            textView.setTextColor(getResources().getColor(R.color.white));

        }else {
            mainLayout.setBackgroundResource(R.color.white);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            textView.setTextColor(getResources().getColor(R.color.blacktwo));
        }
    }

    public void initViews() {
        mainLayout = findViewById(R.id.main_layout);
        save = findViewById(R.id.imageViewSaveWings);
        close = findViewById(R.id.imageViewCloseWings);
        textView = findViewById(R.id.textViewTitle);
        relativeLayoutRootView = findViewById(R.id.relativeLayoutRootView);
        imageViewWings = findViewById(R.id.imageViewWings);
        imageViewCover = findViewById(R.id.imageViewBackground);
        imageViewBackground = findViewById(R.id.imageViewCover);
        recyclerViewWings = (RecyclerView) findViewById(R.id.recyclerViewWings);
        recyclerViewWings.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        wingsAdapter = new WingsAdapter(context);
        wingsAdapter.setMenuItemClickLister(this);
        recyclerViewWings.setAdapter(wingsAdapter);
        wingsAdapter.addData(wingsList);

        findViewById(R.id.imageViewCloseWings).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageViewCover.setRotationY(0.0f);

        imageViewBackground.post(new Runnable() {
            public void run() {
                initBitmap();
            }
        });

        SeekBar sbFrameOpacity = findViewById(R.id.seekbarOpacity);
        sbFrameOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (imageViewWings != null) {
                    float f = ((float) i) * 0.01f;
                    imageViewWings.setAlpha(f);
                }
            }
        });

        findViewById(R.id.imageViewSaveWings).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                new saveFile().execute();
            }
        });

        findViewById(R.id.image_view_eraser).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EraseActivity.b = selectedBitmap;
                Intent intent = new Intent(WingActivity.this, EraseActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_WING);
                startActivityForResult(intent, 1024);
            }
        });
    }

    public void onLayoutListClick(View view, int i) {
            if (i != 0) {
                Bitmap bitmapWings = ImageUtils.getBitmapFromAsset(context, "wings/" + wingsAdapter.getItemList().get(i) + (wingsAdapter.getItemList().get(i).startsWith("b") ? "_back.webp" : ".webp"));
                imageViewWings.setImageBitmap(bitmapWings);
            } else {
                imageViewWings.setImageResource(0);
            }
            imageViewWings.setOnTouchListener(new MultiTouchListener(this, true));
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
                int[] iArr = {0, 0, selectedBitmap.getWidth(), selectedBitmap.getHeight()};
                int width = selectedBitmap.getWidth();
                int height = selectedBitmap.getHeight();
                int i = width * height;
                selectedBitmap.getPixels(new int[i], 0, width, 0, 0, width, height);
                int[] iArr2 = new int[i];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                createBitmap.setPixels(iArr2, 0, width, 0, 0, width, height);
                cutBitmap = ImageUtils.getMask(context, selectedBitmap, createBitmap, width, height);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap, cutBitmap.getWidth(), cutBitmap.getHeight(), false);
                cutBitmap = resizedBitmap;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Palette p = Palette.from(cutBitmap).generate();
                        if (p.getDominantSwatch() == null) {
                            Toast.makeText(WingActivity.this, getString(R.string.txt_not_detect_human), Toast.LENGTH_SHORT).show();
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
            relativeLayoutRootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = getBitmapFromView(relativeLayoutRootView);
            try {
                return bitmap;
            } catch (Exception e) {
                return null;
            } finally {
                relativeLayoutRootView.setDrawingCacheEnabled(false);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null){
                BitmapTransfer.bitmap = bitmap;
            }
            Intent intent = new Intent(WingActivity.this, EditorActivity.class);
            intent.putExtra("MESSAGE","done");
            setResult(RESULT_OK,intent);
            finish();
        }
    }

}
