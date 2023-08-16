package com.photo.editor.snapstudio.Fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.photo.editor.snapstudio.Activity.DashboardActivity.userPhotoUrl;
import static com.photo.editor.snapstudio.Activity.DashboardActivity.username;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.photo.editor.snapstudio.Activity.DashboardActivity;
import com.photo.editor.snapstudio.Activity.EditProfileActivity;
import com.photo.editor.snapstudio.Activity.EditProfilePicActivity;
import com.photo.editor.snapstudio.Activity.LoginActivity;
import com.photo.editor.snapstudio.Activity.SigninActivity;
import com.photo.editor.snapstudio.Activity.SubscriptionActivity;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.BuildConfig;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.Utils;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    LinearLayout subs_set;
    TextView logout, user_name, user_email, share, pp, about, feedback, tc, notifications, theme, txt_ac, txt_set, txt_sub, txt_pre, edit;
    SwitchCompat themes_switch;
    private GoogleSignInClient googleSignInClient;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    String email = null;
    RelativeLayout mainLayout;
    boolean isDarkTheme;

    ImageView profile_pic;

    LinearLayout ads;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        user_name = view.findViewById(R.id.user_name);
        ads = view.findViewById(R.id.native_ad);
        user_email = view.findViewById(R.id.user_email);
        profile_pic = view.findViewById(R.id.profile_pic);
        subs_set = view.findViewById(R.id.subs_set);
        logout = view.findViewById(R.id.logout);
        share = view.findViewById(R.id.share);
        theme = view.findViewById(R.id.theme_txt);
        pp = view.findViewById(R.id.pp);
        txt_ac = view.findViewById(R.id.txt_ac);
        edit = view.findViewById(R.id.edit_profile);
        txt_pre = view.findViewById(R.id.txt_pre);
        txt_set = view.findViewById(R.id.txt_set);
        txt_sub = view.findViewById(R.id.txt_subs);
        about = view.findViewById(R.id.about);
        feedback = view.findViewById(R.id.feedback);
        tc = view.findViewById(R.id.tc);
        notifications = view.findViewById(R.id.notifications);
        themes_switch = view.findViewById(R.id.theme_switch);
        mainLayout = getActivity().findViewById(R.id.main_layout);

        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        user_name.setText(username);
        email = auth.getCurrentUser().getEmail();
        if (email == null) {
            user_email.setVisibility(View.GONE);
        } else {
            user_email.setText(email);
        }
        subs_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SubscriptionActivity.class));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(getActivity(), new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        startActivity(new Intent(getActivity(), EditProfilePicActivity.class));
                    }
                });
            }
        });

        feedback.setOnClickListener(view1 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacypolicy();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutUs();
            }
        });
        isDarkTheme = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        AdUtils.showNativeAd(requireActivity(), ads, false, isDarkTheme, false);
        if (isDarkTheme) {
            themes_switch.setChecked(true);
        } else {
            themes_switch.setChecked(false);
        }
        themes_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).edit().putBoolean("isDarkTheme", b).commit();
                changeTheme();
            }
        });
        changeTheme();
        if (userPhotoUrl == null) {
            profile_pic.setImageResource(R.drawable.profile);
        } else {
            Glide.with(getActivity()).load(userPhotoUrl).circleCrop().into(profile_pic);
        }


        return view;
    }

    private void changeTheme() {
        boolean isDarkThemes = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);

        if (isDarkThemes) {
            mainLayout.setBackgroundResource(R.drawable.darkbg);
            user_name.setTextColor(getResources().getColor(R.color.white));
            user_email.setTextColor(Color.parseColor("#80FFFFFF"));
            txt_sub.setTextColor(getResources().getColor(R.color.white));
            txt_set.setTextColor(getResources().getColor(R.color.white));
            txt_pre.setTextColor(Color.parseColor("#80FFFFFF"));
            txt_ac.setTextColor(getResources().getColor(R.color.white));
            share.setTextColor(getResources().getColor(R.color.white));
            share.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            notifications.setTextColor(getResources().getColor(R.color.white));
            notifications.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            pp.setTextColor(getResources().getColor(R.color.white));
            pp.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            about.setTextColor(getResources().getColor(R.color.white));
            about.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            tc.setTextColor(getResources().getColor(R.color.white));
            tc.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            theme.setTextColor(getResources().getColor(R.color.white));
            theme.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            logout.setTextColor(getResources().getColor(R.color.white));
            logout.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            feedback.setTextColor(getResources().getColor(R.color.white));
            feedback.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        } else {
            mainLayout.setBackgroundResource(R.drawable.homepage);
            user_name.setTextColor(getResources().getColor(R.color.blacktwo));
            user_email.setTextColor(getResources().getColor(R.color.greyt));
            txt_sub.setTextColor(getResources().getColor(R.color.blacktwo));
            txt_set.setTextColor(getResources().getColor(R.color.blacktwo));
            txt_pre.setTextColor(getResources().getColor(R.color.greyt));
            txt_ac.setTextColor(getResources().getColor(R.color.blacktwo));
            share.setTextColor(getResources().getColor(R.color.blacktwo));
            share.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            notifications.setTextColor(getResources().getColor(R.color.blacktwo));
            notifications.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            pp.setTextColor(getResources().getColor(R.color.blacktwo));
            pp.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            about.setTextColor(getResources().getColor(R.color.blacktwo));
            about.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            tc.setTextColor(getResources().getColor(R.color.blacktwo));
            tc.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            theme.setTextColor(getResources().getColor(R.color.blacktwo));
            theme.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            logout.setTextColor(getResources().getColor(R.color.blacktwo));
            logout.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            feedback.setTextColor(getResources().getColor(R.color.blacktwo));
            feedback.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
        }
    }

    public void share() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String shareMessage = "\nTransform your photos with Snap Studio : Photo Editor app - edit, remove backgrounds, create collages, and much more. Download the app now for ultimate photo editing experience!\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share Via"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public void privacypolicy() {
        gotoUrl("https://techiemediainc.blogspot.com/p/privacy-policy.html");
    }

//    public void tc(View view) {
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
//    }

    public void aboutUs() {
        gotoUrl("https://techiemediainc.blogspot.com/p/privacy-policy.html");
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
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getActivity().getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }


    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();

//        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(DashboardActivity.this, "User Logged Out Successfully!!", Toast.LENGTH_SHORT).show();
//                    Log.i("DASHBOARD", "User Logged Out Successfully!!");
//                }
//            }
//        });
    }

}