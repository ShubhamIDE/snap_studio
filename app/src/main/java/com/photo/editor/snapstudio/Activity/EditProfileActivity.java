package com.photo.editor.snapstudio.Activity;

import static com.photo.editor.snapstudio.Activity.DashboardActivity.userAge;
import static com.photo.editor.snapstudio.Activity.DashboardActivity.userGender;
import static com.photo.editor.snapstudio.Activity.DashboardActivity.userNumber;
import static com.photo.editor.snapstudio.Activity.DashboardActivity.userPhotoUrl;
import static com.photo.editor.snapstudio.Activity.DashboardActivity.username;
import static com.photo.editor.snapstudio.Global.getContentMediaUri;
import static com.photo.editor.snapstudio.Global.isLatestVersion;
import static com.photo.editor.snapstudio.Global.selectedPosition;
import static com.photo.editor.snapstudio.PhEditor.support.Constants.getPath;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.photo.editor.snapstudio.Adapter.AgeAdapter;
import com.photo.editor.snapstudio.Adapter.GenderAdapter;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces;
import com.photo.editor.snapstudio.BuildConfig;
import com.photo.editor.snapstudio.DetailsDialog;
import com.photo.editor.snapstudio.Global;
import com.photo.editor.snapstudio.PhEditor.Store.BitmapUtil;
import com.photo.editor.snapstudio.R;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private final int REQUEST_CODE_STORAGE_PERMISSION = 102;

    private ImageView backbt, savebt, username_icon, useremail_icon, userphone_icon, profile_pic, change_profile;
    EditText userName, email, phone;

    TextView title, age_title, gender_title;
    private GoogleSignInClient googleSignInClient;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    Bitmap bitmap;
    private StorageReference storRef;
    private String imgUrl;
    private Dialog loadingDialog;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Intent pictureActionIntent = null;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private static final int RESULT_OK = -1;
    String selectedImagePath, selectedGender;
    private Uri uri, imageUri;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RelativeLayout mainLayout;

    MaterialSpinner age, gender;

    String age_text, gender_text;
    Task<DocumentSnapshot> query;
    FirebaseStorage storage;

    StorageReference storageReference;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_edit_profile);
        backbt = findViewById(R.id.backbt);
        profile_pic = findViewById(R.id.profile_pic);
        change_profile = findViewById(R.id.change_profile);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        mainLayout = findViewById(R.id.main_layout);
        title = findViewById(R.id.title);
        savebt = findViewById(R.id.save_details);
        username_icon = findViewById(R.id.username_icon);
        useremail_icon = findViewById(R.id.useremail_icon);
        userphone_icon = findViewById(R.id.userphone_icon);
        age_title = findViewById(R.id.age_title);
        gender_title = findViewById(R.id.gender_title);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckPermission();
//                startDialog();
            }
        });

        ArrayList<String> ageList = new ArrayList<String>();
        ageList.add("Select Age");
        for (int i = 13; i <= 60; i++) {
            ageList.add(Integer.toString(i));
        }
//        AgeAdapter adapter = new AgeAdapter(this, ageList);
//        age.setAdapter(adapter);
        checkusername();


        age.setItems(ageList);
        boolean isDarkThemes = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkThemes) {
            age.setTextColor(getResources().getColor(R.color.white));
            age.setBackgroundColor(getResources().getColor(R.color.blacktwo));
            gender.setTextColor(getResources().getColor(R.color.white));
            gender.setBackgroundColor(getResources().getColor(R.color.blacktwo));
        } else {
            age.setTextColor(getResources().getColor(R.color.blacktwo));
            age.setBackgroundColor(getResources().getColor(R.color.white));
            gender.setTextColor(getResources().getColor(R.color.blacktwo));
            gender.setBackgroundColor(getResources().getColor(R.color.white));
        }


        age.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (!item.equals("Select Age")) {
                    age_text = item;
                } else {
                    age_text = null;
                }
            }
        });

        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Select Gender");
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Others");

        gender.setItems(genderList);
        /*GenderAdapter adapter2 = new GenderAdapter(this, genderList);
        gender.setAdapter(adapter2);
*/
        gender.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (!item.equals("Select Gender")) {
                    gender_text = item;
                } else {
                    gender_text = null;
                }
            }
        });

        userName.setText(username);
        phone.setText(userNumber);
        email.setText(auth.getCurrentUser().getEmail());
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditProfileActivity.this, "Cannot be edited!!", Toast.LENGTH_SHORT).show();
            }
        });
        if (userPhotoUrl == null) {
            profile_pic.setImageResource(R.drawable.profile);
        } else {
            Glide.with(EditProfileActivity.this).load(userPhotoUrl).circleCrop().into(profile_pic);
        }

        changeTheme();

        if (userGender == null) {
            gender.setSelectedIndex(0);
        } else {
            switch (userGender) {
                case "Male":
                    gender.setSelectedIndex(1);
                    break;
                case "Female":
                    gender.setSelectedIndex(2);
                    break;
                case "Others":
                    gender.setSelectedIndex(3);
                    break;
                default:
                    break;
            }
        }

        if (userAge == null) {
            age.setSelectedIndex(0);
        } else {
            int age_num = Integer.parseInt(userAge) - 12;
            age.setSelectedIndex(age_num);
        }


        savebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(EditProfileActivity.this, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
//                changes();
                        uploadImage();
//                addData();
                    }
                });
            }
        });

    }

//    private void changes() {
//        if (photoChanged) {
//            uploadImage();
//        } else {
//            addData();
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //     UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            storRef = FirebaseStorage.getInstance().getReference().child("profilePhoto");

            Uri imageUri = getImageUri(bitmap);
            StorageReference filePath = storRef.child(auth.getCurrentUser().getUid() + ".jpg");
            UploadTask uploadTask = filePath.putFile(imageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to Upload Photo!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e(TAG, e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            imgUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task1) {
                            if (task1.isSuccessful()) {
                                imgUrl = task1.getResult().toString();
                                Log.println(Log.ASSERT, TAG, "imgURL: " + imgUrl);
                                userPhotoUrl = imgUrl;
                                Log.println(Log.ASSERT, TAG, "userPhotoUrl: " + userPhotoUrl);
                                addDataWithProfile();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to Add Profile Photo!!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage());
                            progressDialog.dismiss();
                        }
                    });
                }
            });

            // Defining the child of storageReference
//            StorageReference ref
//                    = storageReference
//                    .child(
//                            "profilePic/"
//                                    + auth.getCurrentUser().getUid().toString());
//
//            // adding listeners on upload
//            // or failure of image
//            ref.putFile(filePath)
//                    .addOnSuccessListener(
//                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
//
//                                @Override
//                                public void onSuccess(
//                                        UploadTask.TaskSnapshot taskSnapshot)
//                                {
//
//                                    // Image uploaded successfully
//                                    // Dismiss dialog
//                                    progressDialog.dismiss();
//                                    Toast
//                                            .makeText(EditProfileActivity.this,
//                                                    "Image Uploaded!!",
//                                                    Toast.LENGTH_SHORT)
//                                            .show();
//                                }
//                            })
//
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e)
//                        {
//
//                            // Error, Image not uploaded
//                            progressDialog.dismiss();
//                            Toast
//                                    .makeText(EditProfileActivity.this,
//                                            "Failed " + e.getMessage(),
//                                            Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    })
//                    .addOnProgressListener(
//                            new OnProgressListener<UploadTask.TaskSnapshot>() {
//
//                                // Progress Listener for loading
//                                // percentage on the dialog box
//                                @Override
//                                public void onProgress(
//                                        UploadTask.TaskSnapshot taskSnapshot)
//                                {
//                                    double progress
//                                            = (100.0
//                                            * taskSnapshot.getBytesTransferred()
//                                            / taskSnapshot.getTotalByteCount());
//                                    progressDialog.setMessage(
//                                            "Uploaded "
//                                                    + (int)progress + "%");
//                                }
//                            });
        } else {
            addData();
        }
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                EditProfileActivity.this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Dexter.withContext(getApplicationContext()).withPermissions(PERMISSIONS).withListener(new MultiplePermissionsListener() {
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                                }
                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(new PermissionRequestErrorListener() {
                            public void onError(DexterError dexterError) {
                                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        }).onSameThread().check();
                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Dexter.withContext(getApplicationContext()).withPermissions(PERMISSIONS).withListener(new MultiplePermissionsListener() {
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    onCameraButtonClick();
                                }
                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(new PermissionRequestErrorListener() {
                            public void onError(DexterError dexterError) {
                                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        }).onSameThread().check();
                    }
                });
        myAlertDialog.show();
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        bitmap = null;
//        selectedImagePath = null;
//        Log.i(TAG, "requestCode: " + requestCode);
//        Log.i(TAG, "resultCode: " + resultCode);
//        Log.i(TAG, "data: " + data);
//
//        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {
//            try {
//                Uri fromFile2 = Uri.fromFile(new File(getCacheDir(),
//                        auth.getCurrentUser().getUid() + "_" + System.currentTimeMillis() + ".png"));
//                UCrop.Options options3 = new UCrop.Options();
//                options3.setToolbarColor(getResources().getColor(R.color.white));
//                options3.withAspectRatio(3, 4);
//                options3.setCompressionFormat(Bitmap.CompressFormat.PNG);
//                options3.setFreeStyleCropEnabled(false);
//                UCrop.of(uri, fromFile2).withOptions(options3).start((Activity) getApplicationContext(), UCrop.REQUEST_CROP);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_PICTURE) {
//            /*Log.i(TAG, "Gallery Loop Entered!!");
//            Log.i(TAG, "Data: "+data);*/
//            if (data != null) {
//                Log.i(TAG, "Gallery Data NOT NULL!!");
//                Uri fromFile = Uri.fromFile(new File(getCacheDir(),
//                        auth.getCurrentUser().getUid() + "_" + System.currentTimeMillis() + ".png"));
//                UCrop.Options options2 = new UCrop.Options();
//                options2.setToolbarColor(getResources().getColor(R.color.white));
//                options2.withAspectRatio(3, 4);
//                options2.setCompressionFormat(Bitmap.CompressFormat.PNG);
//                options2.setFreeStyleCropEnabled(false);
//                UCrop.of(data.getData(), fromFile).withOptions(options2).start((Activity) getApplicationContext(),  UCrop.REQUEST_CROP);
//
//            } else {
//                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            Log.i(TAG, "handleCropResult Function Called!!");
//            handleCropResult(data);
//        } else if (resultCode == UCrop.RESULT_ERROR) {
//            Log.e(TAG, "UCrop Error: " + data.toString());
//            UCrop.getError(data);
//        }
//    }

    private void onGalleryButtonClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image).toString()), GALLERY_PICTURE);
    }

    private void onCameraButtonClick() {
        uri = FileProvider.getUriForFile(
                getApplicationContext(),
                BuildConfig.APPLICATION_ID + "." + "provider",
                createCameraFile()
        );
        pictureActionIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        Log.i(TAG, "uri: " + uri);
        pictureActionIntent.putExtra("output", uri);
        startActivityForResult(pictureActionIntent, CAMERA_REQUEST);
    }

    private File createCameraFile() {
        File image = null;
        String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + auth.getCurrentUser().getUid() + "" + dateTime + "";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void handleCropResult(Intent data) {
        Bitmap bitmap = null;
        imageUri = UCrop.getOutput(data);
        ContentResolver contentResolver = getContentResolver();
        try {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapUtil.decodeBitmapFromUri(getApplicationContext(), UCrop.getOutput(data), bitmap.getWidth(),
                bitmap.getHeight(), BitmapUtil.getInSampleSizePower2());
//        civProfilePicture.setImageBitmap(bmp);
        uploadProfilePhoto(bmp);
    }

    private void showSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), (String) null));
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void uploadProfilePhoto(Bitmap bitmap) {
        loadingDialog = new Dialog(getApplicationContext());
        loadingDialog.setContentView(R.layout.progress_dialog);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(loadingDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        loadingDialog.getWindow().setAttributes(lp);
        TextView tvLoadingMessage = loadingDialog.findViewById(R.id.tvProgressText);
        tvLoadingMessage.setText("Uploading Your Profile Photo!!");
        storRef = FirebaseStorage.getInstance().getReference().child("profilePhoto");
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();*/
        Uri imageUri = getImageUri(bitmap);
        StorageReference filePath = storRef.child(imageUri.getEncodedPath() + ".jpg");
        UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to Upload Photo!!", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                Log.e(TAG, e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        imgUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task1) {
                        if (task1.isSuccessful()) {
                            imgUrl = task1.getResult().toString();
                            Log.println(Log.ASSERT, TAG, "imgURL: " + imgUrl);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to Add Profile Photo!!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage());
                        loadingDialog.dismiss();
                    }
                });
            }
        });
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, String.valueOf(System.currentTimeMillis()), null);
        return Uri.parse(path);
    }

    private void checkusername() {
        query = db.collection("User").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                        if (userPhotoUrl == null) {
                            profile_pic.setImageResource(R.drawable.profile);
                        } else {
                            Glide.with(EditProfileActivity.this).load(userPhotoUrl).circleCrop().into(profile_pic);
                        }
                        if (userAge == null) {
                            age_text = null;
                        } else {
                            age_text = userAge;
                        }
                        if (userGender == null) {
                            gender_text = null;
                        } else {
                            gender_text = userGender;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with reading and writing external storage
                OpenImageChooser();
            } else {
                // Permission denied, you cannot proceed with reading and writing external storage
                Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
                DetailsDialog.showDetailsDialog(EditProfileActivity.this);
            }
        }
    }

    private void mCheckPermission() {

        if (isLatestVersion()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                OpenImageChooser();
                // Permission already granted, you can proceed with reading and writing external storage
            }
        } else {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
//            startActivity(new Intent(ProfilePicActivity.this, WhatsAppStatusSaverActivity.class));
                OpenImageChooser();
                // Permission already granted, you can proceed with reading and writing external storage
            }
        }
        /*String[] string;

        if (isLatestVersion()) {
            string = new String[]{PermitConstant.Manifest_READ_EXTERNAL_STORAGE};
        } else {
            string = new String[]{PermitConstant.Manifest_READ_EXTERNAL_STORAGE,
                    PermitConstant.Manifest_WRITE_EXTERNAL_STORAGE};
        }

        if (isPermissionsGranted(this, string)) {
            OpenImageChooser();
        } else {
            askCompactPermissions(string, new PermissionHelperActivity.PermissionResult() {
                @Override
                public void permissionGranted() {
                    OpenImageChooser();
                }

                @Override
                public void permissionDenied() {
                    Toast.makeText(EditProfileActivity.this, "Permission Denied..!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void permissionForeverDenied() {
                    Toast.makeText(EditProfileActivity.this, "Permission Forever Denied..!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }*/
    }

    public void OpenImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, getContentMediaUri());
        intent.setType("image/*");
        startActivityForResult(intent, 22);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 22) {
                String actualFilepath = getPath(this, data.getData());
                profile_pic.setImageDrawable(null);
                Glide.with(this).load(actualFilepath).circleCrop().into(profile_pic);
                Global.ProfilePath = actualFilepath;
                // Get the Uri of data
                filePath = data.getData();
                try {

                    // Setting image on image view using Bitmap
                    bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
//                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
                /*if (animSelectionAdapter != null) {
                    animSelectionAdapter.clearSelection();
                }*/

            } else if (requestCode == 3) {
                Toast.makeText(this, "Trim video success", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void changeTheme() {
        boolean isDarkThemes = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        AdUtils.showNativeAd(EditProfileActivity.this, findViewById(R.id.native_ads), false, isDarkThemes, false);

        if (isDarkThemes) {
            mainLayout.setBackgroundResource(R.drawable.darkbg_t);
            userName.setTextColor(getResources().getColor(R.color.white));
            change_profile.setImageResource(R.drawable.camera);
            email.setTextColor(getResources().getColor(R.color.white));
            phone.setTextColor(getResources().getColor(R.color.white));
            userName.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#80FFFFFF")));
            email.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#80FFFFFF")));
            phone.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#80FFFFFF")));
            userName.setHintTextColor(Color.parseColor("#80FFFFFF"));
            email.setHintTextColor(Color.parseColor("#80FFFFFF"));
            phone.setHintTextColor(Color.parseColor("#80FFFFFF"));
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            title.setTextColor(getResources().getColor(R.color.white));
            savebt.setImageResource(R.drawable.save_white);
            age_title.setTextColor(getResources().getColor(R.color.white));
            gender_title.setTextColor(getResources().getColor(R.color.white));
            useremail_icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            username_icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            userphone_icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        } else {
            mainLayout.setBackgroundResource(R.drawable.bg_white);
            userName.setTextColor(getResources().getColor(R.color.blacktwo));
            change_profile.setImageResource(R.drawable.camera_light);
            email.setTextColor(getResources().getColor(R.color.blacktwo));
            phone.setTextColor(getResources().getColor(R.color.blacktwo));
            userName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greyt)));
            email.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greyt)));
            phone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greyt)));
            userName.setHintTextColor(getResources().getColor(R.color.greyt));
            email.setHintTextColor(getResources().getColor(R.color.greyt));
            phone.setHintTextColor(getResources().getColor(R.color.greyt));
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            title.setTextColor(getResources().getColor(R.color.blacktwo));
            savebt.setImageResource(R.drawable.save_grad);
            age_title.setTextColor(getResources().getColor(R.color.blacktwo));
            gender_title.setTextColor(getResources().getColor(R.color.blacktwo));
            username_icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            userphone_icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
            useremail_icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));

        }
    }

    private void addData() {

        String userid = auth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("User").document(userid);
        documentReference.update("firstName", userName.getText().toString());
        documentReference.update("gender", gender_text);
        documentReference.update("age", age_text);
        documentReference.update("phoneNumber", phone.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    onBackPressed();
                    Toast.makeText(EditProfileActivity.this, "Success!!", Toast.LENGTH_SHORT).show();
                    checkusername();
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addDataWithProfile() {

        String userid = auth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("User").document(userid);
        documentReference.update("firstName", userName.getText().toString());
        documentReference.update("gender", gender_text);
        documentReference.update("age", age_text);
        documentReference.update("photoUrl", userPhotoUrl);
        documentReference.update("phoneNumber", phone.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    onBackPressed();
                    Toast.makeText(EditProfileActivity.this, "Success!!", Toast.LENGTH_SHORT).show();
                    checkusername();
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        AdUtils.showInterstitialAd(EditProfileActivity.this, new AppInterfaces.InterstitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {
                EditProfileActivity.super.onBackPressed();
            }
        });
    }
}