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
import com.dhivi.inc.topo.plugin.holder.CabsData;
import com.dhivi.inc.topo.plugin.holder.Topos;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 12/5/2017.
 */

public class CabsAdapter extends RecyclerView.Adapter<CabsAdapter.ViewHolder> {

    OnItemSelectedListener notifier;
    ArrayList<CabsData> cabsData;


    public CabsAdapter(OnItemSelectedListener notifier, ArrayList<CabsData> cabsData) {
        this.notifier = notifier;
        this.cabsData = cabsData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cab_items, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindContent(position);
    }

    @Override
    public int getItemCount() {
        return cabsData.size();
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

            final CabsData data = cabsData.get(position);

            if (data != null) {
                horizontal_textview.setText(data.getTopo_cab_provider());
                Picasso.with(context).load(data.getTopo_cab_logo_url()).into(horizontal_imageview);

            }
            total_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (data != null)
                        notifier.onItemSelected(data);
                }
            });


        }

    }

    public interface OnItemSelectedListener {
        void onItemSelected(CabsData cabsData);
    }
}

