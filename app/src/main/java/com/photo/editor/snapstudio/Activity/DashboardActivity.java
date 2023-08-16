package com.photo.editor.snapstudio.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.Fragment.AddFragment;
import com.photo.editor.snapstudio.Fragment.HomeFragment;
import com.photo.editor.snapstudio.Fragment.ProjectsFragment;
import com.photo.editor.snapstudio.Fragment.SettingsFragment;
import com.photo.editor.snapstudio.Fragment.TemplatesFragment;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.Utils;

public class DashboardActivity extends AppCompatActivity {

    public ImageView home_nav, temp_nav, project_nav, add_nav, settings_nav;
    private GoogleSignInClient googleSignInClient;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();

    public static String username, userNumber, userAge, userGender, assetpos, userPhotoUrl;
//    public static boolean isFromAsset;
    RelativeLayout mainLayout;
    Task<DocumentSnapshot> query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_dashboard);
        mainLayout = findViewById(R.id.main_layout);
        home_nav = findViewById(R.id.home_nav);
        temp_nav = findViewById(R.id.temp_nav);
        add_nav = findViewById(R.id.add_nav);
        project_nav = findViewById(R.id.project_nav);
        settings_nav = findViewById(R.id.settings_nav);
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        home_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(DashboardActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                        home_nav.setImageResource(R.drawable.selected_home);
                        temp_nav.setImageResource(R.drawable.unselected_temp);
                        project_nav.setImageResource(R.drawable.unselected_project_nav);
                        changeTheme();
                    }
                });
            }
        });

        temp_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(DashboardActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TemplatesFragment()).commit();
                        home_nav.setImageResource(R.drawable.unselected_home);
                        temp_nav.setImageResource(R.drawable.selected_temp);
                        project_nav.setImageResource(R.drawable.unselected_project_nav);
                        changeTheme();
                    }
                });
            }
        });

        project_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(DashboardActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProjectsFragment()).commit();
                        home_nav.setImageResource(R.drawable.unselected_home);
                        temp_nav.setImageResource(R.drawable.unselected_temp);
                        project_nav.setImageResource(R.drawable.selected_project_nav);
                        changeTheme();
                    }
                });
            }
        });


        settings_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(DashboardActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                        home_nav.setImageResource(R.drawable.unselected_home);
                        temp_nav.setImageResource(R.drawable.unselected_temp);
                        project_nav.setImageResource(R.drawable.unselected_project_nav);
                        changeTheme();
                    }
                });
            }
        });

        add_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(DashboardActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddFragment()).commit();
                        home_nav.setImageResource(R.drawable.unselected_home);
                        temp_nav.setImageResource(R.drawable.unselected_temp);
                        project_nav.setImageResource(R.drawable.unselected_project_nav);
                        changeTheme();
                    }
                });
            }
        });
        changeTheme();
        checkusername();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkusername();
    }

    private void changeTheme() {
        boolean isDarkThemes = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkThemes) {
            mainLayout.setBackgroundResource(R.drawable.darkbg);
        } else {
            mainLayout.setBackgroundResource(R.drawable.homepage);

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkusername();
    }

    private void checkusername() {
        query = fstore.collection("User").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    try {
                        username = snapshot.getString("firstName");
                        userNumber = snapshot.getString("phoneNumber");
                        userGender = snapshot.getString("gender");
                        userAge = snapshot.getString("age");
                        userPhotoUrl = snapshot.getString("photoUrl");
                        assetpos = snapshot.getString("assetPos");
                        if (userPhotoUrl == null) {
                            settings_nav.setImageResource(R.drawable.profile);
                        } else {
                            Glide.with(DashboardActivity.this).load(userPhotoUrl).circleCrop().into(settings_nav);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e);
            }
        });
    }

    private void exitDialog() {

        Dialog dialog = new Dialog(DashboardActivity.this, R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(this);

        View lay = inflater.inflate(R.layout.exit_dialog, null);
        TextView goback, exit, rate_txt, do_you;
        ImageView rateUs;
        RelativeLayout exit_bg;
        goback = lay.findViewById(R.id.goback);
        exit = lay.findViewById(R.id.exit);
        rateUs = lay.findViewById(R.id.rate_us);
        rate_txt = lay.findViewById(R.id.rate_txt);

        LinearGradient shader = new
                LinearGradient(0f, 0f, 0f, rate_txt.getTextSize(), Color.parseColor("#DD81FF"), Color.parseColor("#1238FF"), Shader.TileMode.CLAMP);
        rate_txt.getPaint().setShader(shader);
//        if (isDarkTheme) {
//            exit_bg.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
//            exit.setTextColor(getResources().getColor(R.color.blacktwo));
//            do_you.setTextColor(getResources().getColor(R.color.blacktwo));
//            rate_txt.setTextColor(getResources().getColor(R.color.blacktwo));
//        } else {
//            exit_bg.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
//            exit.setTextColor(getResources().getColor(R.color.white));
//            do_you.setTextColor(getResources().getColor(R.color.white));
//            rate_txt.setTextColor(getResources().getColor(R.color.white));
//        }
        dialog.setContentView(lay);
        AdUtils.showNativeAd(DashboardActivity.this, lay.findViewById(R.id.native_ad), true, false, true);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finishAffinity();
            }
        });

        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                rateApp();
            }
        });

        dialog.show();

    }

    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 33) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
        exitDialog();
    }

//    @Override
//    public void getMoreBtn(String btn) {
//        if (btn.equals("Temp")){
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TemplatesFragment()).commit();
//            home_nav.setImageResource(R.drawable.unselected_home);
//            temp_nav.setImageResource(R.drawable.selected_temp);
//            project_nav.setImageResource(R.drawable.unselected_project_nav);
//        } else {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProjectsFragment()).commit();
//            home_nav.setImageResource(R.drawable.unselected_home);
//            temp_nav.setImageResource(R.drawable.unselected_temp);
//            project_nav.setImageResource(R.drawable.selected_project_nav);
//        }
//    }
}