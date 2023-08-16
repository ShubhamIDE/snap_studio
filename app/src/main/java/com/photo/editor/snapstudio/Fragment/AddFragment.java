package com.photo.editor.snapstudio.Fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.photo.editor.snapstudio.Activity.SearchAiActivity;
import com.photo.editor.snapstudio.Activity.SubscriptionActivity;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.AiSearch.OneActivity;
import com.photo.editor.snapstudio.DetailsDialog;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.collage.Activity.SelectImageActivity;

import java.util.List;

public class AddFragment extends Fragment {

    private LinearLayout ai_image, photo_edit, collage, beauty;
    private TextView ai_image_txt, photo_edit_txt, collage_txt, beauty_txt;

    private ImageView probt;
    LinearLayout ads;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        photo_edit = view.findViewById(R.id.photo_edit);
        collage = view.findViewById(R.id.collage);
        ads = view.findViewById(R.id.native_ads);
        beauty = view.findViewById(R.id.beauty);
        probt = view.findViewById(R.id.pro);
        ai_image = view.findViewById(R.id.ai_image);
        ai_image_txt = view.findViewById(R.id.ai_image_txt);
        photo_edit_txt = view.findViewById(R.id.photo_edit_txt);
        collage_txt = view.findViewById(R.id.collage_txt);
        beauty_txt = view.findViewById(R.id.beauty_txt);
        changeTheme();


        probt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SubscriptionActivity.class));
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

        photo_edit.setOnClickListener(new View.OnClickListener() {
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
                    }
                });
                    }
                });

                return view;
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
                AdUtils.showNativeAd(requireActivity(), ads, true, isDarkThemes, false);
                if (isDarkThemes) {
//            mainLayout.setBackgroundResource(R.drawable.darkbg);
                    photo_edit_txt.setTextColor(getResources().getColor(R.color.white));
                    collage_txt.setTextColor(getResources().getColor(R.color.white));
                    ai_image_txt.setTextColor(getResources().getColor(R.color.white));
                    beauty_txt.setTextColor(getResources().getColor(R.color.white));

                } else {
//            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    photo_edit_txt.setTextColor(getResources().getColor(R.color.blacktwo));
                    collage_txt.setTextColor(getResources().getColor(R.color.blacktwo));
                    ai_image_txt.setTextColor(getResources().getColor(R.color.blacktwo));
                    beauty_txt.setTextColor(getResources().getColor(R.color.blacktwo));

                }
            }

        }