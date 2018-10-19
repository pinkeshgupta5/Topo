package com.dhivi.inc.topo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.holder.TopoRequests;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.dhivi.inc.topo.utils.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Manoj on 10/30/2017.
 */

public class TopoRequestAdapter extends RecyclerView.Adapter<TopoRequestAdapter.ViewHolder>  {

    OnItemSelectedListener notifier;
    ArrayList<TopoRequests>requests;
    public TopoRequestAdapter(OnItemSelectedListener notifier, ArrayList<TopoRequests> requests) {
        this.notifier = notifier;
        this.requests = requests;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.searchresult_items, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindContent(position);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.total_layout)
        RelativeLayout total_layout;
        @BindView(R.id.list_imageview)
        ImageView list_imageview;
        @BindView(R.id.title_textview)
        TextView title_textview;
        @BindView(R.id.desc_textview)
        TextView desc_textview;
        @BindView(R.id.view)
        View view;
        @BindView(R.id.horizontal_imageview)
        ImageView horizontal_imageview;
        @BindView(R.id.horizontal_textview)
        TextView horizontal_textview;
        Context context;
        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        public void bindContent(final int position) {
           final TopoRequests topoRequests = requests.get(position);

            if(topoRequests!=null)
            {
                horizontal_textview.setText(topoRequests.getTopo_username());
                title_textview.setText(topoRequests.getTopo_username());
                desc_textview.setText(topoRequests.getTopo_request_message());
                if(topoRequests.getTopo_image_url()!=null&&!topoRequests.getTopo_image_url().equals(""))
                {
                    Picasso.with(context).load(context.getString(R.string.profile_image)+topoRequests.getTopo_image_url()).error(R.drawable.defalut_profile).into(horizontal_imageview);
                }else
                {
                    Picasso.with(context).load(R.drawable.noprofile).into(horizontal_imageview);
                }

            }
            total_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(topoRequests!=null) {
                        notifier.onItemSelected(topoRequests);
                    }
                }
            });

            if(position==(requests.size()-1))
            {
                view.setVisibility(View.GONE);
            }
        }

    }

    public interface OnItemSelectedListener {
        void onItemSelected(TopoRequests topoRequests);
    }
}

