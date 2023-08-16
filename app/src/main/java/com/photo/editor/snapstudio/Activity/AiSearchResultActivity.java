package com.photo.editor.snapstudio.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.photo.editor.snapstudio.Adapter.SearchResultAdapter;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.AiSearch.AppInterfaces;
import com.photo.editor.snapstudio.PhEditor.Activity.EditorActivity;
import com.photo.editor.snapstudio.PhEditor.picker.PhotoPicker;
import com.photo.editor.snapstudio.R;

import java.util.ArrayList;

public class AiSearchResultActivity extends AppCompatActivity implements AppInterfaces.SearchResultOnClick {

    TextView title;
    ImageView backbt;
    RelativeLayout mainLayout;
    boolean isDarkTheme;
    RecyclerView search_result_rv;
    SearchResultAdapter adapter;
    ArrayList<String> data = new ArrayList<>();

    private long downloadID;
    DownloadManager manager;
    String selectedFileName = "";

    String search;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_ai_search_result);
        title = findViewById(R.id.title);
        mainLayout = findViewById(R.id.main_layout);
        backbt = findViewById(R.id.backbt);
        search_result_rv = findViewById(R.id.search_result_rv);

        search = getIntent().getStringExtra("search");
        data = getIntent().getStringArrayListExtra("data");
        System.out.println("Url>>>>" + data);
        System.out.println("data size>>>>" + data.size());
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        adapter = new SearchResultAdapter(this, data, this);
        search_result_rv.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        search_result_rv.setAdapter(adapter);

        changeTheme();
        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void changeTheme() {
        isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        AdUtils.showNativeAd(AiSearchResultActivity.this, findViewById(R.id.native_ads), true, isDarkTheme, false);
        if (isDarkTheme) {
            mainLayout.setBackgroundResource(R.drawable.darkbg_t);
            title.setTextColor(getResources().getColor(R.color.white));
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            title.setTextColor(getResources().getColor(R.color.blacktwo));
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
        }
    }

    @Override
    public void getImageUrl(String url) {
//        showImg.setVisibility(View.VISIBLE);
//        Glide.with(this).load(url).into(showImg);
        dialogShowImage(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    @Override
    public void onBackPressed() {
        AdUtils.showInterstitialAd(AiSearchResultActivity.this, new com.photo.editor.snapstudio.AdsUtils.Interfaces.AppInterfaces.InterstitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {
                AiSearchResultActivity.super.onBackPressed();
            }
        });
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            /*if (downloadID == id) {*/
            checkIfDownloadCompleted(manager, Environment.getExternalStorageDirectory().getAbsoluteFile() +
                    "/Download/" + selectedFileName);
            hideProgressDialogg();
            Toast.makeText(AiSearchResultActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
        }
        /* }*/
    };

    private void checkIfDownloadCompleted(DownloadManager manager, String selectedFileName) {
        boolean finishDownload = false;
        int progress;
        while (!finishDownload) {
            Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(downloadID));
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(manager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_FAILED: {
                        finishDownload = true;
                        break;
                    }
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    case DownloadManager.STATUS_PENDING:
                        break;
                    case DownloadManager.STATUS_RUNNING: {
                        @SuppressLint("Range") final long total = cursor.getLong(cursor.getColumnIndex(manager.COLUMN_TOTAL_SIZE_BYTES));
                        if (total >= 0) {
                            @SuppressLint("Range") final long downloaded = cursor.getLong(cursor.getColumnIndex(manager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            progress = (int) ((downloaded * 100L) / total);
                            // if you use downloadmanger in async task, here you can use like this to display progress.
                            // Don't forget to do the division in long to get more digits rather than double.
                            //  publishProgress((int) ((downloaded * 100L) / total));
                        }
                        break;
                    }
                    case DownloadManager.STATUS_SUCCESSFUL: {
                        progress = 100;
                        finishDownload = true;
                        hideProgressDialogg();
                        Toast.makeText(AiSearchResultActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }

    public void showProgressDialog(Context context, String msg) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(null);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        try {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialogg() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void downloadImage(String url) {
        selectedFileName = getResources().getString(R.string.app_name) + "/Images/" + search + ".png";
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedFileName);
        downloadID = manager.enqueue(request);
        showProgressDialog(AiSearchResultActivity.this, "Loading...");
    }

    @Override
    public void editImage(String url) {
        Log.e("editImage: ", url );
        Intent i = new Intent(getApplicationContext(), EditorActivity.class);
        // on below line we are passing the image path to our new activity.
        i.putExtra(PhotoPicker.KEY_SELECTED_PHOTO, url);
//                i.putExtra("imgPath", mSelectedImages.get(position))
        i.putExtra("rvValue", "edit");
        i.putExtra("bgUrl", "");
        startActivity(i);
    }

    private void dialogShowImage(String url) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);


        View reg_layout = inflater.inflate(R.layout.show_result, null);

        ImageView showImg = reg_layout.findViewById(R.id.show_image);
        TextView close = reg_layout.findViewById(R.id.close_dialog);

        dialog.setView(reg_layout);

//        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });


        AlertDialog alertDialog = dialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.pink));

        Glide.with(this).load(url).into(showImg);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

/*        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });
        dialog.setPositiveButton("Search", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                String ifsc = ifscedittext.getText().toString();

                if(TextUtils.isEmpty(ifsc)){
                    Toast.makeText(IFSCcodeActivity.this, "Please type your IFSC Code...", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY, ifsc + " details");
                    startActivity(intent);
                }

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
//                dialog.dismiss();
            }
        });*/

    }
}