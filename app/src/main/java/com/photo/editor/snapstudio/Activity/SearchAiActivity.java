package com.photo.editor.snapstudio.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photo.editor.snapstudio.AiSearch.AppAsyncTask;
import com.photo.editor.snapstudio.AiSearch.AppInterfaces;
import com.photo.editor.snapstudio.R;

import org.jsoup.select.Elements;

import java.util.ArrayList;

public class SearchAiActivity extends AppCompatActivity {

    private static final String TAG = "SearchAiActivity";
    TextView generate_img, txt;
    boolean isDarkTheme;
    LinearLayout mainLayout;
    ImageView backbt;

    EditText editText;
    ArrayList<String> imagesList;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_search_ai);
        generate_img = findViewById(R.id.generate_img);
        mainLayout = findViewById(R.id.main_layout);
        backbt = findViewById(R.id.backbt);
        txt = findViewById(R.id.txt);
        editText = findViewById(R.id.ed_txt);

        changeTheme();

        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        generate_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog(SearchAiActivity.this, "Loading...");
//                generateImage();

            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (i)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            showProgressDialog(SearchAiActivity.this, "Loading...");
                            generateImage();
                            return true;
                        default:
                            break;
                    }
                } else {
                    System.out.println("Error!!");
                }
                return false;
            }
        });
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


    private void generateImage() {
        hideKeyboard();
        if (editText.getText().toString().equals("") || editText.getText() == null) {
            editText.setError("Please write something to search!!");
        } else {
            try {
                        /*AppAsyncTask.SearchResult searchResult = new AppAsyncTask.SearchResult(SearchAiActivity.this, new AppInterfaces.SearchResult() {
                            @Override
                            public void getImageElements(Elements scrapedElementsList) {
                                if (!scrapedElementsList.isEmpty()) {
                                    imagesList = new ArrayList<>();
                                    for (int i = 0; i < scrapedElementsList.size(); i++) {
                                        imagesList.add(scrapedElementsList.get(i).select("img").attr("src"));
                                    }
                                    System.out.println("image list size " + imagesList.size());
                                    startActivity(new Intent(getApplicationContext(), AiSearchResultActivity.class).putExtra("data",imagesList));
                                }
                                System.out.println(scrapedElementsList.toString());

                            }

                            @Override
                            public void getSingleElementAsString(String singleElementAsString) {

                            }
                        }, editText.getText().toString());
                        searchResult.execute();*/
                AppAsyncTask.SearchResultG searchResult = new AppAsyncTask.SearchResultG(SearchAiActivity.this, new AppInterfaces.SearchResult() {
                    @Override
                    public void getImageElements(Elements scrapedElementsList) {
                        if (!scrapedElementsList.isEmpty()) {
                            imagesList = new ArrayList<>();
                            for (int i = 0; i < scrapedElementsList.size(); i++) {
                                String imgURL = scrapedElementsList.get(i).select("img").attr("data-src");
                                if (!imgURL.equals("")) {
                                    imagesList.add(imgURL);
                                }
                            }
                            hideProgressDialogg();
                            startActivity(new Intent(getApplicationContext(), AiSearchResultActivity.class).putExtra("data", imagesList).putExtra("search", editText.getText().toString()));
                        }
                        System.out.println(scrapedElementsList.toString());

                    }

                    @Override
                    public void getSingleElementAsString(String singleElementAsString) {

                    }
                }, editText.getText().toString());
                searchResult.execute();
            } catch (Exception e) {
//                        throw new RuntimeException(e);
                System.out.println("exception>>>>" + e.getMessage());
            }
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void changeTheme() {
        isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
            mainLayout.setBackgroundResource(R.drawable.darkbg_t);
            generate_img.setTextColor(getResources().getColor(R.color.blacktwo));
            txt.setTextColor(getResources().getColor(R.color.greyt));
            generate_img.setBackgroundResource(R.drawable.curvebt3);
            editText.setHintTextColor(getResources().getColor(R.color.greyt));
            editText.setTextColor(getResources().getColor(R.color.white));
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            generate_img.setTextColor(getResources().getColor(R.color.white));
            txt.setTextColor(Color.parseColor("#4D000000"));
            generate_img.setBackgroundResource(R.drawable.curvebt);
            editText.setHintTextColor(Color.parseColor("#4D000000"));
            editText.setTextColor(getResources().getColor(R.color.blacktwo));
            backbt.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blacktwo)));
        }
    }
}