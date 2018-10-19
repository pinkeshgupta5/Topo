package com.dhivi.inc.topo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.fragments.PagerFragment;
import com.dhivi.inc.topo.presenter.NewHomeActivity;
import com.dhivi.inc.topo.utils.MyLinearLayout;

/**
 * Created by User on 11/27/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter implements ViewPager.PageTransformer {
    public final static float BIG_SCALE = 0.9f;
    public final static float SMALL_SCALE = 0.1f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private MyLinearLayout cur = null;
    private MyLinearLayout next = null;
    private Context context;
    private FragmentManager fm;
    private float scale;

    public PagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (position == NewHomeActivity.FIRST_PAGE)
            scale = BIG_SCALE;
        else
            scale = SMALL_SCALE;

        position = position % NewHomeActivity.PAGES;
        return PagerFragment.newInstance(context, position, scale);
    }

    @Override
    public int getCount() {
        return NewHomeActivity.PAGES * NewHomeActivity.LOOPS;
    }

    @Override
    public void transformPage(View page, float position) {
        MyLinearLayout myLinearLayout = (MyLinearLayout) page.findViewById(R.id.root);
        float scale = BIG_SCALE;
        if (position > 0) {
            scale = scale - position * DIFF_SCALE;
        } else {
            scale = scale + position * DIFF_SCALE;
        }
        if (scale < 0) scale = 0;
        myLinearLayout.setScaleBoth(scale);
    }
}

