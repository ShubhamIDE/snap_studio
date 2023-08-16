package com.photo.editor.snapstudio.PhEditor.Activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.Activity.EditProfileActivity;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.PhEditor.Adapter.ItemBeautyAdapter;
import com.photo.editor.snapstudio.PhEditor.Editor.Landmark;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.constants.Constants;
import com.photo.editor.snapstudio.PhEditor.tools.ToolEditor;
import com.photo.editor.snapstudio.PhEditor.utils.BitmapTransfer;
import com.photo.editor.snapstudio.PhEditor.utils.ResizeImages;
import com.photo.editor.snapstudio.PhEditor.views.ScanFileClass;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;
import com.yalantis.ucrop.view.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BeautyActivity extends BaseActivity implements ItemBeautyAdapter.OnItemEyeSelected {
    private static final String TAG = "BeautyActivity";
    public static List<Landmark> faceLandmarks;
    public static int[] faceRects;
    public static Rect rect;
    public Context context;
    public int currentapiVersion;
    public String imagePath;
    public Uri imageuri = null;
    public static Bitmap originalBmp;
    int coutAdFlag = 0;
    String datFilePath;
    private int BLUR_RESULT_CODE = 400;
    private int EYE_RESULT_CODE = 100;
    private int LIPS_RESULT_CODE = 200;
    private int WHITTEN_RESULT_CODE = CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION;
    private ImageView editorImageview;
    private ImageView imageViewCloseFinal;
    private ImageView imageViewSaveFinal;
    private String savePath;
    RecyclerView recyclerViewTools;
    private final ItemBeautyAdapter mEditingToolsAdapter = new ItemBeautyAdapter(this,this);
    public ToolEditor currentMode = ToolEditor.NONE;
    RelativeLayout mainLayout;
    ImageView close, save;
    boolean isDarkThemes;
    // new code i
    public static void setFaceBitmap(Bitmap bitmap) {
        originalBmp = bitmap;
    }
    public void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        setContentView(R.layout.activity_beauty);
        context = this;
        faceLandmarks = new ArrayList();
        faceRects = new int[4];
        rect = new Rect();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        currentapiVersion = VERSION.SDK_INT;
        ResizeImages resizeImage = new ResizeImages();
        imagePath = getIntent().getStringExtra("imagePath");
        isDarkThemes = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);

        //  try {
        try {
            if(originalBmp != null) {
                imagePath = resizeImage.saveBitmap(context, Constants.TEMP_FOLDER_NAME, originalBmp);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(BeautyActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        editorImageview = (ImageView) findViewById(R.id.editor_image_view);
        imageViewCloseFinal = findViewById(R.id.imageViewCloseFinal);
        imageViewSaveFinal = findViewById(R.id.imageViewSaveFinal);
        editorImageview.setImageBitmap(originalBmp);
        mainLayout = findViewById(R.id.main_layout);
        close = findViewById(R.id.imageViewCloseFinal);
        save = findViewById(R.id.imageViewSaveFinal);

        new FaceDetectTask().execute(new Void[0]);
//        adRequest = new Builder().build();
        imageViewSaveFinal.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                showAd();
            }
        });

        imageViewCloseFinal.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerViewTools = findViewById(R.id.recycler_view_tools);
        this.recyclerViewTools.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerViewTools.setAdapter(this.mEditingToolsAdapter);
        this.recyclerViewTools.setHasFixedSize(true);
        changeTheme();
    }

    private void changeTheme() {
        boolean isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
       // AdUtils.showNativeAd(BeautyActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);
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

    public void showAd() {
        if (this.imagePath != null) {
            BitmapTransfer.bitmap = originalBmp;
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra("MESSAGE", "done");
            setResult(-1, intent);
            finish();
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == LIPS_RESULT_CODE || i2 == EYE_RESULT_CODE || i2 == WHITTEN_RESULT_CODE) {
            Bitmap bitmap = originalBmp;
            if (bitmap != null && !bitmap.isRecycled()) {
                originalBmp = null;
                System.gc();
            }
            Bitmap GetImageImageRemaker = GetImageImageRemaker(intent, i2, i);
            originalBmp = GetImageImageRemaker;
            editorImageview.setImageBitmap(GetImageImageRemaker);
        }
        if (i2 == 0) {
            editorImageview.setImageBitmap(originalBmp);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
       /* try {
            faceLandmarks.clear();
            if (originalBmp != null && !originalBmp.isRecycled()) {
//                originalBmp.recycle();
                originalBmp = null;
                System.gc();
            }
            File file = new File(Constant.TEMP_FOLDER_NAME);
            if (file.exists()) {
                DeleteRecursive(file);
            }
            startActivity(new Intent(this, PhotoEditorActivity.class));
        } catch (Exception unused) {
        }

        */
    }

    public void DeleteRecursive(File file) {
        currentapiVersion = VERSION.SDK_INT;
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                DeleteRecursive(file2);
                deleteFromGallery(file2.getPath());
            }
        }
        file.delete();
    }

    private void deleteFromGallery(String str) {
        String[] strArr = {str};
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        String str2 = "_id";
        ContentResolver contentResolver2 = contentResolver;
        Cursor query = contentResolver2.query(uri, new String[]{str2}, "_data = ?", strArr, null);
        if (query.moveToFirst()) {
            contentResolver.delete(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, query.getLong(query.getColumnIndexOrThrow(str2))), null, null);
        } else {
            new File(str).delete();
            new ScanFileClass().refreshGallery(this, str, currentapiVersion);
        }
        query.close();
    }

    private Bitmap GetImageImageRemaker(Intent intent, int i, int i2) {
        Bitmap bitmap;
        try {
            Uri data = intent.getData();
//            File file = new File(intent.getStringExtra("filePath"));
//            if (file.exists()){
//                imagePath = String.valueOf(file);
//                bitmap = BitmapFactory.decodeFile(imagePath);
//            }
            if (data != null) {
                bitmap = Media.getBitmap(getContentResolver(), data);
                imageuri = data;
                imagePath = data.getPath().toString();
            } else {
                bitmap = null;
            }
            return bitmap;
        } catch (Exception unused) {
            StringBuilder sb = new StringBuilder();
            sb.append(intent.getStringExtra("path"));
//            sb.append(intent.getStringExtra("filePath"));
//            sb.append("temp.jpg");
            String sb2 = sb.toString();
            imagePath = sb2;
            File file = new File(sb2);
            return BitmapFactory.decodeFile(file.getAbsolutePath(), new BitmapFactory.Options());
        }
    }

    public void onToolEyeSelected(ToolEditor toolType) {
        this.currentMode = toolType;
        String str = "isBlushChecked";
        String str2 = "imagePath";
        Intent intent;
        switch (toolType) {
            case EYE_BEAUTY:
                intent = new Intent(this, EyeEditorActivity.class);
                intent.putExtra(str2, imagePath);
                startActivityForResult(intent, EYE_RESULT_CODE);
                coutAdFlag++;
                break;
            case FACE_BEAUTY:
                intent = new Intent(this, LipEditorActivity.class);
                intent.putExtra(str2, imagePath);
                intent.putExtra(str, Options.FOUNDATION);
                intent.putExtra("isdark", isDarkThemes);
                startActivityForResult(intent, LIPS_RESULT_CODE);
                coutAdFlag++;
                break;
            case LIP_BEAUTY:
                intent = new Intent(this, LipEditorActivity.class);
                intent.putExtra(str2, imagePath);
                intent.putExtra(str, Options.LIPCOLOR);
                intent.putExtra("isdark", isDarkThemes);
                startActivityForResult(intent, LIPS_RESULT_CODE);
                coutAdFlag++;
                break;
            case CHEEK_BEAUTY:
                intent = new Intent(this, LipEditorActivity.class);
                intent.putExtra(str2, imagePath);
                intent.putExtra(str, Options.BLUSH);
                intent.putExtra("isdark", isDarkThemes);
                startActivityForResult(intent, LIPS_RESULT_CODE);
                coutAdFlag++;
                break;
            case WHITEN_BEAUTY:
                intent = new Intent(this, WhitenEditorActivity.class);
                intent.putExtra(str2, imagePath);
                startActivityForResult(intent, WHITTEN_RESULT_CODE);
                coutAdFlag++;
                break;

        }
    }

    public void copyDatToSdcardSIRA() {
        try {
            InputStream in = getResources().openRawResource(R.raw.shape_predictor_68_face_landmarks);
            FileOutputStream out = new FileOutputStream(datFilePath);

            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }

        } catch (Exception e) {
            Toast.makeText(context, "No Transfer", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public enum Options {
        LIPCOLOR,
        BLUSH,
        FOUNDATION
    }

    private class FaceDetectTask extends AsyncTask<Void, Void, Void> {
        public Dialog progressDialog;
        private FaceDetectTask() {
            progressDialog = new Dialog(BeautyActivity.this);
        }

        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.show();

        }

        public Void doInBackground(Void... voidArr) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(getFilesDir().getAbsolutePath());
                sb.append("/shape_detector.dat");
                datFilePath = new File(sb.toString()).toString();
                if (!new File(datFilePath).exists()) {
                    copyDatToSdcardSIRA();
                }
                for (VisionDetRet visionDetRet : new FaceDet(datFilePath).detect(originalBmp)) {
                    BeautyActivity.faceRects[0] = visionDetRet.getLeft();
                    BeautyActivity.faceRects[1] = visionDetRet.getTop();
                    BeautyActivity.faceRects[2] = visionDetRet.getRight();
                    BeautyActivity.faceRects[3] = visionDetRet.getBottom();
                    BeautyActivity.rect.left = BeautyActivity.faceRects[0];
                    BeautyActivity.rect.top = BeautyActivity.faceRects[1];
                    BeautyActivity.rect.right = BeautyActivity.faceRects[2];
                    BeautyActivity.rect.bottom = BeautyActivity.faceRects[3];

                    ArrayList faceLandmarks = visionDetRet.getFaceLandmarks();
                    for (int i = 0; i != faceLandmarks.size(); i++) {
                        PointF pointF = new PointF();
                        pointF.set((float) ((Point) faceLandmarks.get(i)).x, (float) ((Point) faceLandmarks.get(i)).y);
                        BeautyActivity.faceLandmarks.add(new Landmark(pointF, i));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Could not find face...", Toast.LENGTH_SHORT).show();
                        FaceDetectTask.this.progressDialog.dismiss();
                    }
                });
            } catch (Throwable th) {
                th.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            progressDialog.dismiss();
        }
    }
}


