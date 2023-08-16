package com.photo.editor.snapstudio.Activity;

import static android.os.Build.VERSION.SDK_INT;

import static com.photo.editor.snapstudio.Global.getContentMediaUri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.photo.editor.snapstudio.Adapter.ProfilepicAdapter;
import com.photo.editor.snapstudio.DetailsDialog;
import com.photo.editor.snapstudio.Global;
import com.photo.editor.snapstudio.MyStaggeredGridLayoutManager;
import com.photo.editor.snapstudio.PhEditor.support.Constants;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuestLoginActivity extends AppCompatActivity implements ProfilepicAdapter.OnClickListener {

    private static final String TAG = "GuestLoginActivity";
    ProfilepicAdapter animSelectionAdapter;
    String[] images = new String[0];
    ArrayList<String> listImages;

    ImageView circularimg;
    RecyclerView profileRV;
    TextView mTxtChoosePic;
    Bitmap bitmap;
    private Uri filePath;

    Task<DocumentSnapshot> query;
    FirebaseStorage storage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView nextBtn, set_user_text;
    private String imgUrl;
//    private boolean isFromAssets = true;
    private int assetPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_guest_login);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        circularimg = findViewById(R.id.circularimg);
        profileRV = findViewById(R.id.profileRV);
        nextBtn = findViewById(R.id.nextbtn);
        mTxtChoosePic = findViewById(R.id.mTxtChoosePic);
        set_user_text = findViewById(R.id.set_user_text);

        LinearGradient shader = new
                LinearGradient(0f, 0f, 0f, set_user_text.getTextSize(), Color.parseColor("#DD81FF"), Color.parseColor("#1238FF"), Shader.TileMode.CLAMP);
        set_user_text.getPaint().setShader(shader);

        LinearGradient shader2 = new
                LinearGradient(0f, 0f, 0f, mTxtChoosePic.getTextSize(), Color.parseColor("#DD81FF"), Color.parseColor("#1238FF"), Shader.TileMode.CLAMP);
        mTxtChoosePic.getPaint().setShader(shader2);

        getAnimData();

        mTxtChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] arrPermissionsCollage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrPermissionsCollage = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO};
                }
                Dexter.withContext(GuestLoginActivity.this).withPermissions(arrPermissionsCollage).withListener(new MultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            OpenImageChooser();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            DetailsDialog.showDetailsDialog(GuestLoginActivity.this);
                        }
                    }

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError -> Toast.makeText(GuestLoginActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap == null){
                    Toast.makeText(GuestLoginActivity.this, "Please select profile image!!", Toast.LENGTH_SHORT).show();
                } else {
                    createUser();
                }
            }
        });

    }

    private void createUser() {
        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = auth.getCurrentUser();
//                            updateUI(user);
                            uploadImage(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(GuestLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, String.valueOf(System.currentTimeMillis()), null);
        return Uri.parse(path);
    }


    private void uploadImage(FirebaseUser user) {

        if (bitmap != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            storageReference = FirebaseStorage.getInstance().getReference().child("profilePhoto");
            Uri imageUri = getImageUri(bitmap);
            StorageReference filePath = storageReference.child(user.getUid() + ".jpg");
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
                                Log.println(Log.ASSERT, TAG, "onComplete Image: " + imgUrl);
                                User user1=new User();
                                user1.setFirstName("Guest");
                                user1.setGender(null);
                                user1.setAge(null);
                                user1.setEmailAddress(null);
                                user1.setPhoneNumber(null);
                                user1.setPhotoUrl(Uri.parse(imgUrl));
                                user1.setAssetPos(String.valueOf(assetPosition));
//                                user1.setFromAssets(isFromAssets);
                                db.collection("User").document(user.getUid()).set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
//                                            Toast.makeText(GuestLoginActivity.this, "Success!!", Toast.LENGTH_SHORT).show();
//                                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                            String string = "Your Account is created successfully";
                                            successDialog(string);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(GuestLoginActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Failure: "+e.getMessage());
                                        e.printStackTrace();
                                    }
                                });
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
        }

    }

    private void successDialog(String string) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        View reg_layout = inflater.inflate(R.layout.success_dialog, null);

        final TextView tv = reg_layout.findViewById(R.id.tv);

        tv.setText(string);
        dialog.setView(reg_layout);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

        }, 2000);

        dialog.show();
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
                String actualFilepath = Constants.getPath(this, data.getData());
                circularimg.setImageDrawable(null);
                Glide.with(this).load(actualFilepath).circleCrop().into(circularimg);
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
                    assetPosition = -1;
//                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
                if (animSelectionAdapter != null) {
                    animSelectionAdapter.clearSelection();
                }
            }
        }
    }

    private void getAnimData() {
        try {
            images = getAssets().list("Anims");
            listImages = new ArrayList<String>(Arrays.asList(images));
            animSelectionAdapter = new ProfilepicAdapter(this, listImages, this, assetPosition);
            profileRV.setLayoutManager(new MyStaggeredGridLayoutManager(GuestLoginActivity.this, 3));
            profileRV.setAdapter(animSelectionAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onclickAnim(String string, int mPos) {
        InputStream inputstream = null;
        try {
            Global.ProfilePath = "Anims/" + string;
            inputstream = getAssets().open("Anims/" + string);
            Drawable drawable = Drawable.createFromStream(inputstream, null);
            circularimg.setImageDrawable(null);
            circularimg.setImageDrawable(drawable);
            bitmap = drawableToBitmap(drawable);
            assetPosition = mPos;
            Log.i(TAG, "onclickAnim: ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onLoadAnim(String string) {
////        InputStream inputstream = null;
////        try {
////            Global.ProfilePath = "Anims/" + string;
////            inputstream = getAssets().open("Anims/" + string);
////            Drawable drawable = Drawable.createFromStream(inputstream, null);
////            circularimg.setImageBitmap(null);
////            circularimg.setImageDrawable(drawable);
////            bitmap = drawableToBitmap(drawable);
////            assetPosition = 0;
////            Log.i(TAG, "onLoadAnim: ");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}