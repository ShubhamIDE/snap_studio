package com.photo.editor.snapstudio.PhEditor.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.hold1.keyboardheightprovider.KeyboardHeightProvider;
import com.photo.editor.snapstudio.Activity.DashboardActivity;
import com.photo.editor.snapstudio.Activity.SavedImageActivity;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.BitmapFromUrlTask;
import com.photo.editor.snapstudio.PhEditor.Adapter.AdjustAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.ColorAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.FilterAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.OverlayAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.RecyclerTabLayout;
import com.photo.editor.snapstudio.PhEditor.Adapter.StickerAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.StickerTabAdapter;
import com.photo.editor.snapstudio.PhEditor.Adapter.ToolsAdapter;
//import com.photo.editor.snapstudio.PhEditor.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.PhEditor.Editor.OnPhotoEditorListener;
import com.photo.editor.snapstudio.PhEditor.Editor.PTextView;
import com.photo.editor.snapstudio.PhEditor.Editor.PhotoEditor;
import com.photo.editor.snapstudio.PhEditor.Editor.PhotoEditorView;
import com.photo.editor.snapstudio.PhEditor.Editor.Text;
import com.photo.editor.snapstudio.PhEditor.Editor.ViewType;
import com.photo.editor.snapstudio.PhEditor.Fragment.BeautyFragment;
import com.photo.editor.snapstudio.PhEditor.Fragment.CropperFragment;
import com.photo.editor.snapstudio.PhEditor.Fragment.HSlFragment;
import com.photo.editor.snapstudio.PhEditor.Fragment.MirrorFragment;
import com.photo.editor.snapstudio.PhEditor.Fragment.RatioFragment;
import com.photo.editor.snapstudio.PhEditor.Fragment.SquareFragment;
import com.photo.editor.snapstudio.PhEditor.Fragment.TextFragment;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.assets.FilterFileAsset;
import com.photo.editor.snapstudio.PhEditor.assets.OverlayFileAsset;
import com.photo.editor.snapstudio.PhEditor.assets.StickerFileAsset;
import com.photo.editor.snapstudio.PhEditor.constants.StoreManager;
import com.photo.editor.snapstudio.PhEditor.event.AlignHorizontallyEvent;
import com.photo.editor.snapstudio.PhEditor.event.DeleteIconEvent;
import com.photo.editor.snapstudio.PhEditor.event.EditTextIconEvent;
import com.photo.editor.snapstudio.PhEditor.event.FlipHorizontallyEvent;
import com.photo.editor.snapstudio.PhEditor.event.ZoomIconEvent;
import com.photo.editor.snapstudio.PhEditor.listener.AdjustListener;
import com.photo.editor.snapstudio.PhEditor.listener.BrushColorListener;
import com.photo.editor.snapstudio.PhEditor.listener.FilterListener;
import com.photo.editor.snapstudio.PhEditor.picker.PermissionsUtils;
import com.photo.editor.snapstudio.PhEditor.picker.PhotoPicker;
import com.photo.editor.snapstudio.PhEditor.sticker.BitmapStickerIcon;
import com.photo.editor.snapstudio.PhEditor.sticker.DrawableSticker;
import com.photo.editor.snapstudio.PhEditor.sticker.Sticker;
import com.photo.editor.snapstudio.PhEditor.sticker.StickerView;
import com.photo.editor.snapstudio.PhEditor.support.Constants;
import com.photo.editor.snapstudio.PhEditor.tools.ToolEditor;
import com.photo.editor.snapstudio.PhEditor.utils.BitmapTransfer;
import com.photo.editor.snapstudio.PhEditor.utils.PreferenceUtil;
import com.photo.editor.snapstudio.PhEditor.utils.SaveFileUtils;
import com.photo.editor.snapstudio.PhEditor.utils.SystemUtil;

import org.jetbrains.annotations.NotNull;
import org.wysaid.myUtils.MsgUtil;
import org.wysaid.nativePort.CGENativeLibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint("StaticFieldLeak")
public class EditorActivity extends BaseActivity implements OnPhotoEditorListener, View.OnClickListener, HSlFragment.OnFilterSavePhoto, StickerAdapter.OnClickStickerListener, CropperFragment.OnCropPhoto, BrushColorListener, RatioFragment.RatioSaveListener, SquareFragment.SplashDialogListener, BeautyFragment.OnBeautySave, ToolsAdapter.OnItemSelected, FilterListener, AdjustListener {

    private static final String TAG = "PhotoEditorActivity";

    public ImageView imageViewAddSticker;
    private TextView addNewText;
    private TextView textViewSizeValue;
    private TextView textViewEraerValue;
    private RelativeLayout relativeLayoutText;
    private RelativeLayout relativeLayoutSaveSticker;
    private RelativeLayout linearLayoutRecycler;
    private LinearLayout linearLayoutSize;
    private LinearLayout linearLayoutEraser;
    private ConstraintLayout constraint_layout_adjust;
    private SeekBar seekbar_adjust;
    private ConstraintLayout constraint_layout_brush;
    private SeekBar seekbar_brush_size;
    private ImageView image_view_compare_adjust;
    public ImageView image_view_compare_filter;
    public ImageView image_view_compare_overlay;
    public ToolEditor currentMode = ToolEditor.NONE;
    private ImageView imageViewEraser;
    private SeekBar seekbar_erase_size;
    public SeekBar seekbar_filter;
    public ConstraintLayout constraint_layout_filter;
    private KeyboardHeightProvider keyboardHeightProvider;
    public ArrayList lstBitmapWithFilter = new ArrayList<>();

    public List<Bitmap> lstBitmapWithOverlay = new ArrayList<>();
    public AdjustAdapter mAdjustAdapter;
    private RecyclerView recycler_view_brush_color;
    private final ToolsAdapter mEditingToolsAdapter = new ToolsAdapter(this, this);
    public CGENativeLibrary.LoadImageCallback mLoadImageCallback = new CGENativeLibrary.LoadImageCallback() {
        public Bitmap loadImage(String str, Object obj) {
            try {
                return BitmapFactory.decodeStream(EditorActivity.this.getAssets().open(str));
            } catch (IOException io) {
                return null;
            }
        }

        public void loadImageOK(Bitmap bitmap, Object obj) {
            bitmap.recycle();
        }
    };
    public PhotoEditor photoEditor;
    public PhotoEditorView photo_editor_view;
    private ConstraintLayout constraint_layout_root_view;
    private RecyclerView recycler_view_adjust;
    public RecyclerView recycler_view_filter;

    public RecyclerView recycler_view_overlay;

    public RecyclerView recycler_view_tools;
    ImageView imageViewPaint;
    ImageView imageViewNeon;
    LottieAnimationView lottie_animation_view;

    boolean isDarkTheme;

    RelativeLayout mainLayout;

    ImageView backbt, undo, redo, image_view_close_adjust, image_view_save_adjust, image_view_close_overlay, image_view_save_overlay, image_view_close_filter, image_view_save_filter, image_view_close_paint, image_view_save_paint, image_view_close_text, image_view_save_text, closeSticker, saveSticker;

    TextView adjust_txt, overlay_txt, filter_txt, paint_txt, size_txt;
    RecyclerTabLayout recycler_tab_layout;


    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener onCompareTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case 0:
                EditorActivity.this.photo_editor_view.getGLSurfaceView().setAlpha(0.0f);
                return true;
            case 1:
                EditorActivity.this.photo_editor_view.getGLSurfaceView().setAlpha(1.0f);
                return false;
            default:
                return true;
        }
    };

    public SeekBar seekbar_overlay;
    public ConstraintLayout constraint_layout_overlay;
    private ConstraintLayout constraint_save_control;
    public SeekBar seekbarStickerAlpha;
    private ConstraintLayout constraint_layout_sticker;
    public TextFragment.TextEditor textEditor;
    public TextFragment textEditorDialogFragment;
    private RelativeLayout relativeLayoutSaveText;
    public RelativeLayout relative_layout_wrapper_photo;
    public LinearLayout linear_layout_wrapper_sticker_list;
    private RelativeLayout relative_layout_loading;
    private Guideline guidelinePaint;
    private Guideline guideline;
    private Animation slideUpAnimation, slideDownAnimation;
    private String rv;
    String url;

    //    AdsPref adsPref;
//    AdsManager adsManager;
    public void onCreate(Bundle bundle) {
//        adsPref = new AdsPref(this);
        super.onCreate(bundle);
        makeFullScreen();
        setContentView(R.layout.activity_editor);
//        AdUtils.showNativeAd(this, com.photo.editor.snapstudio.AdsUtils.Utils.Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        lottie_animation_view = findViewById(R.id.lottie_animation_view);
        rv = getIntent().getStringExtra("rvValue");
        url = getIntent().getStringExtra("bgUrl");
//        if (bgurl.isEmpty()){
//            url = "";
//        }else{
//            url = bgurl;
//        }
//        adsManager = new AdsManager(this);
//        adsManager.initializeAd();
//        adsManager.loadBannerAd(BANNER_HOME);
//        adsManager.loadInterstitialAd(INTERSTITIAL_SHOW, adsPref.getInterstitialAdInterval());
//        adsManager.loadInterstitialAd(REWARDED_SHOW, adsPref.getInterstitialAdInterval());
        initViews();
        changeTheme();
        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        CGENativeLibrary.setLoadImageCallback(this.mLoadImageCallback, null);
        if (Build.VERSION.SDK_INT < 26) {
            getWindow().setSoftInputMode(48);
        }
        this.recycler_view_tools.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recycler_view_tools.setAdapter(this.mEditingToolsAdapter);
        this.recycler_view_tools.setHasFixedSize(true);
        this.recycler_view_filter.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recycler_view_filter.setHasFixedSize(true);
        this.recycler_view_overlay.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recycler_view_overlay.setHasFixedSize(true);
        new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        this.recycler_view_adjust.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recycler_view_adjust.setHasFixedSize(true);
        this.mAdjustAdapter = new AdjustAdapter(getApplicationContext(), this);
        this.recycler_view_adjust.setAdapter(this.mAdjustAdapter);
        this.recycler_view_brush_color.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recycler_view_brush_color.setHasFixedSize(true);
        this.recycler_view_brush_color.setAdapter(new ColorAdapter(getApplicationContext(), this));
        this.photoEditor = new PhotoEditor.Builder(this, this.photo_editor_view).setPinchTextScalable(true).build();
        this.photoEditor.setOnPhotoEditorListener(this);
        toogleDrawBottomToolbar(false);
        PreferenceUtil.setHeightOfKeyboard(getApplicationContext(), 0);
        this.keyboardHeightProvider = new KeyboardHeightProvider(this);
        this.keyboardHeightProvider.addKeyboardListener(i -> {
            if (i <= 0) {
                PreferenceUtil.setHeightOfNotch(getApplicationContext(), -i);
            } else if (textEditorDialogFragment != null) {
                textEditorDialogFragment.updateAddTextBottomToolbarHeight(PreferenceUtil.getHeightOfNotch(getApplicationContext()) + i);
                PreferenceUtil.setHeightOfKeyboard(getApplicationContext(), i + PreferenceUtil.getHeightOfNotch(getApplicationContext()));
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            new OnLoadBitmapFromUri().execute(extras.getString(PhotoPicker.KEY_SELECTED_PHOTOS));
        }

        assert extras != null;
        String imageUrl = extras.getString(PhotoPicker.KEY_SELECTED_PHOTO);
        BitmapFromUrlTask task = new BitmapFromUrlTask(new BitmapFromUrlTask.OnBitmapLoadedListener() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                // Do something with the loaded bitmap
                if (bitmap != null) {
                    // Process the bitmap
                    photo_editor_view.setImageSource(bitmap);
                    updateLayout();
                }
            }
        });
        task.execute(imageUrl);


    }

    private void toogleDrawBottomToolbar(boolean z) {
        int i = !z ? View.GONE : View.VISIBLE;
        this.imageViewEraser.setVisibility(i);
    }

    public void iEraserBrush() {
        this.linearLayoutRecycler.setVisibility(View.GONE);
        this.linearLayoutSize.setVisibility(View.GONE);
        this.linearLayoutEraser.setVisibility(View.VISIBLE);
        this.imageViewEraser.setColorFilter(ContextCompat.getColor(this, R.color.pink));
        this.imageViewEraser.setBackgroundResource(R.drawable.background_selected_color);
        this.imageViewPaint.setBackgroundResource(R.drawable.background_unslelected);
        this.imageViewNeon.setBackgroundResource(R.drawable.background_unslelected);
        this.imageViewPaint.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewNeon.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.photoEditor.brushEraser();
        this.seekbar_erase_size.setProgress(20);
    }

    public void iNeonBrush() {
        this.recycler_view_brush_color.setVisibility(View.VISIBLE);
        ColorAdapter colorAdapter = (ColorAdapter) this.recycler_view_brush_color.getAdapter();
        if (colorAdapter != null) {
            colorAdapter.setSelectedColorIndex(0);
        }
        this.recycler_view_brush_color.scrollToPosition(0);
        if (colorAdapter != null) {
            colorAdapter.notifyDataSetChanged();
        }
        this.linearLayoutRecycler.setVisibility(View.VISIBLE);
        this.linearLayoutSize.setVisibility(View.VISIBLE);
        this.linearLayoutEraser.setVisibility(View.GONE);
        this.imageViewEraser.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewEraser.setBackgroundResource(R.drawable.background_unslelected);
        this.imageViewPaint.setBackgroundResource(R.drawable.background_unslelected);
        this.imageViewNeon.setBackgroundResource(R.drawable.background_selected_color);
        this.imageViewPaint.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewNeon.setColorFilter(ContextCompat.getColor(this, R.color.pink));
        this.photoEditor.setBrushMode(2);
        this.photoEditor.setBrushDrawingMode(true);
        this.seekbar_brush_size.setProgress(20);
    }

    public void iColorBrush() {
        this.recycler_view_brush_color.setVisibility(View.VISIBLE);
        this.recycler_view_brush_color.scrollToPosition(0);
        ColorAdapter colorAdapter = (ColorAdapter) this.recycler_view_brush_color.getAdapter();
        if (colorAdapter != null) {
            colorAdapter.setSelectedColorIndex(0);
        }
        if (colorAdapter != null) {
            colorAdapter.notifyDataSetChanged();
        }
        this.linearLayoutRecycler.setVisibility(View.VISIBLE);
        this.linearLayoutSize.setVisibility(View.VISIBLE);
        this.linearLayoutEraser.setVisibility(View.GONE);
        this.imageViewEraser.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewEraser.setBackgroundResource(R.drawable.background_unslelected);
        this.imageViewNeon.setBackgroundResource(R.drawable.background_unslelected);
        this.imageViewPaint.setBackgroundResource(R.drawable.background_selected_color);
        this.imageViewNeon.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewPaint.setColorFilter(ContextCompat.getColor(this, R.color.pink));
        this.photoEditor.setBrushMode(1);
        this.photoEditor.setBrushDrawingMode(true);
        this.seekbar_brush_size.setProgress(20);
    }

    ActivityResultLauncher<Intent> paymentResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    /*if(recyclerViewFilter!=null)recyclerViewFilter.getAdapter().notifyDataSetChanged();
                    if(recycler_view_color!=null)recycler_view_color.getAdapter().notifyDataSetChanged();
                    if(recycler_view_collage!=null)recycler_view_collage.getAdapter().notifyDataSetChanged();*/
                }
            });

    private void changeTheme() {
        isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        // AdUtils.showNativeAd(EditorActivity.this, findViewById(R.id.native_ads), false, isDarkTheme);
        if (isDarkTheme) {
            mainLayout.setBackgroundResource(R.color.blacktwo);
            linear_layout_wrapper_sticker_list.setBackgroundResource(R.color.blacktwo);
            recycler_tab_layout.setBackgroundResource(R.color.blacktwo);
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            undo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            redo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_close_adjust.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_save_adjust.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            adjust_txt.setTextColor(getResources().getColor(R.color.white));
            image_view_close_overlay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_save_overlay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            overlay_txt.setTextColor(getResources().getColor(R.color.white));
            image_view_close_filter.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_save_filter.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            filter_txt.setTextColor(getResources().getColor(R.color.white));
            imageViewAddSticker.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_close_paint.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_save_paint.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            paint_txt.setTextColor(getResources().getColor(R.color.white));
            image_view_save_text.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_close_text.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            closeSticker.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            saveSticker.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            size_txt.setTextColor(getResources().getColor(R.color.white));
            textViewSizeValue.setTextColor(getResources().getColor(R.color.white));
        } else {
            mainLayout.setBackgroundResource(R.color.white);
            linear_layout_wrapper_sticker_list.setBackgroundResource(R.color.white);
            recycler_tab_layout.setBackgroundResource(R.color.white);
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            undo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            redo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_close_adjust.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_save_adjust.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            adjust_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            textViewSizeValue.setTextColor(getResources().getColor(R.color.blacktwo));
            size_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            image_view_close_overlay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_save_overlay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            overlay_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            image_view_close_filter.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_save_filter.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            filter_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            imageViewAddSticker.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_close_paint.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_save_paint.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_save_text.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_close_text.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            closeSticker.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            saveSticker.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            paint_txt.setTextColor(getResources().getColor(R.color.blacktwo));
        }
    }

    private void initViews() {
        mainLayout = findViewById(R.id.linear_layout_editor);
        backbt = findViewById(R.id.image_view_exit);
        undo = findViewById(R.id.imageViewUndo);
        redo = findViewById(R.id.imageViewRedo);
        size_txt = findViewById(R.id.size_txt);
        adjust_txt = findViewById(R.id.adjust_txt);
        image_view_close_adjust = findViewById(R.id.image_view_close_adjust);
        image_view_save_adjust = findViewById(R.id.image_view_save_adjust);
        overlay_txt = findViewById(R.id.overlay_txt);
        image_view_close_overlay = findViewById(R.id.image_view_close_overlay);
        image_view_save_overlay = findViewById(R.id.image_view_save_overlay);
        filter_txt = findViewById(R.id.filter_txt);
        image_view_close_filter = findViewById(R.id.image_view_close_filter);
        image_view_save_filter = findViewById(R.id.image_view_save_filter);
        paint_txt = findViewById(R.id.paint_txt);
        image_view_close_paint = findViewById(R.id.imageViewClosePaint);
        image_view_save_paint = findViewById(R.id.imageViewSavePaint);
        image_view_close_text = findViewById(R.id.image_view_close_text);
        image_view_save_text = findViewById(R.id.image_view_save_text);
        saveSticker = findViewById(R.id.saveSticker);
        closeSticker = findViewById(R.id.closeSticker);
        relative_layout_loading = (RelativeLayout) findViewById(R.id.relative_layout_loading);
        relative_layout_loading.setVisibility(View.VISIBLE);
        this.linear_layout_wrapper_sticker_list = findViewById(R.id.linear_layout_wrapper_sticker_list);
        this.photo_editor_view = findViewById(R.id.photo_editor_view);
        this.photo_editor_view.setVisibility(View.INVISIBLE);
        this.recycler_view_tools = findViewById(R.id.recycler_view_tools);
        this.linearLayoutEraser = findViewById(R.id.linearLayoutEraser);
        this.linearLayoutSize = findViewById(R.id.linearLayoutSize);
        this.guidelinePaint = findViewById(R.id.guidelinePaint);
        this.textViewSizeValue = findViewById(R.id.textViewSizeValue);
        this.textViewEraerValue = findViewById(R.id.textViewEraserValue);
        this.linearLayoutRecycler = findViewById(R.id.linearLayoutRecycler);
        this.guideline = findViewById(R.id.guideline);
        this.recycler_view_filter = findViewById(R.id.recycler_view_filter);
        this.recycler_view_overlay = findViewById(R.id.recycler_view_overlay);
        this.recycler_view_adjust = findViewById(R.id.recycler_view_adjust);
        this.constraint_layout_root_view = findViewById(R.id.constraint_layout_root_view);
        this.constraint_layout_filter = findViewById(R.id.constraint_layout_filter);
        this.constraint_layout_overlay = findViewById(R.id.constraint_layout_overlay);
        this.constraint_layout_adjust = findViewById(R.id.constraint_layout_adjust);
        this.constraint_layout_sticker = findViewById(R.id.constraint_layout_sticker);
        this.relativeLayoutSaveSticker = findViewById(R.id.relativeLayoutSaveSticker);
        this.relativeLayoutSaveText = findViewById(R.id.relativeLayoutSaveText);
        ViewPager sticker_viewpaper = findViewById(R.id.sticker_viewpaper);
        this.seekbar_filter = findViewById(R.id.seekbar_filter);
        this.seekbar_overlay = findViewById(R.id.seekbar_overlay);
        this.seekbarStickerAlpha = findViewById(R.id.seekbarStickerAlpha);
        this.seekbarStickerAlpha.setVisibility(View.GONE);
        this.constraint_layout_brush = findViewById(R.id.constraintLayoutPaint);
        this.recycler_view_brush_color = findViewById(R.id.recycler_view_brush_color);
        this.relative_layout_wrapper_photo = findViewById(R.id.relative_layout_wrapper_photo);
        this.imageViewPaint = findViewById(R.id.imageViewPaint);
        this.imageViewNeon = findViewById(R.id.imageViewNeon);

        this.imageViewEraser = findViewById(R.id.imageViewEraser);
        this.seekbar_brush_size = findViewById(R.id.seekbarBrushSize);
        this.seekbar_erase_size = findViewById(R.id.seekbarEraserSize);
        TextView text_view_save = findViewById(R.id.imageViewSaveFinal);
        this.constraint_save_control = findViewById(R.id.constraint_save_control);
        text_view_save.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(EditorActivity.this, new AppInterfaces.InterstitialADInterface() {
                @Override
                public void adLoadState(boolean isLoaded) {
                    if (PermissionsUtils.checkWriteStoragePermission(EditorActivity.this)) {
                        new SaveBitmap().execute();
                    }
                }
            });
        });
        this.image_view_compare_adjust = findViewById(R.id.image_view_compare_adjust);
        this.image_view_compare_adjust.setOnTouchListener(this.onCompareTouchListener);
        this.image_view_compare_adjust.setVisibility(View.GONE);

        this.image_view_compare_filter = findViewById(R.id.image_view_compare_filter);
        this.image_view_compare_filter.setOnTouchListener(this.onCompareTouchListener);
        this.image_view_compare_filter.setVisibility(View.GONE);

        this.image_view_compare_overlay = findViewById(R.id.image_view_compare_overlay);
        this.image_view_compare_overlay.setOnTouchListener(this.onCompareTouchListener);
        this.image_view_compare_overlay.setVisibility(View.GONE);
        findViewById(R.id.image_view_exit).setOnClickListener(view -> EditorActivity.this.onBackPressed());
        this.imageViewEraser.setOnClickListener(view -> EditorActivity.this.iEraserBrush());
        this.imageViewPaint.setOnClickListener(view -> EditorActivity.this.iColorBrush());
        this.imageViewNeon.setOnClickListener(view -> EditorActivity.this.iNeonBrush());
        this.seekbar_erase_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                EditorActivity.this.photoEditor.setBrushEraserSize((float) i);
                String value = String.valueOf(i);
                textViewEraerValue.setText(value);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                EditorActivity.this.photoEditor.brushEraser();
            }
        });
        this.seekbar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                EditorActivity.this.photoEditor.setBrushSize((float) (i + 10));
                String value = String.valueOf(i);
                textViewSizeValue.setText(value);
            }
        });
        this.seekbarStickerAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                Sticker currentSticker = EditorActivity.this.photo_editor_view.getCurrentSticker();
                if (currentSticker != null) {
                    currentSticker.setAlpha(i);
                }
            }
        });
        this.imageViewAddSticker = findViewById(R.id.imageViewAddSticker);
        this.imageViewAddSticker.setVisibility(View.GONE);
        this.imageViewAddSticker.setOnClickListener(view -> {
            EditorActivity.this.imageViewAddSticker.setVisibility(View.GONE);
            EditorActivity.this.slideUp(EditorActivity.this.linear_layout_wrapper_sticker_list);
        });
        this.addNewText = findViewById(R.id.addNewText);
        this.relativeLayoutText = findViewById(R.id.relativeLayoutText);
        this.addNewText.setOnClickListener(view -> {
            EditorActivity.this.photo_editor_view.setHandlingSticker(null);
            EditorActivity.this.openTextFragment();
        });
        this.seekbar_adjust = findViewById(R.id.seekbar_adjust);
        this.seekbar_adjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                EditorActivity.this.mAdjustAdapter.getCurrentAdjustModel().setSeekBarIntensity(EditorActivity.this.photoEditor, ((float) i) / ((float) seekBar.getMax()), true);
            }
        });
        BitmapStickerIcon bitmapStickerIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_close), 0, BitmapStickerIcon.REMOVE);
        bitmapStickerIcon.setIconEvent(new DeleteIconEvent());
        BitmapStickerIcon bitmapStickerIcon2 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_scale), 3, BitmapStickerIcon.ZOOM);
        bitmapStickerIcon2.setIconEvent(new ZoomIconEvent());
        BitmapStickerIcon bitmapStickerIcon3 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_flip), 1, BitmapStickerIcon.FLIP);
        bitmapStickerIcon3.setIconEvent(new FlipHorizontallyEvent());
        BitmapStickerIcon bitmapStickerIcon4 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_rotate), 3, BitmapStickerIcon.ROTATE);
        bitmapStickerIcon4.setIconEvent(new ZoomIconEvent());
        BitmapStickerIcon bitmapStickerIcon5 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_edit), 1, BitmapStickerIcon.EDIT);
        bitmapStickerIcon5.setIconEvent(new EditTextIconEvent());
        BitmapStickerIcon bitmapStickerIcon6 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_center), 2, BitmapStickerIcon.ALIGN_HORIZONTALLY);
        bitmapStickerIcon6.setIconEvent(new AlignHorizontallyEvent());
        this.photo_editor_view.setIcons(Arrays.asList(bitmapStickerIcon, bitmapStickerIcon2, bitmapStickerIcon3, bitmapStickerIcon5, bitmapStickerIcon4, bitmapStickerIcon6));
        this.photo_editor_view.setBackgroundColor(-16777216);
        this.photo_editor_view.setLocked(false);
        this.photo_editor_view.setConstrained(true);
        this.photo_editor_view.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            public void onStickerDragFinished(@NonNull Sticker sticker) {
            }

            public void onStickerFlipped(@NonNull Sticker sticker) {
            }

            public void onStickerTouchedDown(@NonNull Sticker sticker) {
            }

            public void onStickerZoomFinished(@NonNull Sticker sticker) {
            }

            public void onTouchDownForBeauty(float f, float f2) {
            }

            public void onTouchDragForBeauty(float f, float f2) {
            }

            public void onTouchUpForBeauty(float f, float f2) {
            }

            public void onStickerAdded(@NonNull Sticker sticker) {
                EditorActivity.this.seekbarStickerAlpha.setVisibility(View.VISIBLE);
                EditorActivity.this.seekbarStickerAlpha.setProgress(sticker.getAlpha());
            }

            public void onStickerClicked(@NonNull Sticker sticker) {
                if (sticker instanceof PTextView) {
                    ((PTextView) sticker).setTextColor(SupportMenu.CATEGORY_MASK);
                    EditorActivity.this.photo_editor_view.replace(sticker);
                    EditorActivity.this.photo_editor_view.invalidate();
                }
                EditorActivity.this.seekbarStickerAlpha.setVisibility(View.VISIBLE);
                EditorActivity.this.seekbarStickerAlpha.setProgress(sticker.getAlpha());
            }

            public void onStickerDeleted(@NonNull Sticker sticker) {
                EditorActivity.this.seekbarStickerAlpha.setVisibility(View.GONE);
            }

            public void onStickerTouchOutside() {
                EditorActivity.this.seekbarStickerAlpha.setVisibility(View.GONE);
            }

            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                if (sticker instanceof PTextView) {
                    sticker.setShow(false);
                    photo_editor_view.setHandlingSticker((Sticker) null);
                    textEditorDialogFragment = TextFragment.show(EditorActivity.this, ((PTextView) sticker).getPolishText());
                    textEditor = new TextFragment.TextEditor() {
                        public void onDone(Text polishText) {
                            photo_editor_view.getStickers().remove(photo_editor_view.getLastHandlingSticker());
                            photo_editor_view.addSticker(new PTextView(EditorActivity.this, polishText));
                        }

                        public void onBackButton() {
                            photo_editor_view.showLastHandlingSticker();
                        }
                    };
                    textEditorDialogFragment.setOnTextEditorListener(textEditor);
                }
            }
        });
        this.seekbar_filter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                EditorActivity.this.photo_editor_view.setFilterIntensity(((float) i) / 100.0f);
            }
        });
        this.seekbar_overlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                EditorActivity.this.photo_editor_view.setFilterIntensity(((float) i) / 100.0f);
            }
        });

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        sticker_viewpaper.setAdapter(new PagerAdapter() {
            public int getCount() {
                return 15;
            }

            public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
                return view.equals(obj);
            }


            @Override
            public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
                (container).removeView((View) object);
            }

            @NonNull
            public Object instantiateItem(@NonNull ViewGroup viewGroup, int i) {
                View inflate = LayoutInflater.from(EditorActivity.this.getBaseContext()).inflate(R.layout.sticker_list, null, false);
                RecyclerView recycler_view_sticker = inflate.findViewById(R.id.recycler_view_sticker);
                recycler_view_sticker.setHasFixedSize(true);
                recycler_view_sticker.setLayoutManager(new GridLayoutManager(EditorActivity.this.getApplicationContext(), 6));
                switch (i) {
                    case 0:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.amojiList(), i, EditorActivity.this));
                        break;
                    case 1:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.chickenList(), i, EditorActivity.this));
                        break;
                    case 2:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.childList(), i, EditorActivity.this));
                        break;
                    case 3:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.christmasList(), i, EditorActivity.this));
                        break;
                    case 4:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.cuteList(), i, EditorActivity.this));
                        break;
                    case 5:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.emojList(), i, EditorActivity.this));
                        break;
                    case 6:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.emojiList(), i, EditorActivity.this));
                        break;
                    case 7:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.fruitList(), i, EditorActivity.this));
                        break;
                    case 8:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.heartList(), i, EditorActivity.this));
                        break;
                    case 9:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.lovedayList(), i, EditorActivity.this));
                        break;
                    case 10:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.plantList(), i, EditorActivity.this));
                        break;
                    case 11:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.stickerList(), i, EditorActivity.this));
                        break;
                    case 12:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.sweetList(), i, EditorActivity.this));
                        break;
                    case 13:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.textcolorList(), i, EditorActivity.this));
                        break;
                    case 14:
                        recycler_view_sticker.setAdapter(new StickerAdapter(EditorActivity.this.getApplicationContext(), StickerFileAsset.textneonList(), i, EditorActivity.this));
                        break;
                }

                viewGroup.addView(inflate);
                return inflate;
            }
        });
        recycler_tab_layout = findViewById(R.id.recycler_tab_layout);
        recycler_tab_layout.setUpWithAdapter(new StickerTabAdapter(sticker_viewpaper, getApplicationContext()));
        recycler_tab_layout.setPositionThreshold(0.5f);
        recycler_tab_layout.setBackgroundColor(ContextCompat.getColor(this, R.color.blacktwo));
    }

    public void slideUp(final View showLayout) {
        showLayout.setVisibility(View.VISIBLE);
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        showLayout.startAnimation(slideUpAnimation);
        slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void slideDown(final View hideLayout) {
        hideLayout.setVisibility(View.GONE);
        hideLayout.startAnimation(slideDownAnimation);
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr,
                                           @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    public void onAddViewListener(ViewType viewType, int i) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + i + "]");
    }

    public void onRemoveViewListener(int i) {
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + i + "]");
    }

    public void onRemoveViewListener(ViewType viewType, int i) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + i + "]");
    }

    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_close_adjust:
            case R.id.imageViewClosePaint:
            case R.id.image_view_close_filter:
            case R.id.image_view_close_overlay:
            case R.id.closeSticker:
            case R.id.image_view_close_text:
                slideDownSaveView();
                onBackPressed();
                return;
            case R.id.image_view_save_adjust:
                new SaveFilterAsBitmap().execute();
                this.image_view_compare_adjust.setVisibility(View.GONE);
                slideDown(this.constraint_layout_adjust);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                setGuideLine();
                updateLayout();
                this.currentMode = ToolEditor.NONE;
                return;
            case R.id.imageViewSavePaint:
                mLoading(true);
                runOnUiThread(() -> {
                    photoEditor.setBrushDrawingMode(false);
                    imageViewEraser.setVisibility(View.GONE);
                    slideDown(constraint_layout_brush);
                    slideUp(recycler_view_tools);
                    setGuideLine();
                    photo_editor_view.setImageSource(photoEditor.getBrushDrawingView().getDrawBitmap(photo_editor_view.getCurrentBitmap()));
                    photoEditor.clearBrushAllViews();
                    mLoading(false);
                    updateLayout();
                });
                slideDownSaveView();
                this.currentMode = ToolEditor.NONE;
                return;
            case R.id.image_view_save_filter:
                new SaveFilterAsBitmap().execute();
                this.image_view_compare_filter.setVisibility(View.GONE);
                slideDown(this.constraint_layout_filter);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                setGuideLine();
                updateLayout();
                this.currentMode = ToolEditor.NONE;
//                adsManager.showInterstitialAd();
                return;
            case R.id.image_view_save_overlay:
                new SaveFilterAsBitmap().execute();
                slideDown(this.constraint_layout_overlay);
                slideUp(this.recycler_view_tools);
                this.image_view_compare_overlay.setVisibility(View.GONE);
                slideDownSaveView();
                setGuideLine();
                updateLayout();
                this.currentMode = ToolEditor.NONE;
                return;
            case R.id.saveSticker:
                this.photo_editor_view.setHandlingSticker(null);
                this.photo_editor_view.setLocked(true);
                this.relativeLayoutSaveSticker.setVisibility(View.GONE);
                this.imageViewAddSticker.setVisibility(View.GONE);
                if (!this.photo_editor_view.getStickers().isEmpty()) {
                    new SaveStickerAsBitmap().execute();
                }
                updateLayout();
                setGuideLine();
                slideDown(this.linear_layout_wrapper_sticker_list);
                slideDown(this.constraint_layout_sticker);
                slideDown(this.relativeLayoutSaveSticker);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                this.currentMode = ToolEditor.NONE;
                return;
            case R.id.image_view_save_text:
                this.photo_editor_view.setHandlingSticker(null);
                this.photo_editor_view.setLocked(true);
                this.relativeLayoutSaveText.setVisibility(View.GONE);
                if (!this.photo_editor_view.getStickers().isEmpty()) {
                    new SaveStickerAsBitmap().execute();
                }
                setGuideLine();
                slideDown(this.relativeLayoutText);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                updateLayout();
                this.currentMode = ToolEditor.NONE;
                return;
            case R.id.imageViewRedo:
                setRedo();
                return;
            case R.id.imageViewPaintRedo:
                this.photoEditor.redoBrush();
                return;
            case R.id.imageViewPaintUndo:
                this.photoEditor.undoBrush();
                return;
            case R.id.imageViewUndo:
                setUndo();
                return;
            default:
        }
    }

    private void setUndo() {
        photo_editor_view.undo();
    }

    private void setRedo() {
        photo_editor_view.redo();
    }

    public void onPause() {
        super.onPause();
        this.keyboardHeightProvider.onPause();
    }

    public void onResume() {
        super.onResume();
        this.keyboardHeightProvider.onResume();
    }

    public void openTextFragment() {
        this.textEditorDialogFragment = TextFragment.show(this);
        this.textEditor = new TextFragment.TextEditor() {
            public void onDone(Text polishText) {
                photo_editor_view.addSticker(new PTextView(getApplicationContext(), polishText));
            }

            public void onBackButton() {
                if (photo_editor_view.getStickers().isEmpty()) {
                    onBackPressed();
                }
            }
        };
        this.textEditorDialogFragment.setOnTextEditorListener(this.textEditor);
    }

    public void onToolSelected(ToolEditor toolType) {
        this.currentMode = toolType;
        switch (toolType) {
            case PAINT:
                iColorBrush();
                updateLayout();
                this.photoEditor.setBrushDrawingMode(true);
                slideDown(this.recycler_view_tools);
                slideUp(this.constraint_layout_brush);
                slideUpSaveControl();
                toogleDrawBottomToolbar(true);
                setGuideLinePaint();
                this.photoEditor.setBrushMode(1);
                break;
            case TEXT:
                slideUpSaveView();
                this.photo_editor_view.setLocked(false);
                openTextFragment();
                slideDown(this.recycler_view_tools);
                slideUp(this.relativeLayoutSaveText);
                this.relativeLayoutText.setVisibility(View.VISIBLE);
                setGuideLine();
                break;
            case ADJUST:
                slideUpSaveView();
                updateLayout();
                this.image_view_compare_adjust.setVisibility(View.VISIBLE);
                this.mAdjustAdapter = new AdjustAdapter(getApplicationContext(), this);
                this.recycler_view_adjust.setAdapter(this.mAdjustAdapter);
                this.mAdjustAdapter.setSelectedAdjust(0);
                this.photoEditor.setAdjustFilter(this.mAdjustAdapter.getFilterConfig());
                ConstraintSet constraintSetAdjust = new ConstraintSet();
                constraintSetAdjust.clone(this.constraint_layout_root_view);
                constraintSetAdjust.connect(this.relative_layout_wrapper_photo.getId(), 1, this.constraint_layout_root_view.getId(), 1, 0);
                constraintSetAdjust.connect(this.relative_layout_wrapper_photo.getId(), 4, this.constraint_layout_adjust.getId(), 3, 0);
                constraintSetAdjust.connect(this.relative_layout_wrapper_photo.getId(), 2, this.constraint_layout_root_view.getId(), 2, 0);
                constraintSetAdjust.applyTo(this.constraint_layout_root_view);
                slideUp(this.constraint_layout_adjust);
                slideDown(this.recycler_view_tools);
                break;
            case FILTER:
                slideUpSaveView();
                new LoadFilterBitmap().execute();
                break;
            case STICKER:
                this.constraint_layout_sticker.setVisibility(View.VISIBLE);
                this.linear_layout_wrapper_sticker_list.setVisibility(View.VISIBLE);
                updateLayout();
                slideUpSaveView();
                this.photo_editor_view.setLocked(false);
                slideDown(this.recycler_view_tools);
                slideUp(this.constraint_layout_sticker);
                slideUp(this.relativeLayoutSaveSticker);
                setGuideLine();
                break;
            case EFFECT:
                slideUpSaveView();
                new LoadOverlayBitmap().execute();
                break;
            case RATIO:
                new ShowRatioFragment().execute();
                break;
            case SQUARE:
                new ShowSplashFragment(true).execute();
                break;
            case CROP:
                CropperFragment.show(this, this, this.photo_editor_view.getCurrentBitmap());
                break;
            case BEAUTY:
                BeautyActivity.setFaceBitmap(EditorActivity.this.photo_editor_view.getCurrentBitmap());
                startActivityForResult(new Intent(this, BeautyActivity.class), 900);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case BODY:
                BeautyFragment.show(this, this.photo_editor_view.getCurrentBitmap(), this);
                break;
            case BLUR:
                BitmapTransfer.bitmap = this.photo_editor_view.getCurrentBitmap();
                Intent intent1 = new Intent(EditorActivity.this, BlurActivity.class);
                startActivityForResult(intent1, 900);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case SPLASH:
                BitmapTransfer.bitmap = this.photo_editor_view.getCurrentBitmap();
                Intent intent = new Intent(EditorActivity.this, SplashActivity.class);
                startActivityForResult(intent, 900);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case MIRROR:
                new openMirrorFragment().execute(new Void[0]);
                break;
            case HSL:
                HSlFragment.show(this, this, this.photo_editor_view.getCurrentBitmap());
                break;
            case NEON:
                new neonEffect().execute();
                break;
            case PROFILE:
                new profileEffect().execute();
                break;
            case WINGS:
                new wingEffect().execute();
                break;
            case DRIP:
                new dripEffect().execute();
                break;
            case BG:
                new BgEffect().execute();
                break;

        }
        this.photo_editor_view.setHandlingSticker(null);
    }

    public void setGuideLine() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraint_layout_root_view);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 1, this.constraint_layout_root_view.getId(), 1, 0);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 4, this.guideline.getId(), 3, 0);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 2, this.constraint_layout_root_view.getId(), 2, 0);
        constraintSet.applyTo(this.constraint_layout_root_view);
    }

    public void setGuideLinePaint() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraint_layout_root_view);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 1, this.constraint_layout_root_view.getId(), 1, 0);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 4, this.guidelinePaint.getId(), 3, 0);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 2, this.constraint_layout_root_view.getId(), 2, 0);
        constraintSet.applyTo(this.constraint_layout_root_view);
    }

    public void slideUpSaveView() {
        this.constraint_save_control.setVisibility(View.GONE);
    }

    public void slideUpSaveControl() {
        this.constraint_save_control.setVisibility(View.GONE);
    }

    public void slideDownSaveControl() {
        this.constraint_save_control.setVisibility(View.VISIBLE);
    }

    public void slideDownSaveView() {
        this.constraint_save_control.setVisibility(View.VISIBLE);
    }

    public void onBackPressed() {

        if (this.currentMode != null) {
            try {
                switch (this.currentMode) {
                    case PAINT:
                        slideDown(this.constraint_layout_brush);
                        slideUp(this.recycler_view_tools);
                        slideDownSaveControl();
                        this.imageViewEraser.setVisibility(View.GONE);
                        this.photoEditor.setBrushDrawingMode(false);
                        setGuideLine();
                        this.photoEditor.clearBrushAllViews();
                        slideDownSaveView();
                        this.currentMode = ToolEditor.NONE;
                        updateLayout();
                        return;
                    case TEXT:
                        if (!this.photo_editor_view.getStickers().isEmpty()) {
                            this.photo_editor_view.getStickers().clear();
                            this.photo_editor_view.setHandlingSticker(null);
                        }
                        slideDown(this.relativeLayoutSaveText);
                        this.relativeLayoutText.setVisibility(View.GONE);
                        this.photo_editor_view.setHandlingSticker(null);
                        slideUp(this.recycler_view_tools);
                        this.photo_editor_view.setLocked(true);
                        slideDownSaveView();
                        setGuideLine();
                        this.currentMode = ToolEditor.NONE;
                        updateLayout();
                        return;
                    case ADJUST:
                        this.photoEditor.setFilterEffect("");
                        this.image_view_compare_adjust.setVisibility(View.GONE);
                        slideDown(this.constraint_layout_adjust);
                        slideUp(this.recycler_view_tools);
                        slideDownSaveView();
                        setGuideLine();
                        this.currentMode = ToolEditor.NONE;
                        updateLayout();
                        return;
                    case FILTER:
                        slideDown(this.constraint_layout_filter);
                        slideUp(this.recycler_view_tools);
                        slideDownSaveView();
                        this.photoEditor.setFilterEffect("");
                        this.image_view_compare_filter.setVisibility(View.GONE);
                        this.lstBitmapWithFilter.clear();
                        if (this.recycler_view_filter.getAdapter() != null) {
                            this.recycler_view_filter.getAdapter().notifyDataSetChanged();
                        }
                        setGuideLine();
                        this.currentMode = ToolEditor.NONE;
                        updateLayout();
                        return;
                    case STICKER:
                        if (this.photo_editor_view.getStickers().size() <= 0) {
                            this.linear_layout_wrapper_sticker_list.setVisibility(View.VISIBLE);
                            slideUp(recycler_view_tools);
                            slideDown(constraint_layout_sticker);
                            this.imageViewAddSticker.setVisibility(View.GONE);
                            this.photo_editor_view.setHandlingSticker(null);
                            this.photo_editor_view.setLocked(true);
                            this.currentMode = ToolEditor.NONE;
                        } else if (this.imageViewAddSticker.getVisibility() == View.VISIBLE) {
                            this.photo_editor_view.getStickers().clear();
                            this.imageViewAddSticker.setVisibility(View.GONE);
                            slideUp(recycler_view_tools);
                            slideDown(constraint_layout_sticker);
                            this.photo_editor_view.setHandlingSticker(null);
                            this.linear_layout_wrapper_sticker_list.setVisibility(View.VISIBLE);
                            this.currentMode = ToolEditor.NONE;
                        } else {
                            this.linear_layout_wrapper_sticker_list.setVisibility(View.GONE);
                            this.imageViewAddSticker.setVisibility(View.VISIBLE);
                        }
                        this.linear_layout_wrapper_sticker_list.setVisibility(View.VISIBLE);
                        this.currentMode = ToolEditor.NONE;
                        slideDownSaveView();
                        this.relativeLayoutSaveSticker.setVisibility(View.GONE);
                        setGuideLine();
                        updateLayout();
                        return;
                    case EFFECT:
                        this.photoEditor.setFilterEffect("");
                        this.image_view_compare_overlay.setVisibility(View.GONE);
                        this.lstBitmapWithOverlay.clear();
                        slideUp(this.recycler_view_tools);
                        slideDown(this.constraint_layout_overlay);
                        slideDownSaveView();
                        setGuideLine();
                        this.recycler_view_overlay.getAdapter().notifyDataSetChanged();
                        this.currentMode = ToolEditor.NONE;
                        updateLayout();
                        return;
                    case SQUARE:
                    case BLUR:
                    case DRIP:
                    case BG:
                    case NEON:
                    case WINGS:
                    case CROP:
                    case BEAUTY:
                    case SPLASH:
                    case HSL:
                    case MIRROR:
                    case PROFILE:
                    case BODY:
                    case NONE:
                        setOnBackPressDialog();
                        return;
                    default:

                        super.onBackPressed();
//                        startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setOnBackPressDialog() {
        Dialog dialogOnBackPressed = new Dialog(this, R.style.Theme_Dialog);
        dialogOnBackPressed.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOnBackPressed.setCancelable(false);
        dialogOnBackPressed.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogOnBackPressed.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialogOnBackPressed.setContentView(R.layout.dialog_exit);
        TextView textViewCancel, textViewDiscard;
        textViewCancel = dialogOnBackPressed.findViewById(R.id.textViewCancel);
        textViewDiscard = dialogOnBackPressed.findViewById(R.id.textViewDiscard);


        textViewCancel.setOnClickListener(view -> {
            dialogOnBackPressed.dismiss();
        });

        textViewDiscard.setOnClickListener(view -> {
            dialogOnBackPressed.dismiss();
            EditorActivity.this.currentMode = null;
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            EditorActivity.this.finish();
            finish();
        });

        dialogOnBackPressed.show();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onAdjustSelected(AdjustAdapter.AdjustModel adjustModel) {
        Log.d("XXXXXXXX", "onAdjustSelected " + adjustModel.seekbarIntensity + " " + this.seekbar_adjust.getMax());
        this.seekbar_adjust.setProgress((int) (adjustModel.seekbarIntensity * ((float) this.seekbar_adjust.getMax())));
    }

    public void addSticker(int item, Bitmap bitmap) {
        this.photo_editor_view.addSticker(new DrawableSticker(new BitmapDrawable(getResources(), bitmap)));
        slideDown(this.linear_layout_wrapper_sticker_list);
        this.imageViewAddSticker.setVisibility(View.VISIBLE);
    }

    public void finishCrop(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
        updateLayout();
    }

    public void onColorChanged(int item, String str) {
        this.photoEditor.setBrushColor(Color.parseColor(str));

    }

    public void ratioSavedBitmap(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
        updateLayout();
    }


    public void onBeautySave(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
    }

    @Override
    public void onSaveBlurBackground(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
    }

    @Override
    public void onSaveFilter(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
    }

    public void onFilterSelected(int itemCurrent, String string) {
        this.photoEditor.setFilterEffect(string);
        this.seekbar_filter.setProgress(50);
        this.seekbar_overlay.setProgress(70);
        if (this.currentMode == ToolEditor.EFFECT) {
            this.photo_editor_view.getGLSurfaceView().setFilterIntensity(0.7f);
        } else if (this.currentMode == ToolEditor.FILTER) {
            this.photo_editor_view.getGLSurfaceView().setFilterIntensity(0.5f);
        }
    }


    class LoadFilterBitmap extends AsyncTask<Void, Void, Void> {
        LoadFilterBitmap() {
        }

        public void onPreExecute() {
            EditorActivity.this.mLoading(true);
        }

        public Void doInBackground(Void... voidArr) {
            EditorActivity.this.lstBitmapWithFilter.clear();
            EditorActivity.this.lstBitmapWithFilter.addAll(FilterFileAsset.getListBitmapFilter(ThumbnailUtils.extractThumbnail(EditorActivity.this.photo_editor_view.getCurrentBitmap(), 100, 100)));
            Log.d("XXXXXXXX", "LoadFilterBitmap " + EditorActivity.this.lstBitmapWithFilter.size());
            return null;
        }

        public void onPostExecute(Void voidR) {
            EditorActivity.this.recycler_view_filter.setAdapter(new FilterAdapter(EditorActivity.this.lstBitmapWithFilter, EditorActivity.this, EditorActivity.this.getApplicationContext(), Arrays.asList(FilterFileAsset.FILTERS)));
            EditorActivity.this.slideDown(EditorActivity.this.recycler_view_tools);
            EditorActivity.this.slideUp(EditorActivity.this.constraint_layout_filter);
            EditorActivity.this.image_view_compare_filter.setVisibility(View.VISIBLE);
            EditorActivity.this.seekbar_filter.setProgress(100);
            ConstraintSet constraintSetAdjust = new ConstraintSet();
            constraintSetAdjust.clone(constraint_layout_root_view);
            constraintSetAdjust.connect(relative_layout_wrapper_photo.getId(), 1, constraint_layout_root_view.getId(), 1, 0);
            constraintSetAdjust.connect(relative_layout_wrapper_photo.getId(), 4, constraint_layout_filter.getId(), 3, 0);
            constraintSetAdjust.connect(relative_layout_wrapper_photo.getId(), 2, constraint_layout_root_view.getId(), 2, 0);
            constraintSetAdjust.applyTo(constraint_layout_root_view);
            EditorActivity.this.mLoading(false);
            updateLayout();
        }
    }

    class ShowRatioFragment extends AsyncTask<Void, Bitmap, Bitmap> {
        ShowRatioFragment() {
        }

        public void onPreExecute() {
            EditorActivity.this.mLoading(true);
        }

        public Bitmap doInBackground(Void... voidArr) {
            return FilterFileAsset.getBlurImageFromBitmap(EditorActivity.this.photo_editor_view.getCurrentBitmap(), 5.0f);
        }

        public void onPostExecute(Bitmap bitmap) {
            EditorActivity.this.mLoading(false);
            RatioFragment.show(EditorActivity.this, EditorActivity.this, EditorActivity.this.photo_editor_view.getCurrentBitmap(), bitmap);
        }
    }

    class LoadOverlayBitmap extends AsyncTask<Void, Void, Void> {
        LoadOverlayBitmap() {
        }

        public void onPreExecute() {
            EditorActivity.this.mLoading(true);
        }

        public Void doInBackground(Void... voidArr) {
            EditorActivity.this.lstBitmapWithOverlay.clear();
            EditorActivity.this.lstBitmapWithOverlay.addAll(OverlayFileAsset.getListBitmapOverlayEffect(ThumbnailUtils.extractThumbnail(EditorActivity.this.photo_editor_view.getCurrentBitmap(), 100, 100)));
            return null;
        }

        public void onPostExecute(Void voidR) {
            EditorActivity.this.recycler_view_overlay.setAdapter(new OverlayAdapter(EditorActivity.this.lstBitmapWithOverlay, EditorActivity.this, EditorActivity.this.getApplicationContext(), Arrays.asList(OverlayFileAsset.OVERLAY_EFFECTS)));
            EditorActivity.this.slideDown(EditorActivity.this.recycler_view_tools);
            EditorActivity.this.slideUp(EditorActivity.this.constraint_layout_overlay);
            EditorActivity.this.image_view_compare_overlay.setVisibility(View.VISIBLE);
            EditorActivity.this.seekbar_overlay.setProgress(100);
            EditorActivity.this.mLoading(false);
            ConstraintSet constraintSetAdjust = new ConstraintSet();
            constraintSetAdjust.clone(constraint_layout_root_view);
            constraintSetAdjust.connect(relative_layout_wrapper_photo.getId(), 1, constraint_layout_root_view.getId(), 1, 0);
            constraintSetAdjust.connect(relative_layout_wrapper_photo.getId(), 4, constraint_layout_overlay.getId(), 3, 0);
            constraintSetAdjust.connect(relative_layout_wrapper_photo.getId(), 2, constraint_layout_root_view.getId(), 2, 0);
            constraintSetAdjust.applyTo(constraint_layout_root_view);
            updateLayout();
        }
    }

    class openMirrorFragment extends AsyncTask<Void, Bitmap, Bitmap> {
        openMirrorFragment() {
        }

        public void onPreExecute() {
            mLoading(true);
        }

        public Bitmap doInBackground(Void... voids) {
            return FilterFileAsset.getBlurImageFromBitmap(photo_editor_view.getCurrentBitmap(), 5.0f);
        }

        public void onPostExecute(Bitmap bitmap) {
            mLoading(false);
            EditorActivity queShotEditorActivity = EditorActivity.this;
            MirrorFragment.show(queShotEditorActivity, queShotEditorActivity, photo_editor_view.getCurrentBitmap(), bitmap);
        }
    }

    class dripEffect extends AsyncTask<Void, Void, Void> {
        dripEffect() {
        }

        public void onPreExecute() {
            mLoading(true);
        }

        public Void doInBackground(Void... voids) {
            return null;
        }

        public void onPostExecute(Void voids) {
            StoreManager.setCurrentCropedBitmap(EditorActivity.this, (Bitmap) null);
            StoreManager.setCurrentCroppedMaskBitmap(EditorActivity.this, (Bitmap) null);
            DripActivity.setFaceBitmap(photo_editor_view.getCurrentBitmap());
            StoreManager.setCurrentOriginalBitmap(EditorActivity.this, photo_editor_view.getCurrentBitmap());
            Intent dripIntent = new Intent(EditorActivity.this, DripActivity.class);
            startActivityForResult(dripIntent, 900);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            mLoading(false);
        }
    }

    class BgEffect extends AsyncTask<Void, Void, Void> {
        BgEffect() {
        }

        public void onPreExecute() {
            mLoading(true);
        }

        public Void doInBackground(Void... voids) {
            return null;
        }

        public void onPostExecute(Void voids) {
            StoreManager.setCurrentCropedBitmap(EditorActivity.this, (Bitmap) null);
            StoreManager.setCurrentCroppedMaskBitmap(EditorActivity.this, (Bitmap) null);
            BgChangeActivity.setFaceBitmap(photo_editor_view.getCurrentBitmap());
            StoreManager.setCurrentOriginalBitmap(EditorActivity.this, photo_editor_view.getCurrentBitmap());
            Intent dripIntent = new Intent(EditorActivity.this, BgChangeActivity.class);
            dripIntent.putExtra("url", url);
            startActivityForResult(dripIntent, 900);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            mLoading(false);
        }
    }


    class wingEffect extends AsyncTask<Void, Void, Void> {
        wingEffect() {
        }

        public void onPreExecute() {
            mLoading(true);
        }

        public Void doInBackground(Void... voids) {
            return null;
        }

        public void onPostExecute(Void voids) {
            StoreManager.setCurrentCropedBitmap(EditorActivity.this, (Bitmap) null);
            StoreManager.setCurrentCroppedMaskBitmap(EditorActivity.this, (Bitmap) null);
            WingActivity.setFaceBitmap(photo_editor_view.getCurrentBitmap());
            StoreManager.setCurrentOriginalBitmap(EditorActivity.this, photo_editor_view.getCurrentBitmap());
            Intent neonIntent2 = new Intent(EditorActivity.this, WingActivity.class);
            startActivityForResult(neonIntent2, 900);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            mLoading(false);
        }
    }

    class profileEffect extends AsyncTask<Void, Void, Void> {
        profileEffect() {
        }

        public void onPreExecute() {
            mLoading(true);
        }

        public Void doInBackground(Void... voids) {
            return null;
        }

        public void onPostExecute(Void voids) {
            StoreManager.setCurrentCropedBitmap(EditorActivity.this, (Bitmap) null);
            StoreManager.setCurrentCroppedMaskBitmap(EditorActivity.this, (Bitmap) null);
            ProfileActivity.setFaceBitmap(photo_editor_view.getCurrentBitmap());
            StoreManager.setCurrentOriginalBitmap(EditorActivity.this, photo_editor_view.getCurrentBitmap());
            Intent dripIntent = new Intent(EditorActivity.this, ProfileActivity.class);
            startActivityForResult(dripIntent, 900);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            mLoading(false);
        }
    }

    class neonEffect extends AsyncTask<Void, Void, Void> {
        neonEffect() {
        }

        public void onPreExecute() {
            EditorActivity.this.mLoading(true);
        }

        public Void doInBackground(Void... voids) {
            return null;
        }

        public void onPostExecute(Void voids) {
            StoreManager.setCurrentCropedBitmap(EditorActivity.this, (Bitmap) null);
            StoreManager.setCurrentCroppedMaskBitmap(EditorActivity.this, (Bitmap) null);
            NeonActivity.setFaceBitmap(EditorActivity.this.photo_editor_view.getCurrentBitmap());
            StoreManager.setCurrentOriginalBitmap(EditorActivity.this, EditorActivity.this.photo_editor_view.getCurrentBitmap());
            Intent neonIntent = new Intent(EditorActivity.this, NeonActivity.class);
            startActivityForResult(neonIntent, 900);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            EditorActivity.this.mLoading(false);
        }
    }


    class ShowSplashFragment extends AsyncTask<Void, List<Bitmap>, List<Bitmap>> {
        boolean isSplashSquared;

        public ShowSplashFragment(boolean z) {
            this.isSplashSquared = z;
        }

        public void onPreExecute() {
            mLoading(true);
        }

        public List<Bitmap> doInBackground(Void... voids) {
            Bitmap currentBitmap = photo_editor_view.getCurrentBitmap();
            List<Bitmap> arrayList = new ArrayList<>();
            arrayList.add(currentBitmap);
            if (this.isSplashSquared) {
                arrayList.add(FilterFileAsset.getBlurImageFromBitmap(currentBitmap, 2.5f));
            }
            return arrayList;
        }

        public void onPostExecute(List<Bitmap> list) {
            if (this.isSplashSquared) {
                SquareFragment.show(EditorActivity.this, list.get(0), null, list.get(1), EditorActivity.this, true);
            }
            EditorActivity.this.mLoading(false);
        }
    }

    class SaveFilterAsBitmap extends AsyncTask<Void, Void, Bitmap> {
        SaveFilterAsBitmap() {
        }

        public void onPreExecute() {
            EditorActivity.this.mLoading(true);
        }

        public Bitmap doInBackground(Void... voidArr) {
            final Bitmap[] bitmapArr = {null};
            EditorActivity.this.photo_editor_view.saveGLSurfaceViewAsBitmap(bitmap -> bitmapArr[0] = bitmap);
            while (bitmapArr[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return bitmapArr[0];
        }


        public void onPostExecute(Bitmap bitmap) {
            EditorActivity.this.photo_editor_view.setImageSource(bitmap);
            EditorActivity.this.photo_editor_view.setFilterEffect("");
            EditorActivity.this.mLoading(false);
        }
    }

    class SaveStickerAsBitmap extends AsyncTask<Void, Void, Bitmap> {
        SaveStickerAsBitmap() {
        }

        public void onPreExecute() {
            EditorActivity.this.photo_editor_view.getGLSurfaceView().setAlpha(0.0f);
            EditorActivity.this.mLoading(true);
        }

        public Bitmap doInBackground(Void... voidArr) {
            final Bitmap[] bitmapArr = {null};
            while (bitmapArr[0] == null) {
                try {
                    EditorActivity.this.photoEditor.saveStickerAsBitmap(bitmap -> bitmapArr[0] = bitmap);
                    while (bitmapArr[0] == null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                }
            }
            return bitmapArr[0];
        }

        public void onPostExecute(Bitmap bitmap) {
            EditorActivity.this.photo_editor_view.setImageSource(bitmap);
            EditorActivity.this.photo_editor_view.getStickers().clear();
            EditorActivity.this.photo_editor_view.getGLSurfaceView().setAlpha(1.0f);
            EditorActivity.this.mLoading(false);
            EditorActivity.this.updateLayout();
        }
    }

    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 123) {
            if (i2 == -1) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(intent.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    float width = (float) bitmap.getWidth();
                    float height = (float) bitmap.getHeight();
                    float max = Math.max(width / 1280.0f, height / 1280.0f);
                    if (max > 1.0f) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false);
                    }
                    if (SystemUtil.rotateBitmap(bitmap, new ExifInterface(inputStream).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)) != bitmap) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    this.photo_editor_view.setImageSource(bitmap);
                    updateLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                    MsgUtil.toastMsg(this, "Error: Can not open image");
                }
            } else {
                finish();
            }
        } else if (i == 900) {
            if (intent != null && intent.getStringExtra("MESSAGE").equals("done")) {
                if (BitmapTransfer.bitmap != null) {
                    new loadBitmap().execute(new Bitmap[]{BitmapTransfer.bitmap});
                }
            }

        }
        //else if (i == 900) {
        //   if (intent != null && intent.getStringExtra("imagePath").equals("imagePath")) {

                /*if (stringExtra != null) {
                    try {
                        sourceBitmap = BitmapFactory.decodeStream(new FileInputStream(new File(stringExtra)));
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(sourceBitmap.getWidth(), sourceBitmap.getHeight());
                        layoutParams.addRule(13, -1);
                        photo_editor_view.setLayoutParams(layoutParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Image not supported ", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }


                 */
        // bmmask = BitmapLoad.loadFromResource(this, new int[]{bmmain.getWidth(), bmmain.getHeight()}, R.drawable.mask);
        //       }

        //  }
    }

    public void isPermissionGranted(boolean z, String string) {
        if (z) {
            new SaveBitmap().execute();
        }
    }

    class loadBitmap extends AsyncTask<Bitmap, Bitmap, Bitmap> {
        loadBitmap() {
        }

        public void onPreExecute() {
            mLoading(true);
        }

        public Bitmap doInBackground(Bitmap... bitmaps) {
            try {
                Bitmap bitmap = bitmaps[0];//MediaStore.Images.Media.getBitmap(QueShotEditorActivity.this.getContentResolver(), fromFile);
                float width = (float) bitmap.getWidth();
                float height = (float) bitmap.getHeight();
                float max = Math.max(width / 1280.0f, height / 1280.0f);
                if (max > 1.0f) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false);
                }
//                Bitmap bitmap1 = SystemUtil.rotateBitmap(bitmap, new ExifInterface(QueShotEditorActivity.this.getContentResolver().openInputStream(fromFile)).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1));
//                if (bitmap1 != bitmap) {
//                    bitmap.recycle();
//                }
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        public void onPostExecute(Bitmap bitmap) {
            photo_editor_view.setImageSource(bitmap);
            updateLayout();
        }
    }


    class OnLoadBitmapFromUri extends AsyncTask<String, Bitmap, Bitmap> implements BeautyFragment.OnBeautySave {

        OnLoadBitmapFromUri() {
        }

        public void onPreExecute() {
            EditorActivity.this.mLoading(true);
        }

        public Bitmap doInBackground(String... strArr) {
            try {
                Uri fromFile = Uri.fromFile(new File(strArr[0]));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditorActivity.this.getContentResolver(), fromFile);
                float width = (float) bitmap.getWidth();
                float height = (float) bitmap.getHeight();
                float max = Math.max(width / 1280.0f, height / 1280.0f);
                if (max > 1.0f) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false);
                }
                Bitmap rotateBitmap = SystemUtil.rotateBitmap(bitmap, new ExifInterface(EditorActivity.this.getContentResolver().openInputStream(fromFile)).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1));
                if (rotateBitmap != bitmap) {
                    bitmap.recycle();
                }
                return rotateBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        public void onPostExecute(Bitmap bitmap) {
            EditorActivity.this.photo_editor_view.setImageSource(bitmap);
            EditorActivity.this.updateLayout();

            if (rv != null) {
                switch (rv) {
                    case "beautify":
                    case "makeupbt":
                        BeautyActivity.setFaceBitmap(EditorActivity.this.photo_editor_view.getCurrentBitmap());
                        startActivityForResult(new Intent(getApplicationContext(), BeautyActivity.class), 900);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;
                    case "changebg":
                        new BgEffect().execute();
                        break;
                    case "faceshaper":
                        BeautyFragment.show(EditorActivity.this, EditorActivity.this.photo_editor_view.getCurrentBitmap(), this);
                        break;
                    case "removalbt":
                        EraseActivity.b = EditorActivity.this.photo_editor_view.getCurrentBitmap();
                        Intent intent = new Intent(EditorActivity.this, EraseActivity.class);
                        intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_DRIP);
                        startActivityForResult(intent, 1024);
                        break;
                    case "stickerbt":
                        EditorActivity.this.constraint_layout_sticker.setVisibility(View.VISIBLE);
                        EditorActivity.this.linear_layout_wrapper_sticker_list.setVisibility(View.VISIBLE);
                        updateLayout();
                        slideUpSaveView();
                        EditorActivity.this.photo_editor_view.setLocked(false);
                        slideDown(EditorActivity.this.recycler_view_tools);
                        slideUp(EditorActivity.this.constraint_layout_sticker);
                        slideUp(EditorActivity.this.relativeLayoutSaveSticker);
                        setGuideLine();
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onBeautySave(Bitmap bitmap) {
            EditorActivity.this.photo_editor_view.setImageSource(bitmap);
            EditorActivity.this.currentMode = ToolEditor.NONE;
        }
    }
//    class OnLoadBitmapFromUri extends AsyncTask<String, Bitmap, Bitmap> implements BeautyFragment.OnBeautySave {
//
//        OnLoadBitmapFromUri() {
//        }
//
//        public void onPreExecute() {
//            EditorActivity.this.mLoading(true);
//        }
//
//        public Bitmap doInBackground(String... strArr) {
//            try {
//                Uri fromFile = Uri.fromFile(new File(strArr[0]));
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditorActivity.this.getContentResolver(), fromFile);
//                float width = (float) bitmap.getWidth();
//                float height = (float) bitmap.getHeight();
//                float max = Math.max(width / 1280.0f, height / 1280.0f);
//                if (max > 1.0f) {
//                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false);
//                }
//                Bitmap rotateBitmap = SystemUtil.rotateBitmap(bitmap, new ExifInterface(EditorActivity.this.getContentResolver().openInputStream(fromFile)).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1));
//                if (rotateBitmap != bitmap) {
//                    bitmap.recycle();
//                }
//                return rotateBitmap;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//
//        public void onPostExecute(Bitmap bitmap) {
//            EditorActivity.this.photo_editor_view.setImageSource(bitmap);
//            EditorActivity.this.updateLayout();
//
//            if (rv != null) {
//                switch (rv) {
//                    case "beautify":
//                    case "makeupbt":
//                        BeautyActivity.setFaceBitmap(EditorActivity.this.photo_editor_view.getCurrentBitmap());
//                        startActivityForResult(new Intent(getApplicationContext(), BeautyActivity.class), 900);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                        break;
//                    case "changebg":
//                        new BgEffect().execute();
//                        break;
//                    case "faceshaper":
//                        BeautyFragment.show(EditorActivity.this, EditorActivity.this.photo_editor_view.getCurrentBitmap(), this);
//                        break;
//                    case "removalbt":
//                        EraseActivity.b = EditorActivity.this.photo_editor_view.getCurrentBitmap();
//                        Intent intent = new Intent(EditorActivity.this, EraseActivity.class);
//                        intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_DRIP);
//                        startActivityForResult(intent, 1024);
//                        break;
//                    case "stickerbt":
//                        EditorActivity.this.constraint_layout_sticker.setVisibility(View.VISIBLE);
//                        EditorActivity.this.linear_layout_wrapper_sticker_list.setVisibility(View.VISIBLE);
//                        updateLayout();
//                        slideUpSaveView();
//                        EditorActivity.this.photo_editor_view.setLocked(false);
//                        slideDown(EditorActivity.this.recycler_view_tools);
//                        slideUp(EditorActivity.this.constraint_layout_sticker);
//                        slideUp(EditorActivity.this.relativeLayoutSaveSticker);
//                        setGuideLine();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//
//        @Override
//        public void onBeautySave(Bitmap bitmap) {
//            EditorActivity.this.photo_editor_view.setImageSource(bitmap);
//            EditorActivity.this.currentMode = ToolEditor.NONE;
//        }
//    }


    public void updateLayout() {
        this.photo_editor_view.postDelayed(() -> {
            try {
                Display defaultDisplay = EditorActivity.this.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                defaultDisplay.getSize(point);
                int i = point.x;
                int height = EditorActivity.this.relative_layout_wrapper_photo.getHeight();
                int i2 = EditorActivity.this.photo_editor_view.getGLSurfaceView().getRenderViewport().width;
                float f = (float) EditorActivity.this.photo_editor_view.getGLSurfaceView().getRenderViewport().height;
                float f2 = (float) i2;
                if (((int) ((((float) i) * f) / f2)) <= height) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
                    layoutParams.addRule(13);
                    EditorActivity.this.photo_editor_view.setLayoutParams(layoutParams);
                    EditorActivity.this.photo_editor_view.setVisibility(View.VISIBLE);
                } else {
                    RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams((int) ((((float) height) * f2) / f), -1);
                    layoutParams2.addRule(13);
                    EditorActivity.this.photo_editor_view.setLayoutParams(layoutParams2);
                    EditorActivity.this.photo_editor_view.setVisibility(View.VISIBLE);
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            EditorActivity.this.mLoading(false);
        }, 300);
    }


    class SaveBitmap extends AsyncTask<Void, String, String> {
        SaveBitmap() {
        }

        public void onPreExecute() {
            mLoading(true);
        }

        public String doInBackground(Void... voids) {
            try {
                return SaveFileUtils.saveBitmapFileEditor(EditorActivity.this, EditorActivity.this.photo_editor_view.getCurrentBitmap(), new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()), null).getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void onPostExecute(String string) {
            mLoading(false);
            if (string == null) {
                Toast.makeText(EditorActivity.this.getApplicationContext(), "Oop! Something went wrong", Toast.LENGTH_LONG).show();
                return;
            }
            String strings = "Your photo has been saved !";
            successDialog(strings, string);
            /*Intent i = new Intent(EditorActivity.this, SavedImageActivity.class);
            i.putExtra("image_uri", string);
            EditorActivity.this.startActivity(i);*/
//            adsManager.showInterstitialAd();
        }

    }

    private void successDialog(String string, String s) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        View reg_layout = inflater.inflate(R.layout.success_dialog, null);

        final TextView tv = reg_layout.findViewById(R.id.tv);

        tv.setText(string);
        dialog.setView(reg_layout);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            Intent i = new Intent(EditorActivity.this, SavedImageActivity.class);
            i.putExtra("image_uri", s);
            EditorActivity.this.startActivity(i);

        }, 2000);

        dialog.show();
    }


    public void mLoading(boolean z) {
        if (z) {
            getWindow().setFlags(16, 16);
            this.relative_layout_loading.setVisibility(View.VISIBLE);
            return;
        }
        getWindow().clearFlags(16);
        this.relative_layout_loading.setVisibility(View.GONE);
    }

}
