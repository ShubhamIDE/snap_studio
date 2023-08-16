//package com.photo.editor.snapstudio.PhEditor.Store;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.ContentValues;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//
//import com.photo.editor.snapstudio.Activity.SavedImageActivity;
//import com.photo.editor.snapstudio.PhEditor.Editor.PhotoEditorView;
//import com.photo.editor.snapstudio.R;
//import com.photo.editor.snapstudio.PhEditor.eraser.ImageUtils;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.MultiplePermissionsReport;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//import com.squareup.picasso.Picasso;
//import com.yalantis.ucrop.UCrop;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//public class SEditorActivity extends AppCompatActivity {
//
//    TextView tvAddImage;
//    private static final int SELECT_PICTURE_CAMERA = 805;
//    private static final int SELECT_PICTURE_GALLERY = 807;
//    Uri uri;
//    RelativeLayout rl;
//    String imageUrl, queryString;
//    ImageView ivFestImage, backbt, savebt, eraserbt;
//    public PhotoEditorView photo_editor_view;
//    Bitmap image;
//    boolean isImage = false;
//    CardView cvBase;
//    FrameLayout flSticker, flBackSticker, flForVideo;
//    float sWidth;
//    float sHeight;
//    private Bitmap cutBitmap , mainBitmap;
//    public static Bitmap eraserResultBitmap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_seditor);
//        tvAddImage = findViewById(R.id.tv_add_image);
//        backbt = findViewById(R.id.backbt);
//        savebt = findViewById(R.id.savebt);
////        eraserbt = findViewById(R.id.eraserbt);
//        flForVideo = findViewById(R.id.fl_for_video);
//
//        rl = findViewById(R.id.rl);
////        photo_editor_view = findViewById(R.id.image);
//        imageUrl = getIntent().getStringExtra("imgUrl");
//        queryString = getIntent().getStringExtra("queryString");
//
//        cvBase = findViewById(R.id.cv_base);
//        ivFestImage = findViewById(R.id.iv_fest_image);
//        flSticker = findViewById(R.id.fl_sticker);
//        flBackSticker = findViewById(R.id.fl_back_sticker);
//        Picasso.get().load(imageUrl).into(ivFestImage);
//
//        backbt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//
////        eraserbt.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                EraseActivity.b = mainBitmap;
////                Intent intent = new Intent(SEditorActivity.this, EraseActivity.class);
////                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_S);
////                startActivityForResult(intent, 1024);
////            }
////        });
//
//        savebt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Bitmap bitmap = getScreenShotFromView(cvBase);
////
////                if (bitmap != null) {
////                    try {
////                        saveMediaToStorage(bitmap);
////                    } catch (FileNotFoundException e) {
////                        e.printStackTrace();
////                    }
////                }
//                flForVideo.setDrawingCacheEnabled(true);
//                Bitmap bitmap = Bitmap.createScaledBitmap(viewToBitmap(flForVideo), 1080,
//                        1080, true);
//                String strPath = saveImage(bitmap);
//                Intent i = new Intent(getApplicationContext(), SavedImageActivity.class);
//                i.putExtra("image_uri",strPath);
//                startActivity(i);
//                finish();
//            }
//        });
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        this.sWidth = (float) displayMetrics.widthPixels;
//        this.sHeight = (float) (displayMetrics.heightPixels - ImageUtils.dpToPx(this, 104));
//
//
////        photo_editor_view.setImageSource(image);
//        tvAddImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                removeControl();
//                addImage();
//            }
//        });
//
//    }
//
//    private String saveImage(Bitmap paramBitmap) {
//
//        File directory = new File(Environment.getExternalStorageDirectory().toString()
//                + "/Download/" + getResources().getString(R.string.app_name) + "/");
//
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        File file = new File(directory, "Image-Bitmap" + System.currentTimeMillis() + ".png");
//        if (file.exists()) {
//            file.delete();
//        }
//        try {
//            OutputStream outputStream = new FileOutputStream(file);
//            paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            outputStream.close();
//
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
//            this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//            return file.getAbsolutePath();
//        } catch (Exception e) {
//            return "";
//        }
//    }
//    private Bitmap viewToBitmap(View view) {
//        Bitmap createBitmap = null;
//        try {
//            createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//            view.draw(new Canvas(createBitmap));
//            return createBitmap;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return createBitmap;
//        } finally {
//            view.destroyDrawingCache();
//        }
//    }
//
//    private void addImage() {
////        checkPer();
//        final Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.add_image_dialog);
//        dialog.setCancelable(false);
//        TextView textView = (TextView) dialog.findViewById(R.id.txtTitle);
//        TextView fitEditText = (TextView) dialog.findViewById(R.id.auto_fit_edit_text);
//        TextView btnGallery = (TextView) dialog.findViewById(R.id.btnGallery);
//        TextView btnCamera = (TextView) dialog.findViewById(R.id.btnCamera);
//        TextView btnCancel = (TextView) dialog.findViewById(R.id.btnCancel);
//
////        GlideBinding.setTextSize(textView, "font_title_size");
////        GlideBinding.setTextSize(fitEditText, "font_body_size");
//
//        btnCamera.setOnClickListener(v -> {
//            dialog.dismiss();
//            String[] arrPermissionsCamera = {"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
//            if (Build.VERSION.SDK_INT >= 29)arrPermissionsCamera=new String[]{"android.permission.CAMERA"};
//            Dexter.withContext(this).withPermissions(arrPermissionsCamera)
//                    .withListener(new MultiplePermissionsListener() {
//                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                                onCameraButtonClick();
//
//                            }
//                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                                showSettingsDialog();
//                            }
//                        }
//
//                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                            permissionToken.continuePermissionRequest();
//                        }
//                    }).withErrorListener(dexterError -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
//
//        });
//
//        btnGallery.setOnClickListener(v -> {
//            dialog.dismiss();
//            String[] arrPermissionsCamera = {"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
//            if (Build.VERSION.SDK_INT >= 29)arrPermissionsCamera=new String[]{"android.permission.CAMERA"};
//            Dexter.withContext(this).withPermissions(arrPermissionsCamera)
//                    .withListener(new MultiplePermissionsListener() {
//                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                                onGalleryButtonClick();
//                            }
//                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                                showSettingsDialog();
//                            }
//                        }
//
//                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                            permissionToken.continuePermissionRequest();
//                        }
//                    }).withErrorListener(dexterError -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
//        });
//
//        btnCancel.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        dialog.show();
//
//    }
//    private void onGalleryButtonClick() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction("android.intent.action.GET_CONTENT");
//        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image).toString()), SELECT_PICTURE_GALLERY);
//    }
//
//    private void onCameraButtonClick() {
////        uri = FileProvider.getUriForFile(
////                getApplicationContext(),
////                BuildConfig.APPLICATION_ID + "." + "provider",
////                createCameraFile()
////        );
////        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
////        intent.putExtra("output", uri);
////        startActivityForResult(intent, SELECT_PICTURE_CAMERA);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, SELECT_PICTURE_CAMERA);
//    }
//
//    private File createCameraFile() {
//        File image = null;
//        String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "IMG_" + dateTime + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        try {
//            image = File.createTempFile(imageFileName, ".jpg", storageDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return image;
//    }
//
//    private void showSettingsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Need Permissions");
//        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
//        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
//                intent.setData(Uri.fromParts("package", getPackageName(), (String) null));
//                startActivityForResult(intent, 101);
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//            }
//        });
//        builder.show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        int i3 = requestCode;
//        int i4 = resultCode;
//        if (i4 == -1) {
//            if (data != null || i3 == SELECT_PICTURE_CAMERA) {
//                if (i3 == SELECT_PICTURE_GALLERY) {
//                    try {
//                        Uri fromFile = Uri.fromFile(new File(getCacheDir(),
//                                "SampleCropImage" + System.currentTimeMillis() + ".png"));
//                        Uri path = data.getData();
//                        StringBuilder sb = new StringBuilder();
//                        sb.append("====");
//                        sb.append(data.getData().getPath());
//                        Log.e("downaload", sb.toString());
//                        Log.e("downaload", "====" + fromFile.getPath());
//                        onSelectFromGalleryResult(data);
////                        LoadLogo loadLogo = new LoadLogo(path.toString());
////                        loadLogo.execute();
//                        Log.e("path", fromFile.getPath());
//                        UCrop.Options options2 = new UCrop.Options();
//                        options2.setToolbarColor(getResources().getColor(R.color.white));
//                        options2.setFreeStyleCropEnabled(true);
//                        UCrop.of(data.getData(), fromFile).withOptions(options2).start(this);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (i3 == SELECT_PICTURE_CAMERA) {
//                    try {
//                        Uri fromFile2 = Uri.fromFile(new File(getCacheDir(),
//                                "SampleCropImage" + System.currentTimeMillis() + ".png"));
//                        StringBuilder sb2 = new StringBuilder();
//                        sb2.append("====");
//                        sb2.append(fromFile2.getPath());
//                        onCaptureImageResult(data);
////                        LoadLogo loadLogo = new LoadLogo(fromFile2.getPath());
////                        loadLogo.execute();
//                        Log.e("downaload", sb2.toString());
//                        UCrop.Options options3 = new UCrop.Options();
//                        options3.setToolbarColor(getResources().getColor(R.color.white));
//                        options3.setFreeStyleCropEnabled(true);
//                        UCrop.of(uri, fromFile2).withOptions(options3).start(this);
//                    } catch (Exception e2) {
//                        e2.printStackTrace();
//                    }
//                }
//                if (i4 == -1 && i3 == 69) {
//                    handleCropResult(data);
//                } else if (i4 == 96) {
//                    UCrop.getError(data);
//                }
////                if (resultCode == RESULT_OK && requestCode == 1024) {
////                    if (eraserResultBitmap != null) {
////                        cutBitmap = eraserResultBitmap;
////                        Bitmap bitmap;
////                        Drawable drawable;
////                        drawable = new BitmapDrawable(Resources.getSystem(), cutBitmap);
////                        bitmap = ((BitmapDrawable) drawable).getBitmap();
////                        addSticker("", "", bitmap, false);
////                    }
////                }
//            }
//        }
//    }
//
//    private void handleCropResult(Intent data) {
//        Bitmap bmp = BitmapUtil.decodeBitmapFromUri(this, UCrop.getOutput(data), 456,
//                456, BitmapUtil.getInSampleSizePower2());
//        addSticker("", "", bmp, isImage);
////        LoadLogo loadLogo = new LoadLogo(bmp);
////        loadLogo.execute();
//    }
//
//    private void addSticker(String str, String str2, Bitmap bitmap2, boolean isImage) {
//        ElementInfo elementInfo = new ElementInfo();
//        elementInfo.setPOS_X((float) ((cvBase.getWidth() / 2) - ImageUtils.dpToPx(getApplicationContext(), 70)));
//        elementInfo.setPOS_Y((float) ((cvBase.getHeight() / 2) - ImageUtils.dpToPx(getApplicationContext(), 70)));
//        elementInfo.setWIDTH(ImageUtils.dpToPx(getApplicationContext(), 140));
//        elementInfo.setHEIGHT(ImageUtils.dpToPx(getApplicationContext(), 140));
//        elementInfo.setROTATION(0.0f);
//        elementInfo.setRES_ID(str);
//        elementInfo.setBITMAP(bitmap2);
//        mainBitmap = bitmap2;
//        elementInfo.setCOLORTYPE("colored");
//        elementInfo.setTYPE("STICKER");
//        elementInfo.setSTC_OPACITY(255);
//        elementInfo.setSTC_COLOR(0);
//        elementInfo.setSTKR_PATH(str2);
//        elementInfo.setSTC_HUE(1);
//        elementInfo.setFIELD_TWO("0,0");
//        RelStickerView relStickerView = new RelStickerView(getApplicationContext(), isImage);
//        relStickerView.optimizeScreen(sWidth, sHeight);
//        relStickerView.setMainLayoutWH((float) cvBase.getWidth(), (float) cvBase.getHeight());
//        relStickerView.setComponentInfo(elementInfo);
//        relStickerView.setId(ViewIdGenerator.generateViewId());
//        flSticker.addView(relStickerView);
//        relStickerView.setOnTouchCallbackListener(rtouchlistener);
//        relStickerView.setBorderVisibility(true);
//    }
//    RelStickerView.TouchEventListener rtouchlistener = new RelStickerView.TouchEventListener() {
//        @Override
//        public void onDelete() {
//        }
//
//        @Override
//        public void onEdit(View view, Uri uri) {
//
//        }
//
//        @Override
//        public void onRotateDown(View view) {
//            touchDown(view, "viewboder");
//        }
//
//        @Override
//        public void onRotateMove(View view) {
//            touchMove(view);
//        }
//
//        @Override
//        public void onRotateUp(View view) {
//            touchUp(view);
//        }
//
//        @Override
//        public void onScaleDown(View view) {
//            touchDown(view, "viewboder");
//        }
//
//        @Override
//        public void onScaleMove(View view) {
//            touchMove(view);
//        }
//
//        @Override
//        public void onScaleUp(View view) {
//            touchUp(view);
//        }
//
//        @Override
//        public void onTouchDown(View view) {
//            touchDown(view, "viewboder");
//        }
//
//        @Override
//        public void onTouchMove(View view) {
//            touchMove(view);
//        }
//
//        @Override
//        public void onTouchUp(View view) {
//            touchUp(view);
//        }
//
//        @Override
//        public void onMainClick(View view) {
//            Log.e("TOUCH", "MAIN TOUCH");
//            setBackImage();
//        }
//    };
//
//    RelStickerView.TouchEventListener newtouchlistener = new RelStickerView.TouchEventListener() {
//        @Override
//        public void onDelete() {
//        }
//
//        @Override
//        public void onEdit(View view, Uri uri) {
//
//        }
//
//        @Override
//        public void onRotateDown(View view) {
//            touchDown(view, "viewboder");
//        }
//
//        @Override
//        public void onRotateMove(View view) {
//            touchMove(view);
//        }
//
//        @Override
//        public void onRotateUp(View view) {
//            touchUp(view);
//        }
//
//        @Override
//        public void onScaleDown(View view) {
//            touchDown(view, "viewboder");
//        }
//
//        @Override
//        public void onScaleMove(View view) {
//            touchMove(view);
//        }
//
//        @Override
//        public void onScaleUp(View view) {
//            touchUp(view);
//        }
//
//        @Override
//        public void onTouchDown(View view) {
//            touchDown(view, "viewboder");
//        }
//
//        @Override
//        public void onTouchMove(View view) {
//            touchMove(view);
//        }
//
//        @Override
//        public void onTouchUp(View view) {
//            touchUp(view);
//        }
//
//        @Override
//        public void onMainClick(View view) {
//            Log.e("TOUCH", "MAIN TOUCH");
//            setBackImage();
//        }
//    };
//
//    public void setBackImage() {
//        int childCount = flSticker.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View childAt = flSticker.getChildAt(i);
//            if (childAt instanceof RelStickerView) {
//                if (((RelStickerView) childAt).getBorderVisbilty() && ((RelStickerView) childAt).getIsImage()) {
//                    flSticker.removeView(childAt);
//                    flBackSticker.addView(childAt);
//                    ((RelStickerView) childAt).setOnTouchCallbackListener(newtouchlistener);
//                    ((RelStickerView) childAt).setBorderVisibility(false);
//                }
//            }
//        }
//    }
//
//    private void touchDown(View view, String str) {
//        if (view instanceof RelStickerView) {
//            RelStickerView relStickerView = (RelStickerView) view;
////            setBackImage();
//        }
//    }
//
//    private void touchMove(View view) {
//        boolean z = view instanceof RelStickerView;
//        if (z) {
//            RelStickerView relStickerView = (RelStickerView) view;
//        } else {
//
//        }
//    }
//
//    private void touchUp(final View view) {
//        if (view instanceof RelStickerView) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("");
//            RelStickerView relStickerView = (RelStickerView) view;
//            sb.append(relStickerView.getColorType());
//            relStickerView.setBorderVisibility(true);
//            if (isImage && view.getParent() == flBackSticker) {
//                flBackSticker.removeView(relStickerView);
//                flSticker.addView(relStickerView);
//                relStickerView.setOnTouchCallbackListener(rtouchlistener);
//            }
//        }
//    }
//
//    public void removeControl() {
//        int childCount = flSticker.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View childAt = flSticker.getChildAt(i);
//            if (childAt instanceof RelStickerView) {
//                ((RelStickerView) childAt).setBorderVisibility(false);
//            }
//        }
//    }
//
//    private void onCaptureImageResult(Intent data) {
//        Drawable drawable;
//
//        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//        drawable = new BitmapDrawable(Resources.getSystem(), thumbnail);
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//
//        File destination = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//
//        FileOutputStream fo;
//        try {
//            destination.createNewFile();
//            fo = new FileOutputStream(destination);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//        addSticker("", "", bitmap, false);
////        imageView.setImageBitmap(thumbnail);
//    }
//
//    private void onSelectFromGalleryResult(Intent data) {
//        Drawable drawable = null;
//
//        Bitmap bm = null;
//        if (data != null) {
//            try {
//                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
//            drawable = new BitmapDrawable(Resources.getSystem(), bm);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//            addSticker("", "", bitmap, false);
//        }
//    }
//
//}