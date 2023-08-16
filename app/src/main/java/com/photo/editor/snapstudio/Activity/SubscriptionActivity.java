package com.photo.editor.snapstudio.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photo.editor.snapstudio.R;

public class SubscriptionActivity extends AppCompatActivity {

    ImageView backbt, ytick, mtick;
    String amt = null;
    RelativeLayout yplan, mplan;
    TextView paybt, textView, textView2, tv;
    boolean isDarkTheme;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_subscription);
        ytick = findViewById(R.id.ytick);
        mtick = findViewById(R.id.mtick);
        yplan = findViewById(R.id.year_plan);
        mplan = findViewById(R.id.month_plan);
        textView = findViewById(R.id.text);
        textView2 = findViewById(R.id.text2);
        mainLayout = findViewById(R.id.main_layout);
        tv = findViewById(R.id.tv);
        TextView txt = findViewById(R.id.txt);
        changeTheme();
        yplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ytick.setVisibility(View.VISIBLE);
                mtick.setVisibility(View.GONE);
                amt = "2200";
            }
        });

        mplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtick.setVisibility(View.VISIBLE);
                ytick.setVisibility(View.GONE);
                amt = "400";
            }
        });
        LinearGradient shader = new /*LinearGradient(0, 0, 0, 20,
                new int[]{Color.BLUE, Color.CYAN},
                new float[]{0, 1}, Shader.TileMode.CLAMP);*/
                LinearGradient(0f, 0f, 0f, txt.getTextSize(), Color.parseColor("#987CDB"), Color.parseColor("#33D0E0"), Shader.TileMode.CLAMP);
        txt.getPaint().setShader(shader);


    }

    private void changeTheme() {
        isDarkTheme = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
            mainLayout.setBackgroundResource(R.drawable.darkbg_t);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView2.setTextColor(getResources().getColor(R.color.white));
            tv.setTextColor(getResources().getColor(R.color.white));
        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            textView.setTextColor(getResources().getColor(R.color.greyt));
            textView2.setTextColor(getResources().getColor(R.color.greyt));
            tv.setTextColor(getResources().getColor(R.color.blacktwo));
        }
    }
}