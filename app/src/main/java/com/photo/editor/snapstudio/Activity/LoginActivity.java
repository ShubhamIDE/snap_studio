package com.photo.editor.snapstudio.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.User;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    TextView guestLogin;
    ImageView googleLogin;
    FirebaseAuth firebaseauth;
    FirebaseFirestore fStore;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_login);
        firebaseauth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        guestLogin = findViewById(R.id.guest_login);
        googleLogin = findViewById(R.id.google_login);

        LinearGradient shader = new
                LinearGradient(0f, 0f, 0f, guestLogin.getTextSize(), Color.parseColor("#DD81FF"), Color.parseColor("#1238FF"), Shader.TileMode.CLAMP);
        guestLogin.getPaint().setShader(shader);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInToGoogle();
            }
        });

        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GuestLoginActivity.class));
            }
        });

    }

    private void signInToGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Toast.makeText(this, "Google Sign in Succeeded", Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign in Failed " + e, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void exitDialog() {

        Dialog dialog = new Dialog(LoginActivity.this, R.style.SheetDialog);
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
        AdUtils.showNativeAd(LoginActivity.this, lay.findViewById(R.id.native_ad), true, false, true);
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseauth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseauth.getCurrentUser();
//                    List<String> lstName= Arrays.asList(user.getDisplayName().split(" "));
                    String firstName, lastName;
                    Uri photoUrl = user.getPhotoUrl();
                    firstName = user.getDisplayName();
//                    lastName = lstName.get(lstName.size()-1);
                    Log.i(TAG, "photoUrl: "+photoUrl);
                    Log.i(TAG, "firstName: "+ firstName);
//                    Log.i(TAG, "lastName: "+ lastName);
                    fStore.collection("User").document(firebaseauth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                            if(task1.isSuccessful()){
                                DocumentSnapshot docSnapshot1= task1.getResult();
                                if(docSnapshot1.exists()){
//                                    Toast.makeText(LoginActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "Login Successful for: "+firebaseauth.getCurrentUser().getUid());
//                                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                                    String string = "Log in successful!!";
                                    showDialogsuccess(string);
//                                    Toast.makeText(LoginActivity.this, "Log in successful!!", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                    /*Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                    Toast.makeText(LoginActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                    startActivity(loginIntent);
                                    finish();*/
                                }
                                else{
                                    DocumentReference documentReference = fStore.collection("User").document(firebaseauth.getCurrentUser().getUid());
                                    User user1 = new User();
                                    user1.setPhotoUrl(photoUrl);
                                    user1.setFirstName(firstName);
//                                    user1.setLastName(lastName);
                                    user1.setEmailAddress(user.getEmail());
                                    user1.setPhoneNumber(null);
                                    user1.setProEnabled(false);
                                    user1.setPaymentId(null);
                                    user1.setStartTimeStamp(null);
                                    user1.setEndTimeStamp(null);
                                    user1.setAge(null);
                                    user1.setGender(null);
                                    user1.setAssetPos("-1");
//                                    user1.setWeight(null);
//                                    user1.setHeight(null);
//                                    user1.setUserBMI(null);
//                                    user1.setUserAge(null);
//                                    user1.setPrefDistance(null);
//                                    user1.setPrefGender(null);
//                                    user1.setPrefAgeStart(null);
//                                    user1.setPrefAgeEnd(null);
                                    documentReference.set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task2) {
                                            if (task2.isSuccessful()) {
                                                Log.i(TAG, "currentUser Phone: "+user.getPhoneNumber());
                                                Log.i(TAG, "currentUser ID: "+user.getUid());
                                                Log.i(TAG, "currentUser Email: "+user.getEmail());
                                                Log.i(TAG, "currentUser Name: "+user.getDisplayName());
                                                Log.i(TAG, "firebaseAuthWithGoogle: "+task.getResult());
                                                /*Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                                Toast.makeText(LoginActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                                startActivity(loginIntent);
                                                finish();*/
//                                                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                                               String string = "Your Account is created successfully";
                                                showDialogsuccess(string);
//                                                Toast.makeText(LoginActivity.this, "Your Account is created successfully!!", Toast.LENGTH_SHORT).show();
//                                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
//                                                Toast.makeText(LoginActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                                Log.i(TAG, "Login Successful for new user: "+firebaseauth.getCurrentUser().getUid());
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });


                } else {
                    Log.w(TAG, "signInWithCredential:failure: " + task.getException());
                }
            }
        });
    }

    private void showDialogsuccess(String s) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        View reg_layout = inflater.inflate(R.layout.success_dialog, null);

        final TextView tv = reg_layout.findViewById(R.id.tv);

        tv.setText(s);
        dialog.setView(reg_layout);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

        }, 2000);

        dialog.show();
    }

}
