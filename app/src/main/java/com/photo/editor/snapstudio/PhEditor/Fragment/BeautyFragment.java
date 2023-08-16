package com.photo.editor.snapstudio.PhEditor.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.photo.editor.snapstudio.PhEditor.Editor.OnSaveBitmap;
import com.photo.editor.snapstudio.PhEditor.Editor.PhotoEditorView;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.event.ZoomIconEvent;
import com.photo.editor.snapstudio.PhEditor.sticker.BeautySticker;
import com.photo.editor.snapstudio.PhEditor.sticker.BitmapStickerIcon;
import com.photo.editor.snapstudio.PhEditor.sticker.Sticker;
import com.photo.editor.snapstudio.PhEditor.sticker.StickerView;
import com.photo.editor.snapstudio.PhEditor.views.DegreeSeekBar;

import org.wysaid.nativePort.CGEDeformFilterWrapper;
import org.wysaid.nativePort.CGEImageHandler;
import org.wysaid.texUtils.TextureRenderer;
import org.wysaid.view.ImageGLSurfaceView;

import java.util.Arrays;
import java.util.Iterator;

public class BeautyFragment extends DialogFragment {
    Bitmap bitmap;
    ImageView compare;
    int currentType = 7;
    ImageGLSurfaceView glSurfaceView;
    DegreeSeekBar intensityTwoDirection;
    RelativeLayout loadingView;
    CGEDeformFilterWrapper mDeformWrapper;
    OnBeautySave onBeautySave;

    RelativeLayout relativeLayoutBoobs;
    RelativeLayout relativeLayoutHips;
    RelativeLayout relativeLayoutFace;

    PhotoEditorView photoEditorView;
    float startX;
    float startY;
    ViewGroup viewGroup;
    public interface OnBeautySave {
        void onBeautySave(Bitmap bitmap);
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }


    private void saveCurrentState() {
        new SaveCurrentState().execute(new Void[0]);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }


    public void hideAllFunction() {
        this.intensityTwoDirection.setVisibility(View.GONE);
        this.loadingView.setVisibility(View.GONE);
    }

    public void setOnBeautySave(OnBeautySave onBeautySave2) {
        this.onBeautySave = onBeautySave2;
    }

    public static BeautyFragment show(AppCompatActivity appCompatActivity, Bitmap bitmap2, OnBeautySave onBeautySave2) {
        BeautyFragment beautyDialog = new BeautyFragment();
        beautyDialog.setBitmap(bitmap2);
        beautyDialog.setOnBeautySave(onBeautySave2);
        beautyDialog.show(appCompatActivity.getSupportFragmentManager(), "BeautyDialog");
        return beautyDialog;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    ConstraintLayout mainLayout;
    ImageView close, save, image_view_face, image_view_hips, image_view_boobs;
    TextView textView, textView2, textView3;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup2, Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.fragment_body, viewGroup2, false);
        save = inflate.findViewById(R.id.image_view_save);
        close = inflate.findViewById(R.id.image_view_close);
        textView = inflate.findViewById(R.id.text_view_whiten);
        textView2 = inflate.findViewById(R.id.text_view_eraser);
        textView3 = inflate.findViewById(R.id.text_view_face);
        mainLayout = inflate.findViewById(R.id.main_layout);
        image_view_face = inflate.findViewById(R.id.image_view_face);
        image_view_hips = inflate.findViewById(R.id.image_view_hips);
        image_view_boobs = inflate.findViewById(R.id.image_view_boobs);
        this.intensityTwoDirection = (DegreeSeekBar) inflate.findViewById(R.id.intensityTwoDirection);
        changeTheme();


//        this.intensityTwoDirection.setCenterTextColor(getResources().getColor(R.color.pink));
//        this.intensityTwoDirection.setTextColor(getResources().getColor(R.color.white));
//        this.intensityTwoDirection.setPointColor(getResources().getColor(R.color.white));
        this.intensityTwoDirection.setDegreeRange(-20, 20);
        this.photoEditorView = (PhotoEditorView) inflate.findViewById(R.id.photoEditorView);
        this.glSurfaceView = photoEditorView.getGLSurfaceView();
        this.loadingView = (RelativeLayout) inflate.findViewById(R.id.relative_layout_loading);
        this.relativeLayoutBoobs = (RelativeLayout) inflate.findViewById(R.id.relativeLayoutBoobs);
        this.relativeLayoutBoobs.setOnClickListener(this.onClickListener);
        this.relativeLayoutHips = (RelativeLayout) inflate.findViewById(R.id.relativeLayoutHips);
        this.relativeLayoutHips.setOnClickListener(this.onClickListener);
        this.relativeLayoutFace = (RelativeLayout) inflate.findViewById(R.id.relativeLayoutFace);
        this.relativeLayoutFace.setOnClickListener(this.onClickListener);
        this.viewGroup = viewGroup2;

        this.intensityTwoDirection.setScrollingListener(new DegreeSeekBar.ScrollingListener() {
            public void onScrollStart() {
                Iterator<Sticker> it = BeautyFragment.this.photoEditorView.getStickers().iterator();
                while (it.hasNext()) {
                    ((BeautySticker) it.next()).updateRadius();
                }
            }

            public void onScroll(int currentDegrees) {
                TextureRenderer.Viewport renderViewport = BeautyFragment.this.glSurfaceView.getRenderViewport();
                float w = (float) renderViewport.width;
                float h = (float) renderViewport.height;
                if (BeautyFragment.this.currentType == 7) {
                    glSurfaceView.lazyFlush(true, new Runnable() {
                        public final void run() {
                            if (mDeformWrapper != null) {
                                mDeformWrapper.restore();
                                for (Sticker next : photoEditorView.getStickers()) {
                                    PointF mappedCenterPoint2 = ((BeautySticker) next).getMappedCenterPoint2();
                                    Log.i("CURRENT", currentDegrees + "");
                                    for (int i2 = 0; i2 < Math.abs(currentDegrees); i2++) {
                                        if (currentDegrees > 0) {
                                            mDeformWrapper.bloatDeform(mappedCenterPoint2.x, mappedCenterPoint2.y, w, h, (float) (next.getWidth() / 2), 0.03f);
                                        } else if (currentDegrees < 0) {
                                            mDeformWrapper.wrinkleDeform(mappedCenterPoint2.x, mappedCenterPoint2.y, w, h, (float) (next.getWidth() / 2), 0.03f);
                                        }
                                    }
                                }
                            }
                        }
                    });
                } else if (BeautyFragment.this.currentType == 9) {
                    BeautyFragment.this.glSurfaceView.lazyFlush(true, new Runnable() {
                        public final void run() {
                            if (BeautyFragment.this.mDeformWrapper != null) {
                                BeautyFragment.this.mDeformWrapper.restore();
                                Iterator<Sticker> it = BeautyFragment.this.photoEditorView.getStickers().iterator();
                                while (it.hasNext()) {
                                    BeautySticker beautySticker = (BeautySticker) it.next();
                                    PointF mappedCenterPoint2 = beautySticker.getMappedCenterPoint2();
                                    RectF mappedBound = beautySticker.getMappedBound();
                                    for (int i = 0; i < Math.abs(currentDegrees); i++) {
                                        if (currentDegrees > 0) {
                                            float f3 = w;
                                            float f4 = h;
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(mappedBound.right - 20.0f, mappedCenterPoint2.y, mappedBound.right + 20.0f, mappedCenterPoint2.y, f3, f4, (float) beautySticker.getRadius(), 0.01f);
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(mappedBound.left + 20.0f, mappedCenterPoint2.y, mappedBound.left - 20.0f, mappedCenterPoint2.y, f3, f4, (float) beautySticker.getRadius(), 0.01f);
                                        } else {
                                            float f = w;
                                            float f2 = h;
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(mappedBound.right + 20.0f, mappedCenterPoint2.y, mappedBound.right - 20.0f, mappedCenterPoint2.y, f, f2, (float) beautySticker.getRadius(), 0.01f);
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(mappedBound.left - 20.0f, mappedCenterPoint2.y, mappedBound.left + 20.0f, mappedCenterPoint2.y, f, f2, (float) beautySticker.getRadius(), 0.01f);
                                        }
                                    }
                                }
                            }
                        }
                    });
                } else if (BeautyFragment.this.currentType == 4) {
                    BeautyFragment.this.glSurfaceView.lazyFlush(true, new Runnable() {
                        public final void run() {
                            int j;
                            if (BeautyFragment.this.mDeformWrapper != null) {
                                BeautyFragment.this.mDeformWrapper.restore();
                                Iterator<Sticker> iterator = BeautyFragment.this.photoEditorView.getStickers().iterator();
                                while (iterator.hasNext()) {
                                    BeautySticker beautySticker = (BeautySticker) iterator.next();
                                    PointF pointF = beautySticker.getMappedCenterPoint2();
                                    RectF rectF = beautySticker.getMappedBound();
                                    int i = beautySticker.getRadius() / 2;
                                    float f1 = (rectF.left + pointF.x) / 2.0f;
                                    float f2 = rectF.left + ((f1 - rectF.left) / 2.0f);
                                    float f3 = (rectF.bottom + rectF.top) / 2.0f;
                                    float f4 = rectF.top + ((f3 - rectF.top) / 2.0f);
                                    float f5 = (rectF.right + pointF.x) / 2.0f;
                                    float f6 = rectF.right - ((rectF.right - f5) / 2.0f);
                                    float f7 = (rectF.bottom + rectF.top) / 2.0f;
                                    float f8 = rectF.top + ((f7 - rectF.top) / 2.0f);
                                    int j2 = 0;
                                    Iterator<Sticker> iterator1 = iterator;
                                    while (true) {
                                        iterator = iterator1;
                                        if (j2 >= Math.abs(currentDegrees)) {
                                            break;
                                        }
                                        if (currentDegrees > 0) {
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper = BeautyFragment.this.mDeformWrapper;
                                            float f9 = rectF.right;
                                            float f10 = rectF.top;
                                            float f11 = rectF.right;
                                            float f12 = (float) i;
                                            float f = rectF.top;
                                            float f122 = f12;
                                            float f13 = f11;
                                            float f14 = f10;
                                            float f102 = f;
                                            float f15 = f9;
                                            float f16 = h;
                                            cGEDeformFilterWrapper.forwardDeform(f9, f10, f11 - f12, f102, w, f16, (float) beautySticker.getRadius(), 0.002f);
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(f6, f8, f6 - f122, f8, w, f16, (float) beautySticker.getRadius(), 0.005f);
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper2 = BeautyFragment.this.mDeformWrapper;
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper3 = cGEDeformFilterWrapper;
                                            cGEDeformFilterWrapper2.forwardDeform(f5, f7, f5 - f122, f7, w, h, (float) beautySticker.getRadius(), 0.007f);
                                            float f17 = w;
                                            float f18 = h;
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(rectF.left, rectF.top, rectF.left + f122, rectF.top, f17, f18, (float) beautySticker.getRadius(), 0.002f);
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(f2, f4, f2 + f122, f4, f17, f18, (float) beautySticker.getRadius(), 0.005f);
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper4 = BeautyFragment.this.mDeformWrapper;
                                            j = j2;
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper5 = cGEDeformFilterWrapper4;
                                            float f19 = f1;
                                            float f20 = f3;
                                            cGEDeformFilterWrapper5.forwardDeform(f19, f20, f1 + f122, f3, w, h, (float) beautySticker.getRadius(), 0.007f);
                                        } else {
                                            j = j2;
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper6 = BeautyFragment.this.mDeformWrapper;
                                            float f92 = rectF.right;
                                            float f103 = rectF.top;
                                            float f112 = rectF.right;
                                            float f123 = (float) i;
                                            float f21 = rectF.top;
                                            float f124 = f123;
                                            float f22 = f112;
                                            float f113 = f21;
                                            float f23 = f103;
                                            float f24 = f92;
                                            cGEDeformFilterWrapper6.forwardDeform(f92, f103, f112 + f123, f113, w, h, (float) beautySticker.getRadius(), 0.002f);
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(f6, f8, f6 + f124, f8, w, h, (float) beautySticker.getRadius(), 0.005f);
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(f5, f7, f5 + f124, f7, w, h, (float) beautySticker.getRadius(), 0.007f);
                                            float f25 = w;
                                            float f26 = h;
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(rectF.left + f124, rectF.top, rectF.left, rectF.top, f25, f26, (float) beautySticker.getRadius(), 0.002f);
                                            BeautyFragment.this.mDeformWrapper.forwardDeform(f2, f4, f2 - f124, f4, f25, f26, (float) beautySticker.getRadius(), 0.005f);
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper7 = BeautyFragment.this.mDeformWrapper;
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper8 = cGEDeformFilterWrapper6;
                                            CGEDeformFilterWrapper cGEDeformFilterWrapper9 = cGEDeformFilterWrapper7;
                                            float f27 = f1;
                                            float f28 = f3;
                                            cGEDeformFilterWrapper9.forwardDeform(f27, f28, f1 - f124, f3, w, h, (float) beautySticker.getRadius(), 0.007f);
                                        }
                                        j2 = j + 1;
                                    }
                                    int i2 = j2;
                                }
                            }
                        }
                    });

                }
            }

            public void onScrollEnd() {
                BeautyFragment.this.glSurfaceView.requestRender();
            }
        });
        BitmapStickerIcon bitmapStickerIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_outline_scale), 3, BitmapStickerIcon.ZOOM);
        bitmapStickerIcon.setIconEvent(new ZoomIconEvent());
        BitmapStickerIcon bitmapStickerIcon2 = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_outline_scale), 2, BitmapStickerIcon.ZOOM);
        bitmapStickerIcon2.setIconEvent(new ZoomIconEvent());
        this.photoEditorView.setIcons(Arrays.asList(new BitmapStickerIcon[]{bitmapStickerIcon, bitmapStickerIcon2}));
        this.photoEditorView.setBackgroundColor(-16777216);
        this.photoEditorView.setLocked(false);
        this.photoEditorView.setConstrained(true);
        this.photoEditorView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            public void onStickerAdded(Sticker sticker) {
            }

            public void onStickerClicked(Sticker sticker) {
            }

            public void onStickerDoubleTapped(Sticker sticker) {
            }

            public void onStickerDragFinished(Sticker sticker) {
            }

            public void onStickerFlipped(Sticker sticker) {
            }

            public void onStickerTouchOutside() {
            }

            public void onStickerTouchedDown(Sticker sticker) {
            }

            public void onStickerZoomFinished(Sticker sticker) {
            }

            public void onTouchUpForBeauty(float f, float f2) {
            }

            public void onStickerDeleted(Sticker sticker) {
                BeautyFragment.this.loadingView.setVisibility(View.GONE);
            }

            public void onTouchDownForBeauty(float f, float f2) {
                BeautyFragment.this.startX = f;
                BeautyFragment.this.startY = f2;
            }

            public void onTouchDragForBeauty(float f, float f2) {
                TextureRenderer.Viewport renderViewport = glSurfaceView.getRenderViewport();
                float f3 = (float) renderViewport.height;
                glSurfaceView.lazyFlush(true, new Runnable() {


                    public final void run() {

                        if (mDeformWrapper != null) {
                            mDeformWrapper.forwardDeform(startX, startY, f, f2, renderViewport.width, f3, 200.0f, 0.02f);
                        }
                    }
                });
                startX = f;
                startY = f2;
            }
        });
        ImageView imageView = (ImageView) inflate.findViewById(R.id.image_view_compare_filter);
        this.compare = imageView;
        imageView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    photoEditorView.getGLSurfaceView().setAlpha(0.0f);
                    return true;
                } else if (actionMasked != 1) {
                    return true;
                } else {
                    photoEditorView.getGLSurfaceView().setAlpha(1.0f);
                    return false;
                }
            }
        });
        inflate.findViewById(R.id.image_view_save).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                new SaveCurrentState(true).execute(new Void[0]);
            }
        });
        inflate.findViewById(R.id.image_view_close).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                dismiss();
            }
        });
        this.photoEditorView.setImageSource(this.bitmap, new ImageGLSurfaceView.OnSurfaceCreatedCallback() {
            public final void surfaceCreated() {
                Bitmap bitmap2 = bitmap;
                if (bitmap2 != null) {
                    glSurfaceView.setImageBitmap(bitmap2);
                    glSurfaceView.queueEvent(new Runnable() {
                        public final void run() {
                            float width = (float) bitmap.getWidth();
                            float height = (float) bitmap.getHeight();
                            float min = Math.min(((float) glSurfaceView.getRenderViewport().width) / width, ((float) glSurfaceView.getRenderViewport().height) / height);
                            if (min < 1.0f) {
                                width *= min;
                                height *= min;
                            }
                            CGEDeformFilterWrapper create = CGEDeformFilterWrapper.create((int) width, (int) height, 10.0f);
                            mDeformWrapper = create;
                            create.setUndoSteps(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                            if (mDeformWrapper != null) {
                                CGEImageHandler imageHandler = glSurfaceView.getImageHandler();
                                imageHandler.setFilterWithAddres(mDeformWrapper.getNativeAddress());
                                imageHandler.processFilters();
                            }
                        }
                    });
                }
            }
        });
        this.photoEditorView.post(new Runnable() {
            public final void run() {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(glSurfaceView.getRenderViewport().width, glSurfaceView.getRenderViewport().height);
                layoutParams.addRule(13);
                photoEditorView.setLayoutParams(layoutParams);
            }
        });
        hideAllFunction();
        return inflate;
    }

    private void changeTheme() {
        boolean isDarkTheme = requireActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkTheme){
            mainLayout.setBackgroundResource(R.color.blacktwo);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            this.intensityTwoDirection.setCenterTextColor(getResources().getColor(R.color.pink));
            this.intensityTwoDirection.setTextColor(getResources().getColor(R.color.white));
            this.intensityTwoDirection.setPointColor(getResources().getColor(R.color.white));
         /*   image_view_boobs.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_hips.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            image_view_face.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            textView.setTextColor(getResources().getColor(R.color.blacktwo));
            textView2.setTextColor(getResources().getColor(R.color.blacktwo));
            textView3.setTextColor(getResources().getColor(R.color.blacktwo));*/

        }else {
            mainLayout.setBackgroundResource(R.color.white);
            close.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            save.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            this.intensityTwoDirection.setCenterTextColor(getResources().getColor(R.color.pink));
            this.intensityTwoDirection.setTextColor(getResources().getColor(R.color.blacktwo));
            this.intensityTwoDirection.setPointColor(getResources().getColor(R.color.blacktwo));
            /*textView.setTextColor(getResources().getColor(R.color.blacktwo));
            image_view_boobs.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_hips.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            image_view_face.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            textView.setTextColor(getResources().getColor(R.color.white));
            textView2.setTextColor(getResources().getColor(R.color.white));
            textView3.setTextColor(getResources().getColor(R.color.white));*/
        }
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(-16777216));
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public final void onClick(View v) {
            switch (v.getId()) {
                case R.id.relativeLayoutBoobs:
                    saveCurrentState();
                    intensityTwoDirection.setVisibility(View.VISIBLE);
                    intensityTwoDirection.setDegreeRange(-30, 30);
                    photoEditorView.setDrawCirclePoint(false);
                    currentType = 7;
                    intensityTwoDirection.setCurrentDegrees(0);
                    photoEditorView.getStickers().clear();
                    photoEditorView.addSticker(new BeautySticker(getContext(), 0, ContextCompat.getDrawable(getContext(), R.drawable.circle)));
                    photoEditorView.addSticker(new BeautySticker(getContext(), 1, ContextCompat.getDrawable(getContext(), R.drawable.circle)));
                    relativeLayoutBoobs.setBackgroundResource(R.drawable.background_item_selected);
                    relativeLayoutHips.setBackgroundResource(R.drawable.background_item);
                    relativeLayoutFace.setBackgroundResource(R.drawable.background_item);
                    return;
                case R.id.relativeLayoutFace:
                    saveCurrentState();
                    intensityTwoDirection.setVisibility(View.VISIBLE);
                    intensityTwoDirection.setDegreeRange(-15, 15);
                    photoEditorView.setDrawCirclePoint(false);
                    currentType = 4;
                    intensityTwoDirection.setCurrentDegrees(0);
                    photoEditorView.getStickers().clear();
                    photoEditorView.addSticker(new BeautySticker(getContext(), 4, ContextCompat.getDrawable(getContext(), R.drawable.chin)));
                    relativeLayoutBoobs.setBackgroundResource(R.drawable.background_item);
                    relativeLayoutHips.setBackgroundResource(R.drawable.background_item);
                    relativeLayoutFace.setBackgroundResource(R.drawable.background_item_selected);
                    return;
                case R.id.relativeLayoutHips:
                    saveCurrentState();
                    intensityTwoDirection.setVisibility(View.VISIBLE);
                    intensityTwoDirection.setDegreeRange(-30, 30);
                    photoEditorView.setDrawCirclePoint(false);
                    intensityTwoDirection.setCurrentDegrees(0);
                    currentType = 9;
                    photoEditorView.getStickers().clear();
                    photoEditorView.addSticker(new BeautySticker(getContext(), 2, ContextCompat.getDrawable(getContext(), R.drawable.hip_1)));
                    relativeLayoutBoobs.setBackgroundResource(R.drawable.background_item);
                    relativeLayoutHips.setBackgroundResource(R.drawable.background_item_selected);
                    relativeLayoutFace.setBackgroundResource(R.drawable.background_item);
                    return;
                default:
                    return;
            }
        }
    };


    class SaveCurrentState extends AsyncTask<Void, Void, Bitmap> {
        boolean isCloseDialog;

        SaveCurrentState() {
        }

        SaveCurrentState(boolean z) {
            this.isCloseDialog = z;
        }

        public void onPreExecute() {
            BeautyFragment.this.getDialog().getWindow().setFlags(16, 16);
            BeautyFragment.this.loadingView.setVisibility(View.VISIBLE);
        }

        public Bitmap doInBackground(Void... voidArr) {
            final Bitmap[] bitmapArr = {null};
            BeautyFragment.this.photoEditorView.saveGLSurfaceViewAsBitmap(new OnSaveBitmap() {
                public void onFailure(Exception exc) {
                }

                public void onBitmapReady(Bitmap bitmap) {
                    bitmapArr[0] = bitmap;
                }
            });
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
            BeautyFragment.this.photoEditorView.setImageSource(bitmap);
            BeautyFragment.this.loadingView.setVisibility(View.GONE);
            try {
                BeautyFragment.this.getDialog().getWindow().clearFlags(16);
            } catch (Exception e) {
            }
            BeautyFragment.this.glSurfaceView.flush(true, new Runnable() {
                public final void run() {
                    if (BeautyFragment.this.mDeformWrapper != null) {
                        BeautyFragment.this.mDeformWrapper.restore();
                        BeautyFragment.this.glSurfaceView.requestRender();
                    }
                }
            });
            if (this.isCloseDialog) {
                BeautyFragment.this.onBeautySave.onBeautySave(bitmap);
                BeautyFragment.this.dismiss();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        CGEDeformFilterWrapper cGEDeformFilterWrapper = this.mDeformWrapper;
        if (cGEDeformFilterWrapper != null) {
            cGEDeformFilterWrapper.release(true);
            this.mDeformWrapper = null;
        }
        this.glSurfaceView.release();
        this.glSurfaceView.onPause();
    }
}
