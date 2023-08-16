package com.photo.editor.snapstudio.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.photo.editor.snapstudio.R;

public class ViewAdapter extends PagerAdapter {

    private Context context;

    private LayoutInflater layoutInflater;
    private Integer[] images={R.drawable.one,R.drawable.two,R.drawable.three, R.drawable.four};
    private Integer[] text={R.string.one,R.string.two,R.string.three, R.string.four};

    public ViewAdapter(Context context)
    {
        this.context=context;
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        View view =layoutInflater.inflate(R.layout.item,null);
        ImageView imageView=view.findViewById(R.id.image_view);
        TextView textView=view.findViewById(R.id.textView);
        imageView.setImageResource(images[position]);
        textView.setText(text[position]);
        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);

        /*nextbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (position < 2) {
//                    viewPager.addView(view, 1);
//                } else {
//
//                }

                switch (position){
                    case 0:
                        viewPager.;
                }
            }
        });

        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(context, DashboardActivity.class));

            }
        });*/

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager=(ViewPager) container;
        View view=(View) object;
        viewPager.removeView(view);
    }
}

