package com.example.mas;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class StudentAboutActivity extends AppCompatActivity {

    private ViewPager mPager;
    private int[] layouts = {
            R.layout.student_about_page1,
            R.layout.student_about_page2,
            R.layout.student_about_page3,
            R.layout.student_about_page4,
            R.layout.student_about_page5
    };
    private LinearLayout Dots_layout;
    private MpagerAdapter mMpagerAdapter;
    private ImageView backArrow;

    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.student_about);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mMpagerAdapter = new MpagerAdapter(layouts, this);
        mPager.setAdapter(mMpagerAdapter);

        Dots_layout = (LinearLayout) findViewById(R.id.dosLayout);
        createDots(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createDots(int current_position) {
        if(Dots_layout != null) {
            Dots_layout.removeAllViews();
        }

        dots = new ImageView[layouts.length];

        for(int i=0; i<layouts.length; i++) {
            dots[i] = new ImageView(this);
            if(i == current_position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            }
            else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4 ,0);
            Dots_layout.addView(dots[i], params);
        }
    }
}
