package com.photo.editor.snapstudio.PhEditor.Activity;


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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.PhEditor.Adapter.ColorDripAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.DripAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.DripBackgroundAdapter;
import com.photo.editor.snapstudio.PhEditor.Editor.PolishDripView;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.assets.BrushColorAsset;
import com.photo.editor.snapstudio.PhEditor.crop.MLCropAsyncTask;
import com.photo.editor.snapstudio.PhEditor.crop.MLOnCropTaskCompleted;
import com.photo.editor.snapstudio.PhEditor.drip.imagescale.TouchListener;
import com.photo.editor.snapstudio.PhEditor.listener.BackgroundItemListener;
import com.photo.editor.snapstudio.PhEditor.listener.LayoutItemListener;
import com.photo.editor.snapstudio.PhEditor.listener.MultiTouchListener;
import com.photo.editor.snapstudio.PhEditor.support.Constants;
import com.photo.editor.snapstudio.PhEditor.utils.BitmapTransfer;
import com.photo.editor.snapstudio.PhEditor.utils.DripFrameLayout;
import com.photo.editor.snapstudio.PhEditor.utils.DripUtils;
import com.photo.editor.snapstudio.PhEditor.utils.ImageUtils;
import com.photo.editor.snapstudio.PhEditor.views.MyExceptionHandlerPix;

import java.util.ArrayList;
import java.util.List;

public class DripActivity extends BaseActivity implements LayoutItemListener, BackgroundItemListener,
        ColorDripAdapter.blacktwoListener{
    public static Bitmap resultBmp;
    private static Bitmap faceBitmap;
    private Bitmap selectedBitmap;
    private Bitmap cutBitmap;
    private Bitmap mainBitmap = null;
    private Bitmap OverLayBackground = null;
    private Bitmap bitmapColor = null;
    private boolean isFirst = true;
    private boolean isReady = false;
    public int count = 0;
    private PolishDripView dripViewStyle;
    private PolishDripView dripViewImage;
    private DripFrameLayout frameLayoutBackground;
    private PolishDripView dripViewBackground;
    private RecyclerView recyclerViewStyle;
    private RecyclerView recyclerViewColor;
    private RecyclerView recyclerViewBackground;
    private LinearLayout linearLayoutStyle;
    private DripAdapter dripItemAdapter;
    private ImageView imageViewStyle;
    private ImageView imageViewBg;
    private ImageView imageViewColor;

    ConstraintLayout mainLayout;
    ImageView close, save;
    TextView textView;
    private DripBackgroundAdapter dripBackgroundAdapter;
    private List<String> dripColorList = BrushColorAsset.listColorBrush();
    private ArrayList<String> dripEffectList = new ArrayList<String>();
    private ArrayList<String> dripBackgroundList = new ArrayList<String>();
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_drip);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(DripActivity.this));
        this.dripViewStyle = findViewById(R.id.dripViewStyle);
        this.dripViewImage = findViewById(R.id.dripViewImage);
        this.dripViewBackground = findViewById(R.id.dripViewBackground);
        this.frameLayoutBackground = findViewById(R.id.frameLayoutBackground);
        imageViewStyle = findViewById(R.id.imageViewStyle);

        imageViewBg = findViewById(R.id.imageViewBg);
        imageViewColor = findViewById(R.id.imageViewColor);
        this.linearLayoutStyle = findViewById(R.id.linearLayoutStyle);
        this.linearLayoutStyle.setVisibility(View.VISIBLE);
        this.dripViewStyle.setOnTouchListenerCustom(new TouchListener());
        this.dripViewImage.setOnTouchListenerCustom(new TouchListener());
        save = findViewById(R.id.imageViewSaveDrip);
        close = findViewById(R.id.imageViewCloseDrip);
        textView = findViewById(R.id.drip_txt);
        mainLayout = findViewById(R.id.main_layout);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dripViewImage.post(new Runnable() {
                    public void run() {
                        if (isFirst) {
                            isFirst = false;
                            initBitmap();
                        }
                    }
                });
            }
        },  1000);


        findViewById(R.id.imageViewCloseDrip).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.imageViewSaveDrip).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                new saveFile().execute();
            }
        });

        for (int i = 1; i <= 14; i++) {
            dripEffectList.add("drip_" + i);
        }

        for (int i = 1; i <= 30; i++) {
            dripBackgroundList.add("background_" + i);
        }

        this.recyclerViewColor = findViewById(R.id.recyclerViewColor);
        this.recyclerViewColor.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerViewColor.setAdapter(new ColorDripAdapter(this, this));

        recyclerViewStyle = (RecyclerView) findViewById(R.id.recyclerViewStyle);
        recyclerViewStyle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        recyclerViewBackground = (RecyclerView) findViewById(R.id.recyclerViewBackground);
        recyclerViewBackground.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        setDripList();
        setBgList();

        dripViewImage.post(new Runnable() {
            public void run() {
                initBitmap();
            }
        });

        this.findViewById(R.id.imageViewStyle).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewStyle.setBackgroundResource(R.drawable.background_selected_color);
                imageViewColor.setBackgroundResource(R.drawable.background_unslelected);
                imageViewBg.setBackgroundResource(R.drawable.background_unslelected);
                imageViewStyle.setColorFilter(getResources().getColor(R.color.pink));
                imageViewColor.setColorFilter(getResources().getColor(R.color.white));
                imageViewBg.setColorFilter(getResources().getColor(R.color.white));
                recyclerViewStyle.setVisibility(View.VISIBLE);
                recyclerViewBackground.setVisibility(View.GONE);
                recyclerViewColor.setVisibility(View.GONE);
            }
        });

        this.findViewById(R.id.imageViewColor).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewColor.setBackgroundResource(R.drawable.background_selected_color);
                imageViewStyle.setBackgroundResource(R.drawable.background_unslelected);
                imageViewBg.setBackgroundResource(R.drawable.background_unslelected);
                imageViewColor.setColorFilter(getResources().getColor(R.color.pink));
                imageViewStyle.setColorFilter(getResources().getColor(R.color.white));
                imageViewBg.setColorFilter(getResources().getColor(R.color.white));
                recyclerViewStyle.setVisibility(View.GONE);
                recyclerViewBackground.setVisibility(View.GONE);
                recyclerViewColor.setVisibility(View.VISIBLE);
            }
        });

        this.findViewById(R.id.imageViewEraser).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EraseActivity.b = selectedBitmap;
                Intent intent = new Intent(DripActivity.this, EraseActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_DRIP);
                startActivityForResult(intent, 1024);
            }
        });

        this.findViewById(R.id.imageViewBg).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewBg.setBackgroundResource(R.drawable.background_selected_color);
                imageViewColor.setBackgroundResource(R.drawable.background_unslelected);
                imageViewStyle.setBackgroundResource(R.drawable.background_unslelected);
                imageViewBg.setColorFilter(getResources().getColor(R.color.pink));
                imageViewColor.setColorFilter(getResources().getColor(R.color.white));
                imageViewStyle.setColorFilter(getResources().getColor(R.color.white));
                recyclerViewStyle.setVisibility(View.GONE);
                recyclerViewBackground.setVisibility(View.VISIBLE);
                recyclerViewColor.setVisibility(View.GONE);
            }
        });
        changeTheme();
    }

    private void changeTheme() {
        boolean isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        //   AdUtils.showNativeAd(DripActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);

        if (isDarkTheme){
            mainLayout.setBackgroundResource(R.color.blacktwo);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            textView.setTextColor(getResources().getColor(R.color.white));
            dripViewStyle.setColorFilter(getResources().getColor(R.color.blacktwo));

        }else {
            mainLayout.setBackgroundResource(R.color.white);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            textView.setTextColor(getResources().getColor(R.color.blacktwo));
            dripViewStyle.setColorFilter(getResources().getColor(R.color.white));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (resultBmp != null) {
                cutBitmap = resultBmp;
                dripViewImage.setImageBitmap(cutBitmap);
                isReady = true;
                Bitmap bitmapFromAsset = DripUtils.getBitmapFromAsset(DripActivity.this, "drip/style/" + dripItemAdapter.getItemList().get(dripItemAdapter.selectedPos) + ".webp");
                if (!"none".equals(dripItemAdapter.getItemList().get(0))) {
                    OverLayBackground = bitmapFromAsset;
                }
            }
        }
    }

    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    private void initBitmap() {
        if (faceBitmap != null) {
            selectedBitmap = ImageUtils.getBitmapResize(DripActivity.this, faceBitmap, 1024, 1024);
            mainBitmap = Bitmap.createScaledBitmap(DripUtils.getBitmapFromAsset(DripActivity.this, "drip/style/white.webp"), selectedBitmap.getWidth(), selectedBitmap.getHeight(), true);
            bitmapColor = mainBitmap;
            Glide.with(this).load(Integer.valueOf(R.drawable.drip_1)).into(dripViewStyle);
            setStartDrip();
        }
    }

    public void setStartDrip() {
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
                cutBitmap = ImageUtils.getMask(DripActivity.this, selectedBitmap, createBitmap, width, height);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap, cutBitmap.getWidth(), cutBitmap.getHeight(), false);
                cutBitmap = resizedBitmap;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Palette p = Palette.from(cutBitmap).generate();
                        if (p.getDominantSwatch() == null) {
                            Toast.makeText(DripActivity.this, getString(R.string.txt_not_detect_human), Toast.LENGTH_SHORT).show();
                        }
                        dripViewImage.setImageBitmap(cutBitmap);
                        isReady = true;
                        Bitmap bitmapFromAsset = DripUtils.getBitmapFromAsset(DripActivity.this, "drip/style/" + dripItemAdapter.getItemList().get(0) + ".webp");
                        if (!"none".equals(dripItemAdapter.getItemList().get(0))) {
                            OverLayBackground = bitmapFromAsset;
                        }

                    }
                });
            }
        }, this, progressBar).execute(new Void[0]);

    }

    public void onLayoutListClick(View view, int i) {
        Bitmap bitmapFromAsset = DripUtils.getBitmapFromAsset(this, "drip/style/" + this.dripItemAdapter.getItemList().get(i) + ".webp");
        if (!"none".equals(dripItemAdapter.getItemList().get(i))) {
            OverLayBackground = bitmapFromAsset;
            dripViewStyle.setImageBitmap(OverLayBackground);
            return;
        }
        this.OverLayBackground = null;

    }

    @Override
    public void onBackgroundListClick(View view, int i) {
        if (i != 0) {
            Bitmap assetBitmapBack = ImageUtils.getBitmapFromAsset(DripActivity.this, "drip/background/" + dripBackgroundAdapter.getItemList().get(i) + ".webp");
            dripViewBackground.setImageBitmap(assetBitmapBack);
        } else {
            dripViewBackground.setImageResource(0);
        }
        dripViewBackground.setOnTouchListener(new MultiTouchListener(this, true));
    }


    public void setDripList() {
        dripItemAdapter = new DripAdapter(DripActivity.this);
        dripItemAdapter.setClickListener(this);
        recyclerViewStyle.setAdapter(dripItemAdapter);
        dripItemAdapter.addData(dripEffectList);
    }

    public void setBgList() {
        dripBackgroundAdapter = new DripBackgroundAdapter(DripActivity.this);
        dripBackgroundAdapter.setClickListener(this);
        recyclerViewBackground.setAdapter(dripBackgroundAdapter);
        dripBackgroundAdapter.addData(dripBackgroundList);
    }


    @Override
    public void onblacktwoSelected(int i, ColorDripAdapter.SquareView squareView) {
        if (squareView.isColor) {
            frameLayoutBackground.setBackgroundColor(squareView.drawableId);
            dripViewStyle.setColorFilter(squareView.drawableId);
        }  else {
            frameLayoutBackground.setBackgroundColor(squareView.drawableId);
            dripViewStyle.setColorFilter(squareView.drawableId);
        }
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
            frameLayoutBackground.setDrawingCacheEnabled(true);
            try {
                return getBitmapFromView(frameLayoutBackground);
            } catch (Exception e) {
//            Exception e = new UnsupportedOperationException();
                return null;
            } finally {
                frameLayoutBackground.setDrawingCacheEnabled(false);
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null){
                BitmapTransfer.bitmap = bitmap;
            }
            Intent intent = new Intent(DripActivity.this, EditorActivity.class);
            intent.putExtra("MESSAGE","done");
            setResult(RESULT_OK,intent);
            finish();
        }
    }

}
