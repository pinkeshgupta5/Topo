package com.dhivi.inc.topo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dhivi.inc.topo.R;


public class TutorialPagerAdapter extends PagerAdapter {
    private TutorialPageListener pageListener;
    private static final int[] images = {
            R.drawable.tutorial_screen_1,
            R.drawable.tutorial_screen_2,
            R.drawable.tutorial_screen_3,
    };

    Context context;
    LayoutInflater layoutInflater;

    public TutorialPagerAdapter(Context context, TutorialPageListener listener) {
        this.context = context;
        pageListener = listener;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.view_pager_tutorial_item, container, false);
        itemView.setTag(position);
        itemView.setOnClickListener(onClickListener);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.view_pager_tutorial_item_image);
        imageView.setImageResource(images[position]);

        container.addView(itemView);
        return itemView;
    }

    /*@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }*/
    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (pageListener != null) {
                int position = (int) v.getTag();
                pageListener.onClick(position);
            }
        }
    };

    public interface TutorialPageListener {
        void onClick(int position);
    }
}
