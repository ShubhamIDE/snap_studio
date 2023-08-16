package com.photo.editor.snapstudio.Fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;

import static com.photo.editor.snapstudio.Global.hideProgressDialog;
import static com.photo.editor.snapstudio.Global.showProgressDialog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.photo.editor.snapstudio.Activity.DashboardActivity;
import com.photo.editor.snapstudio.Activity.ImageActivity;
import com.photo.editor.snapstudio.Activity.SavedImageActivity;
import com.photo.editor.snapstudio.Activity.SearchAiActivity;
import com.photo.editor.snapstudio.Activity.SubscriptionActivity;
import com.photo.editor.snapstudio.Adapter.TemplateUrlAdapter;
import com.photo.editor.snapstudio.Adapter.viewPager2Adapter;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.AiSearch.OneActivity;
import com.photo.editor.snapstudio.AiSearch.model.Image;
import com.photo.editor.snapstudio.DetailsDialog;
import com.photo.editor.snapstudio.Global;
import com.photo.editor.snapstudio.PhEditor.Model.Templates;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.Templates.frame.ChooseFrameActivity;
import com.photo.editor.snapstudio.collage.Activity.SelectImageActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private TextView title, ai_image, photo_edit, collage, beauty, recent_txt, temp_txt, temp_more, recent_more, empty;
    private ViewPager viewpager;
    private ImageView probt;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    RecyclerView recyclerView;
    ArrayList<File_Model> file_list;
    RecyclerView temprv;
    TemplateUrlAdapter adapter;
    Task<DocumentSnapshot> collageQuery;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    FirebaseFirestore fstore;
    ArrayList<Templates> TemplateItems = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        boolean isDarkTheme = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        AdUtils.showNativeAd(requireActivity(), view.findViewById(R.id.native_ads), true, isDarkTheme, false);
        title = view.findViewById(R.id.title);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        temp_more = view.findViewById(R.id.temp_more);
        empty = view.findViewById(R.id.empty);
        recent_more = view.findViewById(R.id.recent_more);
        temprv = view.findViewById(R.id.temp_rv);
        ai_image = view.findViewById(R.id.ai_image);
        probt = view.findViewById(R.id.pro);
        viewpager = view.findViewById(R.id.viewpager);
        sliderDotspanel = view.findViewById(R.id.SliderDots);
        photo_edit = view.findViewById(R.id.photo_edit);
        collage = view.findViewById(R.id.collage);
        beauty = view.findViewById(R.id.beauty);
        recent_txt = view.findViewById(R.id.recent_txt);
        temp_txt = view.findViewById(R.id.temp_txt);
        recyclerView = view.findViewById(R.id.recent_rv);
        changeTheme();

        viewPager2Adapter viewPager2Adapter = new viewPager2Adapter(getActivity());
        viewpager.setAdapter(viewPager2Adapter);


        dotscount = viewPager2Adapter.getCount();
        dots = new ImageView[dotscount];


        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(getActivity());
            if (isDarkTheme) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.nonactive_dot_dark));

            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.nonactive_dot));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        if (isDarkTheme) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot_dark));

        } else {
            dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));
        }

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    if (isDarkTheme) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.nonactive_dot_dark));

                    } else {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.nonactive_dot));
                    }
                }

                if (isDarkTheme) {
                    dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot_dark));

                } else {
                    dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        ai_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        startActivity(new Intent(getActivity(), OneActivity.class));
                    }
                });
            }
        });


        ImageView home_nav, temp_nav, project_nav;
        home_nav = requireActivity().findViewById(R.id.home_nav);
        temp_nav = requireActivity().findViewById(R.id.temp_nav);
        project_nav = requireActivity().findViewById(R.id.project_nav);

        temp_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {

//                moreBtnOnClick.getMoreBtn("Temp");
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TemplatesFragment()).commit();
                        home_nav.setImageResource(R.drawable.unselected_home);
                        temp_nav.setImageResource(R.drawable.selected_temp);
                        project_nav.setImageResource(R.drawable.unselected_project_nav);
                    }
                });
            }
        });

        recent_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {

//                moreBtnOnClick.getMoreBtn("recent");
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProjectsFragment()).commit();
                        home_nav.setImageResource(R.drawable.unselected_home);
                        temp_nav.setImageResource(R.drawable.unselected_temp);
                        project_nav.setImageResource(R.drawable.selected_project_nav);
                    }
                });
            }
        });

        probt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        startActivity(new Intent(getActivity(), SubscriptionActivity.class));
                    }
                });
            }
        });
        showProgressDialog(getContext(), "Loading...");

        collageQuery = fstore.collection("Templates").document("Templates").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    TemplateItems = new ArrayList<>();
                    List<HashMap<String, String>> testlist = (List<HashMap<String, String>>) snapshot.getData().get("data");
                    for (int i = 0; i < testlist.size(); i++) {
                        Templates singleItem = new Templates();
                        singleItem.setImageUrl(testlist.get(i).get("imageUrl"));
                        TemplateItems.add(singleItem);
                    }
                    hideProgressDialog();
                    adapter = new TemplateUrlAdapter(getActivity(), TemplateItems, "template");
                    LinearLayoutManager bmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    temprv.setLayoutManager(bmanager);
                    temprv.setAdapter(adapter);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();

                Toast.makeText(getActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });


        collage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        String[] arrPermissionsCollage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrPermissionsCollage = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO};
                        }
                        Dexter.withContext(getActivity()).withPermissions(arrPermissionsCollage).withListener(new MultiplePermissionsListener() {
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                    checkClick();
                                    Intent intent = new Intent(getActivity(), SelectImageActivity.class);
                                    intent.putExtra("rvValues", "collage");
                                    startActivity(intent);
                                }
                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    DetailsDialog.showDetailsDialog(getActivity());
                                }
                            }

                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(dexterError -> Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();

                    }
                });
            }
        });

        photo_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {

                        String[] arrPermissionsEdit = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            arrPermissionsEdit = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO};
                        Dexter.withContext(getActivity()).withPermissions(arrPermissionsEdit).withListener(new MultiplePermissionsListener() {
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    checkClick();
                                    Intent intent = new Intent(getActivity(), SelectImageActivity.class);
//                            Intent intent = new Intent(getActivity(), GalleryActivity.class);
                                    intent.putExtra("rvValues", "edit");
                                    intent.putExtra("bgUrl", "");
                                    startActivity(intent);
                                }

                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    DetailsDialog.showDetailsDialog(getActivity());
                                }
                            }

                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }

                        }).withErrorListener(dexterError -> Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
//                    }
//                });

                    }
                });
            }
        });

        beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
//                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterStitialADInterface() {
//                    @Override
//                    public void adLoadState(boolean isLoaded) {
                        String[] arrPermissionsEdit = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            arrPermissionsEdit = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO};
                        Dexter.withContext(getActivity()).withPermissions(arrPermissionsEdit).withListener(new MultiplePermissionsListener() {
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    checkClick();
                                    Intent intent = new Intent(getActivity(), SelectImageActivity.class);
//                            Intent intent = new Intent(getActivity(), GalleryActivity.class);
                                    intent.putExtra("rvValues", "beautify");
                                    intent.putExtra("bgUrl", "");
                                    startActivity(intent);

                                }

                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    DetailsDialog.showDetailsDialog(getActivity());
                                }
                            }

                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }

                        }).withErrorListener(dexterError -> Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
//                    }
//                });
                    }
                });
            }
        });

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        new LoadImages().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    /*  public class MyTimerTask extends TimerTask {

          @Override
          public void run() {

              requireActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {

                      if(viewpager.getCurrentItem() == 0){
                          viewpager.setCurrentItem(1);
                      } else if(viewpager.getCurrentItem() == 1){
                          viewpager.setCurrentItem(2);
                      }else if(viewpager.getCurrentItem() == 2){
                          viewpager.setCurrentItem(3);
                      } else {
                          viewpager.setCurrentItem(0);
                      }

                  }
              });

          }
      }*/
    public void showProgressDialog(Context context, String msg) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(null);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        try {
            if (progressDialog != null && !progressDialog.isShowing()) progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (file_list.size() != 0) {
                recyclerView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                RecentAdapter adapter = new RecentAdapter(file_list);
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setVisibility(View.GONE);
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
//        file_list = new ArrayList<>();
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
//                file_list.add(file_model);
//            }
//        }
//
//    }

    private void updateFileList() {
        String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + getResources().getString(R.string.app_name);
        File directory = new File(path);
//        if (directory.exists()) {
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals("Images");
            }
        });


        file_list = new ArrayList<>();
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
                file_list.add(file_model);
            }

        }
//        } else {
//            System.out.println("Empty");
//        }
    }


 /*   private void updateFileList() {
        String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + getResources().getString(R.string.app_name);
        File directory = new File(path);
        File[] files = directory.listFiles();

        file_list = new ArrayList<>();
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
                file_list.add(file_model);
            }
        }
    }*/

    private class File_Model {
        String file_path;
        String file_title;
    }

    private class RecentAdapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<File_Model> paths;
        int i;

        public RecentAdapter(ArrayList<File_Model> imgPath) {
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

            if (paths.size() != 0) {
                Glide.with(getActivity()).load(paths.get(position).file_path).apply(new RequestOptions().transform(new RoundedCorners(50))).into(holder.img_creation);
                holder.txt_title.setText(paths.get(position).file_title);
            } else {
               /* holder.relativeLayout.setVisibility(View.GONE);
                holder.empty.setVisibility(View.VISIBLE);*/
                System.out.println("Empty");
            }

//            holder.img_dlt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setMessage("Are you sure you want to delete?");
//
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            String filepath = paths.get(position).file_path;
//                            if (new File(filepath).delete()) {
//                                paths.remove(position);
//                                notifyDataSetChanged();
//                            }
//                            dialog.dismiss();
//                        }
//                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.setCancelable(false);
//                    alertDialog.show();
//                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
//                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
//                }
//            });
            holder.img_dlt.setVisibility(View.GONE);
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
            if (paths.size() > 6) {
                return 6;
            } else {
                return paths.size();
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_creation, img_dlt;
        TextView txt_title, empty;

        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_creation = itemView.findViewById(R.id.img_creation);
            img_dlt = itemView.findViewById(R.id.img_dlt);
            txt_title = itemView.findViewById(R.id.txt_title);
        }

    }

    private long mLastClickTime = 0;

    private void checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }


    private void changeTheme() {
        boolean isDarkThemes = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);

        if (isDarkThemes) {
            title.setTextColor(getResources().getColor(R.color.pink));
//            mainLayout.setBackgroundResource(R.drawable.darkbg);
            photo_edit.setTextColor(getResources().getColor(R.color.white));
            collage.setTextColor(getResources().getColor(R.color.white));
            ai_image.setTextColor(getResources().getColor(R.color.white));
            beauty.setTextColor(getResources().getColor(R.color.white));
            recent_txt.setTextColor(getResources().getColor(R.color.white));
            temp_txt.setTextColor(getResources().getColor(R.color.white));
        } else {
            LinearGradient shader = new
                    LinearGradient(0f, 0f, 0f, title.getTextSize(), Color.parseColor("#DD81FF"), Color.parseColor("#1238FF"), Shader.TileMode.CLAMP);
            title.getPaint().setShader(shader);
//            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            photo_edit.setTextColor(getResources().getColor(R.color.blacktwo));
            collage.setTextColor(getResources().getColor(R.color.blacktwo));
            ai_image.setTextColor(getResources().getColor(R.color.blacktwo));
            beauty.setTextColor(getResources().getColor(R.color.blacktwo));
            recent_txt.setTextColor(getResources().getColor(R.color.blacktwo));
            temp_txt.setTextColor(getResources().getColor(R.color.blacktwo));
        }
    }


}