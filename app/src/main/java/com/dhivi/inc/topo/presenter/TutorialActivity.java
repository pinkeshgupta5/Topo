package com.dhivi.inc.topo.presenter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.TutorialPagerAdapter;
import com.dhivi.inc.topo.utils.StoreDetails;

import java.util.ArrayList;

public class TutorialActivity extends AppCompatActivity {
    ViewPager tutorialViewPager;
    PagerAdapter pagerAdapter;

    int totalcount = 3;
    private LinearLayout pager_indicator;
    RelativeLayout tut_viewPagerIndicator;
    ArrayList<ImageView> imagedots = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialViewPager = (ViewPager) findViewById(R.id.tutorial_view_pager);
        pagerAdapter = new TutorialPagerAdapter(this, pageListener);
        tutorialViewPager.setAdapter(pagerAdapter);


        pager_indicator = (LinearLayout) findViewById(R.id.tut_viewPagerCountDots);
        tut_viewPagerIndicator = (RelativeLayout) findViewById(R.id.tut_viewPagerIndicator);
        //setUiPageViewController(3);
        //selectPagerDot(0);
        tutorialViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               // selectPagerDot(position);
                if(position==2)
                {
                    StoreDetails.setTutorialData(TutorialActivity.this,"data");
                    Intent i = new Intent(TutorialActivity.this, FbLoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    TutorialPagerAdapter.TutorialPageListener pageListener = new TutorialPagerAdapter.TutorialPageListener() {
        @Override
        public void onClick(int position) {
            if (position < pagerAdapter.getCount() - 1) {
                tutorialViewPager.setCurrentItem(position + 1, true);
               // selectPagerDot(position + 1);
            } else {
                //  finish();
                /*Intent i = new Intent(TutorialActivity.this, FbLoginActivity.class);
                startActivity(i);
                finish();*/
            }
        }
    };

   /* private void setUiPageViewController(int total) {


        totalcount = total;
        for (int i = 0; i < totalcount; i++) {
            try {
                ImageView dot = new ImageView(TutorialActivity.this);
                dot.setImageDrawable(getResources().getDrawable(R.drawable.tutorial_dot_unselected));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        30,
                        30
                );
                params.setMargins(7, 0, 7, 0);
                pager_indicator.addView(dot, params);


                imagedots.add(dot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void selectPagerDot(int idx) {

        try {
            Resources res = getResources();
            int profileList = totalcount;


            for (int i = 0; i < profileList; i++) {
                int drawableId = (i == idx) ? (R.drawable.tutorial_dot_selected) : (R.drawable.tutorial_dot_unselected);
                Drawable drawable = res.getDrawable(drawableId);
                imagedots.get(i).setImageDrawable(drawable);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
