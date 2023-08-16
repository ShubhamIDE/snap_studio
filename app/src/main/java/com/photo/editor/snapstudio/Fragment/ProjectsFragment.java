package com.photo.editor.snapstudio.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.photo.editor.snapstudio.Activity.DashboardActivity;
import com.photo.editor.snapstudio.Activity.ImageActivity;
import com.photo.editor.snapstudio.Activity.SavedImageActivity;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.BuildConfig;
import com.photo.editor.snapstudio.PhEditor.Activity.EditorActivity;
import com.photo.editor.snapstudio.PhEditor.picker.PhotoPicker;
import com.photo.editor.snapstudio.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ProjectsFragment extends Fragment {

    private RecyclerView listCreation;
    ArrayList<File_Model> img_path;
    TextView empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        boolean isDarkTheme = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        AdUtils.showNativeAd(requireActivity(), view.findViewById(R.id.native_ad), false, isDarkTheme, false);
        listCreation = view.findViewById(R.id.list_creation);
        empty = view.findViewById(R.id.empty);
        listCreation.setLayoutManager(new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false));
        new LoadImages().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return view;
    }

    private class LoadImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            updateFileList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            if (img_path.size() == 0) {
//                rlsph.setVisibility(View.VISIBLE);
//            } else {
//                rlsph.setVisibility(View.GONE);
            if (img_path.size() != 0){
                listCreation.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                CreationAdapter creationAdapter = new CreationAdapter(img_path);
                listCreation.setAdapter(creationAdapter);
            }else {
                listCreation.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
//            }
        }
    }

//    private void updateFileList() {
//        String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + getResources().getString(R.string.app_name);
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//
//        img_path = new ArrayList<>();
//        Comparator<File> fileDateCmp = new Comparator<File>() {
//            @Override
//            public int compare(File o1, File o2) {
//                return 0;
//            }
//        };
//
//        if (files != null) {
//            Arrays.sort(files, fileDateCmp);
//
//            for (int i = 0; i < files.length; i++) {
//                File_Model file_model = new File_Model();
//                file_model.file_path = files[i].getAbsolutePath();
//                file_model.file_title = files[i].getName();
//                img_path.add(file_model);
//            }
//        }
//
//    }

    private void updateFileList() {
        String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + getResources().getString(R.string.app_name);
        File directory = new File(path);
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals("Images");
            }
        });

        img_path = new ArrayList<>();
        Comparator<File> fileDateCmp = new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                // Compare the last modified date of the files
                long lastModified1 = o1.lastModified();
                long lastModified2 = o2.lastModified();
                if (lastModified1 > lastModified2) {
                    return -1; // o1 is newer than o2, so return -1 to place it first
                } else if (lastModified1 < lastModified2) {
                    return 1; // o1 is older than o2, so return 1 to place it after o2
                } else {
                    return 0; // o1 and o2 have the same last modified date, so return 0
                }
            }
        };

        if (files != null) {
            Arrays.sort(files, fileDateCmp);

            for (int i = 0; i < files.length; i++) {
                File_Model file_model = new File_Model();
                file_model.file_path = files[i].getAbsolutePath();
                file_model.file_title = files[i].getName();
                img_path.add(file_model);
            }
        }
    }

    private class File_Model {
        String file_path;
        String file_title;
    }

    private class CreationAdapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<File_Model> paths;

        public CreationAdapter(ArrayList<File_Model> imgPath) {
            this.paths = imgPath;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;

            holder.img_creation.setLayoutParams(new RelativeLayout.LayoutParams(width / 2, width / 2));

//            Glide.with(getActivity()).load(paths.get(position).file_path).into(holder.img_creation);
            Glide.with(getActivity()).load(paths.get(position).file_path)
                    .apply(new RequestOptions().transform(new RoundedCorners(50)))
                    .into(holder.img_creation);
            holder.txt_title.setText(paths.get(position).file_title);
            holder.img_dlt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    setOnMenuPressDialog();
                    Dialog dialogOnBackPressed = new Dialog(getContext());
                    dialogOnBackPressed.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogOnBackPressed.setCancelable(true);
                    dialogOnBackPressed.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    Window window = dialogOnBackPressed.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    dialogOnBackPressed.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    wlp.gravity = Gravity.CENTER;
                    window.setAttributes(wlp);
                    dialogOnBackPressed.setContentView(R.layout.dialog_menu);
                    TextView textViewEdit, textViewShare, textViewDelete;
                    textViewEdit = dialogOnBackPressed.findViewById(R.id.textViewEdit);
                    textViewShare = dialogOnBackPressed.findViewById(R.id.textViewShare);
                    textViewDelete = dialogOnBackPressed.findViewById(R.id.textViewDelete);

                    textViewEdit.setOnClickListener(view -> {
                        String uri = paths.get(position).file_path;
                        try {
                            Intent i = new Intent(requireActivity(), EditorActivity.class);
                            i.putExtra(PhotoPicker.KEY_SELECTED_PHOTOS, uri);
//                i.putExtra("imgPath", mSelectedImages.get(position))
                            i.putExtra("rvValue", "edit");
                            i.putExtra("bgUrl", "");
                            startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    textViewShare.setOnClickListener(view -> {
                        String uri = paths.get(position).file_path;
                        File saved_file = new File(uri);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        share.putExtra(Intent.EXTRA_TEXT, "Share Via");
                        share.putExtra(
                                "android.intent.extra.STREAM",
                                FileProvider.getUriForFile(
                                        requireActivity(),
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        saved_file
                                )
                        );
                        share.setType("image/*");
                        startActivity(Intent.createChooser(share, "Share Image"));
                    });

                    textViewDelete.setOnClickListener(view -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure you want to delete?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filepath = paths.get(position).file_path;
                                if (new File(filepath).delete()) {
                                    paths.remove(position);
                                    notifyDataSetChanged();
                                }
                                dialog.dismiss();
                                dialogOnBackPressed.dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blacktwo));
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blacktwo));

                    });

    /*    textViewCancel.setOnClickListener(view -> {
            dialogOnBackPressed.dismiss();
        });

        textViewDiscard.setOnClickListener(view -> {
            dialogOnBackPressed.dismiss();
            startActivity(new Intent(requireActivity(), DashboardActivity.class));
            EditorActivity.this.finish();
        });*/

                    dialogOnBackPressed.show();
                }
            });


            holder.img_creation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = paths.get(position).file_path;
                    /*if (uri.contains(".3gp") || uri.contains(".3GP") || uri.contains(".flv") || uri.contains(
                            ".FLv"
                    ) || uri.contains(".mov") || uri.contains(".MOV") || uri.contains(".wmv") || uri.contains(
                            ".WMV"
                    ) || uri.contains(".mp4") || uri.contains(".Mp4") || uri.contains(".MP4")
                    ) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), ShowVideoActivity.class);
                            intent.putExtra("video_uri", uri);
                            intent.putExtra("type", "video");
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (uri.contains(".mp3") || uri.contains(".Mp3") || uri.contains(".MP3") || uri.contains(".aac") || uri.contains(".AAC")){
                        try {
                            Intent intent = new Intent(getApplicationContext(), AudioPlayer.class);
                            intent.putExtra("song", uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{*/
                    try {
                        Intent intent = new Intent(getActivity(), ImageActivity.class);
                        intent.putExtra("image_uri", uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return paths.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_creation, img_dlt;
        TextView txt_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_creation = itemView.findViewById(R.id.img_creation);
            img_dlt = itemView.findViewById(R.id.img_dlt);
            txt_title = itemView.findViewById(R.id.txt_title);

        }
    }
}