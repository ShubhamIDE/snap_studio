package com.photo.editor.snapstudio.PhEditor.Activity;

//import static com.photo.editor.snapstudio.ads.Constant.REWARDED_SHOW;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.photo.editor.snapstudio.Activity.EditProfileActivity;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.BuildConfig;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.constants.Constants;
import com.photo.editor.snapstudio.PhEditor.draw.BlurBrushView;
import com.photo.editor.snapstudio.PhEditor.draw.BlurView;
import com.photo.editor.snapstudio.PhEditor.utils.BitmapTransfer;
import com.photo.editor.snapstudio.PhEditor.views.MyExceptionHandlerPix;
import com.photo.editor.snapstudio.PhEditor.views.SupportedClass;

import java.io.File;
import java.io.IOException;

public class BlurActivity extends BaseActivity implements OnSeekBarChangeListener {
    protected static final int REQUEST_CODE_CAMERA = 0x2;
    protected static final int REQUEST_CODE_GALLERY = 0x3;
    public static Bitmap bitmapBlur;
    public static Bitmap bitmapClear;
    public static SeekBar seekBarBlur;
    public static BlurBrushView brushView;
    private ImageView imageViewEraser;
    private ImageView imageViewBlur;
    private ImageView imageViewZoom;
    private TextView textViewSize;
    private TextView textViewBlur;
    static int displayHight;
    public static int displayWidth;
    public static SeekBar seekBarSize;
    static BlurView blurView;

    public String mSelectedImagePath;
    public String mSelectedOutputPath;
    public Uri mSelectedImageUri;
    RelativeLayout imageViewContainer;
    private boolean erase;
    private ProgressDialog progressBlurring;
    private int startBlurSeekbarPosition;
    ConstraintLayout mainLayout;
    ImageView close, save;
    TextView textView, txt_size, txt_blur;
//    AdsPref adsPref;
//    AdsManager adsManager;
    public static Bitmap blur(Context context, Bitmap bitmap, int i) {
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap createBitmap = Bitmap.createBitmap(copy);
        RenderScript create = RenderScript.create(context);
        ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        Allocation createFromBitmap = Allocation.createFromBitmap(create, copy);
        Allocation createFromBitmap2 = Allocation.createFromBitmap(create, createBitmap);
        create2.setRadius((float) i);
        create2.setInput(createFromBitmap);
        create2.forEach(createFromBitmap2);
        createFromBitmap2.copyTo(createBitmap);
        return createBitmap;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        adsPref = new AdsPref(this);
        requestWindowFeature(1);
        setContentView(R.layout.activity_blur);

        getWindow().setFlags(1024, 1024);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(BlurActivity.this));
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        displayWidth = point.x;
        displayHight = point.y;
//        adsManager = new AdsManager(this);
//        adsManager.initializeAd();
//        adsManager.loadInterstitialAd(REWARDED_SHOW, adsPref.getInterstitialAdInterval());
        imageViewContainer = findViewById(R.id.relativeLayoutContainer);
        blurView = findViewById(R.id.drawingImageView);
        if (BitmapTransfer.bitmap != null) {
            bitmapClear = BitmapTransfer.bitmap;
        } else { }
        bitmapBlur = blur(this, bitmapClear, blurView.opacity);
        imageViewEraser = findViewById(R.id.imageViewEraser);
        imageViewBlur = findViewById(R.id.imageViewBlur);
        imageViewZoom= findViewById(R.id.imageViewZoom);
        seekBarSize = findViewById(R.id.seekBarSize);
        seekBarBlur = findViewById(R.id.seekBarBlur);
        textViewSize = findViewById(R.id.textViewSizeValue);
        textViewBlur = findViewById(R.id.textViewBlurValue);
        brushView = findViewById(R.id.brushView);

        save = findViewById(R.id.imageViewSaveBlur);
        close = findViewById(R.id.imageViewCloseBlur);
        textView = findViewById(R.id.title);
        mainLayout = findViewById(R.id.mainLayOut);
        txt_blur = findViewById(R.id.blur_txt);
        txt_size = findViewById(R.id.size_txt);

        brushView.setShapeRadiusRatio(((float) seekBarSize.getProgress()) / ((float) seekBarSize.getMax()));
        seekBarSize.setProgress((int) blurView.radius);
        seekBarBlur.setProgress(blurView.opacity);
        Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);
        Paint paint = new Paint(1);
        paint.setColor(-16711936);
        seekBarSize.setOnSeekBarChangeListener(this);
        seekBarBlur.setOnSeekBarChangeListener(this);

        blurView.initDrawing();
        this.progressBlurring = new ProgressDialog(this);
        findViewById(R.id.imageViewSaveBlur).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (blurView.drawingBitmap != null){
                    BitmapTransfer.bitmap = blurView.drawingBitmap;
                }
                Intent intent = new Intent(BlurActivity.this, EditorActivity.class);
                intent.putExtra("MESSAGE","done");
                setResult(RESULT_OK,intent);
                finish();

//                adsManager.showInterstitialAd();
            }
        });

        findViewById(R.id.imageViewCloseBlur).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        findViewById(R.id.imageViewEraser).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewEraser.setBackgroundResource(R.drawable.background_selected_color);
                imageViewEraser.setColorFilter(getResources().getColor(R.color.pink));
                imageViewBlur.setColorFilter(getResources().getColor(R.color.white));
                imageViewZoom.setColorFilter(getResources().getColor(R.color.white));
                imageViewBlur.setBackgroundResource(R.drawable.background_unslelected);
                imageViewZoom.setBackgroundResource(R.drawable.background_unslelected);
                erase = true;
                blurView.mode = 0;
                BlurView touchImageView = blurView;
                touchImageView.splashBitmap = bitmapClear;
                touchImageView.updateRefMetrix();
                blurView.changeShaderBitmap();
                blurView.coloring = true;
            }
        });

        findViewById(R.id.imageViewBlur).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewBlur.setBackgroundResource(R.drawable.background_selected_color);
                imageViewBlur.setColorFilter(getResources().getColor(R.color.pink));
                imageViewEraser.setColorFilter(getResources().getColor(R.color.white));
                imageViewZoom.setColorFilter(getResources().getColor(R.color.white));
                imageViewEraser.setBackgroundResource(R.drawable.background_unslelected);
                imageViewZoom.setBackgroundResource(R.drawable.background_unslelected);
                erase = false;
                blurView.mode = 0;
                BlurView touchImageView3 = blurView;
                touchImageView3.splashBitmap = bitmapBlur;
                touchImageView3.updateRefMetrix();
                blurView.changeShaderBitmap();
                blurView.coloring = false;
            }
        });


        findViewById(R.id.imageViewReset).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                imageViewEraser.setBackgroundResource(R.drawable.background_selected_color);
                imageViewBlur.setBackgroundResource(R.drawable.background_unslelected);
                imageViewZoom.setBackgroundResource(R.drawable.background_unslelected);
                imageViewEraser.setColorFilter(getResources().getColor(R.color.pink));
                imageViewBlur.setColorFilter(getResources().getColor(R.color.white));
                imageViewZoom.setColorFilter(getResources().getColor(R.color.white));
                BlurActivity.blurView.initDrawing();
                BlurActivity.blurView.saveScale = 1.0f;
                BlurActivity.blurView.fitScreen();
                BlurActivity.blurView.updatePreviewPaint();
                BlurActivity.blurView.updatePaintBrush();
            }
        });

        findViewById(R.id.imageViewFit).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                BlurView touchImageView2 = blurView;
                touchImageView2.saveScale = 1.0f;
                touchImageView2.radius = ((float) (seekBarSize.getProgress() + 10)) / blurView.saveScale;
                brushView.setShapeRadiusRatio(((float) (seekBarSize.getProgress() + 10)) / blurView.saveScale);
                blurView.fitScreen();
                blurView.updatePreviewPaint();
            }
        });

        findViewById(R.id.imageViewZoom).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                blurView.mode = 1;
                imageViewZoom.setBackgroundResource(R.drawable.background_selected_color);
                imageViewZoom.setColorFilter(getResources().getColor(R.color.pink));
                imageViewEraser.setColorFilter(getResources().getColor(R.color.white));
                imageViewBlur.setColorFilter(getResources().getColor(R.color.white));
                imageViewEraser.setBackgroundResource(R.drawable.background_unslelected);
                imageViewBlur.setBackgroundResource(R.drawable.background_unslelected);
            }
        });
        changeTheme();
    }

    private void changeTheme() {
        boolean isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        //  AdUtils.showNativeAd(BlurActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);
        if (isDarkTheme){
            mainLayout.setBackgroundResource(R.color.blacktwo);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            textView.setTextColor(getResources().getColor(R.color.white));
            textViewBlur.setTextColor(getResources().getColor(R.color.white));
            textViewSize.setTextColor(getResources().getColor(R.color.white));
            txt_blur.setTextColor(getResources().getColor(R.color.white));
            txt_size.setTextColor(getResources().getColor(R.color.white));
        }else {
            mainLayout.setBackgroundResource(R.color.white);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            textView.setTextColor(getResources().getColor(R.color.blacktwo));
            textViewBlur.setTextColor(getResources().getColor(R.color.blacktwo));
            textViewSize.setTextColor(getResources().getColor(R.color.blacktwo));
            txt_blur.setTextColor(getResources().getColor(R.color.blacktwo));
            txt_size.setTextColor(getResources().getColor(R.color.blacktwo));
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        if (id == R.id.seekBarBlur) {
            BlurBrushView brushView2 = brushView;
            brushView2.isBrushSize = false;
            brushView2.setShapeRadiusRatio(blurView.radius);
            brushView.brushSize.setPaintOpacity(seekBarBlur.getProgress());
            brushView.invalidate();
            BlurView touchImageView = blurView;
            touchImageView.opacity = i + 1;
            touchImageView.updatePaintBrush();
            String value = String.valueOf(i);
            textViewBlur.setText(value);
        } else if (id == R.id.seekBarSize) {
            BlurBrushView brushView3 = brushView;
            brushView3.isBrushSize = true;
            brushView3.brushSize.setPaintOpacity(255);
            brushView.setShapeRadiusRatio(((float) (seekBarSize.getProgress() + 10)) / blurView.saveScale);
            brushView.invalidate();
            blurView.radius = ((float) (seekBarSize.getProgress() + 10)) / blurView.saveScale;
            blurView.updatePaintBrush();
            String value = String.valueOf(i);
            textViewSize.setText(value);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            mSelectedImagePath = mSelectedOutputPath;
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                File fileImageClick = new File(mSelectedImagePath);
                if (fileImageClick.exists()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        mSelectedImageUri = Uri.fromFile(fileImageClick);
                    } else {
                        mSelectedImageUri = FileProvider.getUriForFile(BlurActivity.this, BuildConfig.APPLICATION_ID + ".provider", fileImageClick);
                    }
                    onPhotoTakenApp();
                }
            }
        } else if (data != null && data.getData() != null) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                mSelectedImageUri = data.getData();
                if (mSelectedImageUri != null) {
                    mSelectedImagePath = Constants.convertMediaUriToPath(BlurActivity.this, mSelectedImageUri);
                } else {
                    Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                }
            } else {
                mSelectedImagePath = mSelectedOutputPath;
            }
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                onPhotoTakenApp();
            }

        } else {
            Log.e("TAG", "");
        }
    }

    public void onPhotoTakenApp() {
        imageViewContainer.post(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmapClear = Constants.getBitmapFromUri(BlurActivity.this, mSelectedImageUri, (float) imageViewContainer.getMeasuredWidth(), (float) imageViewContainer.getMeasuredHeight());
                    bitmapBlur = blur(getApplicationContext(), bitmapClear, blurView.opacity);
                    blurView.initDrawing();
                    blurView.saveScale = 1.0f;
                    blurView.fitScreen();
                    blurView.updatePreviewPaint();
                    blurView.updatePaintBrush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.seekBarBlur) {
            this.startBlurSeekbarPosition = seekBarBlur.getProgress();
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.seekBarBlur) {
            final Dialog dialogOnBackPressed = new Dialog(BlurActivity.this, R.style.UploadDialog);
            dialogOnBackPressed.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogOnBackPressed.setContentView(R.layout.dialog_warning);
            dialogOnBackPressed.setCancelable(true);
            dialogOnBackPressed.show();
            TextView textViewCancel, textViewContinue;
            textViewCancel = dialogOnBackPressed.findViewById(R.id.textViewCancel);
            textViewContinue = dialogOnBackPressed.findViewById(R.id.textViewContinue);
            textViewContinue.setOnClickListener(view -> {
                new BlurUpdater().execute();
                dialogOnBackPressed.dismiss();
            });
            textViewCancel.setOnClickListener(view -> {
                BlurActivity.seekBarBlur.setProgress(startBlurSeekbarPosition);
                dialogOnBackPressed.dismiss();
            });
        }
    }

    private class BlurUpdater extends AsyncTask<String, Integer, Bitmap> {

        public void onPreExecute() {
            super.onPreExecute();
            progressBlurring.setMessage("Blurring...");
            progressBlurring.setIndeterminate(true);
            progressBlurring.setCancelable(false);
            progressBlurring.show();
        }

        public Bitmap doInBackground(String... strArr) {
            BlurActivity.bitmapBlur = BlurActivity.blur(getApplicationContext(), BlurActivity.bitmapClear, BlurActivity.blurView.opacity);
            return BlurActivity.bitmapBlur;
        }

        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }

        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!erase) {
                BlurActivity.blurView.splashBitmap = BlurActivity.bitmapBlur;
                BlurActivity.blurView.updateRefMetrix();
                BlurActivity.blurView.changeShaderBitmap();
            }
            BlurActivity.blurView.initDrawing();
            BlurActivity.blurView.saveScale = 1.0f;
            BlurActivity.blurView.fitScreen();
            BlurActivity.blurView.updatePreviewPaint();
            BlurActivity.blurView.updatePaintBrush();
            if (progressBlurring.isShowing()) {
                progressBlurring.dismiss();
            }
        }
    }

}
