package com.dhivi.inc.topo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.presenter.InstatntTopoActivity;
import com.dhivi.inc.topo.presenter.MapActivity;
import com.dhivi.inc.topo.presenter.SendRequestTopoActivity;
import com.dhivi.inc.topo.utils.MyLinearLayout;

/**
 * Created by User on 11/27/2017.
 */

public class PagerFragment extends Fragment {




    public static Fragment newInstance(Context context, int pos, float scale) {
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putFloat("scale", scale);
        return Fragment.instantiate(context, PagerFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout l = (LinearLayout)
                inflater.inflate(R.layout.item, container, false);
        int pos = this.getArguments().getInt("pos");
        final TextView text = (TextView) l.findViewById(R.id.text);
        ImageView content = (ImageView)l.findViewById(R.id.shadow_image);



        if(pos==0)
        {
           //Picasso.with(getActivity()).load("https://s3.ap-south-1.amazonaws.com/edispatch/Topo/request+topo9.png").into(content);
            content.setImageResource(R.drawable.pager_addtopo);
            text.setText(getActivity().getString(R.string.addtopo));
        }else if(pos==1)
        {
           //Picasso.with(getActivity()).load("https://s3.ap-south-1.amazonaws.com/edispatch/Topo/request+topo10.png").into(content);
            content.setImageResource(R.drawable.viewpager_requesttopo);
            text.setText(getActivity().getString(R.string.request));
        }else if(pos==2)
        {
          //  Picasso.with(getActivity()).load("https://s3.ap-south-1.amazonaws.com/edispatch/Topo/instant.png").into(content);
            content.setImageResource(R.drawable.pager_instanttopo);
            text.setText(getActivity().getString(R.string.instatnt));
        }
        MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getText().toString().equals(getString(R.string.addtopo)))
                {
                    Intent i = new Intent(getActivity(), MapActivity.class);
                    startActivity(i);
                }else if(text.getText().toString().equals(getString(R.string.request)))
                {
                    Intent i = new Intent(getActivity(), SendRequestTopoActivity.class);
                    startActivity(i);
                }else if(text.getText().toString().equals(getString(R.string.instatnt)))
                {
                    Intent i = new Intent(getActivity(), InstatntTopoActivity.class);
                    startActivity(i);
                }
            }
        });

       /* text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getText().toString().equals(getString(R.string.addtopo)))
                {
                    Intent i = new Intent(getActivity(), MapActivity.class);
                    startActivity(i);
                }else if(text.getText().toString().equals(getString(R.string.request)))
                {
                    Intent i = new Intent(getActivity(), SendRequestTopoActivity.class);
                    startActivity(i);
                }else if(text.getText().toString().equals(getString(R.string.instatnt)))
                {
                    Intent i = new Intent(getActivity(), InstatntTopoActivity.class);
                    startActivity(i);
                }

            }
        });*/
        return l;
    }

    ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            // Or read size directly from the view's width/height
            int size = 150;
            outline.setOval(0, 0, size, size);
        }
    };

}
