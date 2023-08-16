package com.photo.editor.snapstudio.PhEditor.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.editor.snapstudio.PhEditor.Adapter.MirrorAdapter;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.PhEditor.assets.FilterFileAsset;
import com.photo.editor.snapstudio.PhEditor.mirror.Mirror3D_2Layer;
import com.photo.editor.snapstudio.PhEditor.mirror.Mirror3D_4Layer;
import com.photo.editor.snapstudio.PhEditor.views.SquareLayout;

public class MirrorFragment extends DialogFragment implements MirrorAdapter.Mirror3Listener {
    private static final String TAG = "MirrorFragment";
    private Bitmap bitmap;
    private Bitmap blurBitmap;

    public FrameLayout frame_layout_wrapper, frame;
    public RatioFragment.RatioSaveListener ratioSaveListener;
    private SquareLayout squareLayout3D_1;
    private SquareLayout squareLayout3D_3;
    //3D
    Mirror3D_2Layer dragLayout3D_1, dragLayout3D_2;
    Mirror3D_4Layer dragLayout3D_3, dragLayout3D_4, dragLayout3D_5, dragLayout3D_6;
    ImageView imageView3D_1, imageView3D_2;
    ImageView imageView3D_3, imageView3D_4, imageView3D_5, imageView3D_6;
    public RecyclerView recycler_view_background;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static MirrorFragment show(@NonNull AppCompatActivity appCompatActivity, RatioFragment.RatioSaveListener ratioSaveListener, Bitmap mBitmap, Bitmap iBitmap) {
        MirrorFragment ratioFragment = new MirrorFragment();
        ratioFragment.setBitmap(mBitmap);
        ratioFragment.setBlurBitmap(iBitmap);
        ratioFragment.setRatioSaveListener(ratioSaveListener);
        ratioFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return ratioFragment;
    }
    public void setRatioSaveListener(RatioFragment.RatioSaveListener iRatioSaveListener) {
        this.ratioSaveListener = iRatioSaveListener;
    }
    public void setBlurBitmap(Bitmap bitmap2) {
        this.blurBitmap = bitmap2;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }
    ConstraintLayout mainLayout;
    ImageView close, save;
    TextView textView;
    @SuppressLint("WrongConstant")
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.fragment_mirrors, viewGroup, false);
        initViews(inflate);
        return inflate;
    }

    private void initViews(View inflate) {
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.frame_layout_wrapper = inflate.findViewById(R.id.frameLayoutWrapper);
        this.frame = inflate.findViewById(R.id.frameLayout3D_1);
        inflate.findViewById(R.id.imageViewCloseMirror).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MirrorFragment.this.dismiss();
            }
        });
        inflate.findViewById(R.id.imageViewSaveMirror).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new SaveRatioView().execute(getBitmapFromView(MirrorFragment.this.frame_layout_wrapper));
            }
        });

        this.mainLayout = inflate.findViewById(R.id.main_layout);
        this.save = inflate.findViewById(R.id.imageViewSaveMirror);
        this.close = inflate.findViewById(R.id.imageViewCloseMirror);
        this.textView = inflate.findViewById(R.id.textViewTitle);
        changeTheme();
        this.recycler_view_background = inflate.findViewById(R.id.recyclerViewMirror);
        this.recycler_view_background.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        this.recycler_view_background.setAdapter(new MirrorAdapter(getContext(), this));
        this.recycler_view_background.setVisibility(View.VISIBLE);

        this.squareLayout3D_1 = inflate.findViewById(R.id.squareLayout3d_1);
        this.squareLayout3D_1.setVisibility(View.VISIBLE);
        this.squareLayout3D_3 = inflate.findViewById(R.id.squareLayout3d_3);
        this.squareLayout3D_3.setVisibility(View.GONE);
        this.frame.setVisibility(View.VISIBLE);

        //3D
        this.dragLayout3D_1 = inflate.findViewById(R.id.drag3D_1);
        this.dragLayout3D_2 = inflate.findViewById(R.id.drag3D_2);
        dragLayout3D_1.init(getContext(),dragLayout3D_2);
        dragLayout3D_2.init(getContext(),dragLayout3D_1);
        dragLayout3D_1.applyScaleAndTranslation();
        dragLayout3D_2.applyScaleAndTranslation();
        this.imageView3D_1 = inflate.findViewById(R.id.imageView3D_1);
        this.imageView3D_1.setImageBitmap(this.bitmap);
        this.imageView3D_1.setAdjustViewBounds(true);
        this.imageView3D_2 = inflate.findViewById(R.id.imageView3D_2);
        this.imageView3D_2.setImageBitmap(this.bitmap);
        this.imageView3D_2.setAdjustViewBounds(true);

        this.dragLayout3D_3 = inflate.findViewById(R.id.drag3D_3);
        this.dragLayout3D_4 = inflate.findViewById(R.id.drag3D_4);
        this.dragLayout3D_5 = inflate.findViewById(R.id.drag3D_5);
        this.dragLayout3D_6 = inflate.findViewById(R.id.drag3D_6);
        dragLayout3D_3.init(getContext(),dragLayout3D_4, dragLayout3D_5, dragLayout3D_6);
        dragLayout3D_4.init(getContext(),dragLayout3D_5, dragLayout3D_6, dragLayout3D_3);
        dragLayout3D_5.init(getContext(),dragLayout3D_6, dragLayout3D_3, dragLayout3D_4);
        dragLayout3D_6.init(getContext(),dragLayout3D_3, dragLayout3D_4, dragLayout3D_5);
        dragLayout3D_3.applyScaleAndTranslation();
        dragLayout3D_4.applyScaleAndTranslation();
        dragLayout3D_5.applyScaleAndTranslation();
        dragLayout3D_6.applyScaleAndTranslation();
        this.imageView3D_3 = inflate.findViewById(R.id.imageView3D_3);
        this.imageView3D_3.setImageBitmap(this.bitmap);
        this.imageView3D_3.setAdjustViewBounds(true);
        this.imageView3D_4 = inflate.findViewById(R.id.imageView3D_4);
        this.imageView3D_4.setImageBitmap(this.bitmap);
        this.imageView3D_4.setAdjustViewBounds(true);
        this.imageView3D_5 = inflate.findViewById(R.id.imageView3D_5);
        this.imageView3D_5.setImageBitmap(this.bitmap);
        this.imageView3D_5.setAdjustViewBounds(true);
        this.imageView3D_6 = inflate.findViewById(R.id.imageView3D_6);
        this.imageView3D_6.setImageBitmap(this.bitmap);
        this.imageView3D_6.setAdjustViewBounds(true);

    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(-16777216));
        }
    }

    private void changeTheme() {
        boolean isDarkTheme = requireActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
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


    public void onStop() {
        super.onStop();
    }

    @Override
    public void onMirrorSelected(int item, MirrorAdapter.SquareView squareView) {
        this.frame.setBackgroundResource(squareView.mirror);
        this.frame.setVisibility(View.VISIBLE);
        if(squareView.text.equals("3D-1") || squareView.text.equals("3D-2") || squareView.text.equals("3D-3")
                || squareView.text.equals("3D-4") || squareView.text.equals("3D-5") || squareView.text.equals("3D-6")
                || squareView.text.equals("3D-7") || squareView.text.equals("3D-8") || squareView.text.equals("3D-9")
                || squareView.text.equals("3D-10") || squareView.text.equals("3D-11") || squareView.text.equals("3D-12")){

            squareLayout3D_1.setVisibility(View.VISIBLE);
            squareLayout3D_3.setVisibility(View.GONE);

        }  else if(squareView.text.equals("3D-13") || squareView.text.equals("3D-14") || squareView.text.equals("3D-15")){
            squareLayout3D_1.setVisibility(View.GONE);
            squareLayout3D_3.setVisibility(View.VISIBLE);
        }
        this.frame_layout_wrapper.invalidate();

    }

    class SaveRatioView extends AsyncTask<Bitmap, Bitmap, Bitmap> {
        SaveRatioView() {
        }

        public void onPreExecute() {
        }

        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap cloneBitmap = FilterFileAsset.cloneBitmap(bitmapArr[0]);
            bitmapArr[0].recycle();
            bitmapArr[0] = null;
            return cloneBitmap;
        }

        public void onPostExecute(Bitmap bitmap) {
            MirrorFragment. this.ratioSaveListener.ratioSavedBitmap(bitmap);
            MirrorFragment.this.dismiss();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.blurBitmap != null) {
            this.blurBitmap.recycle();
            this.blurBitmap = null;
        }
        this.bitmap = null;
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
