package com.photo.editor.snapstudio.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.User;

import java.util.Arrays;
import java.util.List;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity";
    TextView dont_have_acc, skip;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    TextInputEditText editTextEmail, editTextPassword;
    private TextView btnEmailLogin, forgotpass;
    ImageView ibGoogle;

    @Override
    protected void onResume() {
        super.onResume();
        AdUtils.loadInitialInterstitialAds(this);
        AdUtils.loadAppOpenAds(this);
        AdUtils.loadInitialNativeList(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_signin);
        dont_have_acc = findViewById(R.id.dont_have_acc);
        skip = findViewById(R.id.skip);
        ibGoogle = findViewById(R.id.google);
        btnEmailLogin = (TextView) findViewById(R.id.loginbt);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.edit_Textpassword);
        forgotpass=findViewById(R.id.newtxt);

        AdUtils.showNativeAd(SigninActivity.this, findViewById(R.id.native_ads), false, false, false);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });

        dont_have_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(SigninActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });
            }
        });

        btnEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextEmail.getText().toString();
                String textPwd = editTextPassword.getText().toString();
                if (TextUtils.isEmpty(textEmail)) {
//                    Toast.makeText(SigninActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
//                    Toast.makeText(SigninActivity.this, "Please re-enter your Email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Valid Email is required");
                    editTextEmail.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)) {
//                    Toast.makeText(SigninActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                }else {
//                    progress_bar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText reset_mail = new EditText(v.getContext());
                final AlertDialog.Builder passResetDialog = new AlertDialog.Builder(v.getContext());
                passResetDialog.setTitle("Reset Password");
                passResetDialog.setMessage("Enter your Email ID to Receive Reset Link");
                passResetDialog.setView(reset_mail);
                passResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and send the rest link
                        String mail = reset_mail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SigninActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(SigninActivity.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog box
                    }
                });
                passResetDialog.create().show();            }
        });

        ibGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInToGoogle();
            }
        });

    }



    private void loginUser(String email, String pwd) {
        fAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(SigninActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //Get instant of current user
//                    FirebaseUser firebaseUser=authProfile.getCurrentUser();
//                    assert firebaseUser != null;
//                    if (firebaseUser.isEmailVerified()){
//                    Toast.makeText(SigninActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();
//                    SplashScreenActivity.IsLogin="True";
//                    Intent intent=new Intent(SigninActivity.this, DashboardActivity.class);
//                    startActivity(intent);
//                    string = "Log in successful!!";
//                    showDialogsuccess(string);
                    Toast.makeText(SigninActivity.this, "Sign in successful!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
//                    }else{
//                        firebaseUser.sendEmailVerification();
//                        authProfile.signOut();
//                        showAlertDialog();
//                    }
                }else{
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        editTextEmail.setError("User does not exists or is no longer valid. Please register again");
                        editTextEmail.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        editTextPassword.setError("Invalid credentials. Kindly, check and re-enter");
                        editTextPassword.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(SigninActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                //progress_bar.setVisibility(View.GONE);
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
        Log.i(TAG, "requestCode: " + requestCode);
        Log.i(TAG, "resultCode: " + resultCode);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.i(TAG, "data: " + data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google Sign in Succeeded", Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign in Failed " + e, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = fAuth.getCurrentUser();
//                    List<String> lstName= Arrays.asList(user.getDisplayName().split(" "));
                    String firstName, lastName;
                    Uri photoUrl = user.getPhotoUrl();
                    firstName = user.getDisplayName();
//                    lastName = lstName.get(lstName.size()-1);
//                    Log.i(TAG, "Name List: "+lstName);
                    Log.i(TAG, "firstName: "+ firstName);
//                    Log.i(TAG, "lastName: "+ lastName);
                    fStore.collection("User").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                            if(task1.isSuccessful()){
                                DocumentSnapshot docSnapshot1= task1.getResult();
                                if(docSnapshot1.exists()){
                                    Toast.makeText(SigninActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "Login Successful for: "+fAuth.getCurrentUser().getUid());
                                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
//                                    string = "Log in successful!!";
//                                    showDialogsuccess(string);
                                    Toast.makeText(SigninActivity.this, "Sign in successful!!", Toast.LENGTH_SHORT).show();

                                    /*Intent loginIntent = new Intent(SigninActivity.this, HomeActivity.class);
                                    Toast.makeText(SigninActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                    startActivity(loginIntent);
                                    finish();*/
                                }
                                else{
                                    DocumentReference documentReference = fStore.collection("User").document(fAuth.getCurrentUser().getUid());
                                    User user1 = new User();
                                    user1.setPhotoUrl(photoUrl);
                                    user1.setFirstName(firstName);
//                                    user1.setLastName(lastName);
                                    user1.setEmailAddress(user.getEmail());
                                    user1.setProEnabled(false);
                                    user1.setPaymentId(null);
                                    user1.setStartTimeStamp(null);
                                    user1.setEndTimeStamp(null);
                                    user1.setAge(null);
                                    user1.setGender(null);
//                                    user1.setGender(null);
//                                    user1.setDateOfBirth(null);
//                                    user1.setUserContactNo(null);
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
                                                /*Intent loginIntent = new Intent(SigninActivity.this, HomeActivity.class);
                                                Toast.makeText(SigninActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                                startActivity(loginIntent);
                                                finish();*/
                                                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                                                Toast.makeText(SigninActivity.this, "Your Account is created successfully", Toast.LENGTH_SHORT).show();
//                                                string = "Your Account is created successfully";
//                                                showDialogsuccess(string);

                                                Toast.makeText(SigninActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                                                Log.i(TAG, "Login Successful for new user: "+fAuth.getCurrentUser().getUid());
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

    private void exitDialog() {

        Dialog dialog = new Dialog(SigninActivity.this, R.style.SheetDialog);
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
        AdUtils.showNativeAd(SigninActivity.this, lay.findViewById(R.id.native_ad), true, false, true);
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

}