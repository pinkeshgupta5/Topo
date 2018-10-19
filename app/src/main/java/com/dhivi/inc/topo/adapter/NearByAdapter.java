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
import com.dhivi.inc.topo.plugin.holder.NearByData;
import com.dhivi.inc.topo.plugin.holder.Topos;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 12/5/2017.
 */

public class NearByAdapter extends RecyclerView.Adapter<NearByAdapter.ViewHolder> {

    OnItemSelectedListener notifier;
    ArrayList<NearByData> nearByData;

    public NearByAdapter(OnItemSelectedListener notifier, ArrayList<NearByData> nearByData) {
        this.notifier = notifier;
        this.nearByData = nearByData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.nearby_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindContent(position);
    }

    @Override
    public int getItemCount() {
        return nearByData.size();
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
        @BindView(R.id.distance_textview)
        TextView distance_textview;
        @BindView(R.id.view)
        View view;

        Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        public void bindContent(final int position) {

            final NearByData data = nearByData.get(position);

            if (data != null) {
                title_textview.setText(data.getName());
                desc_textview.setText(data.getType());
                distance_textview.setText(data.getDistance());
                Picasso.with(context).load(data.getIcon()).into(list_imageview);
            }
            total_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (data != null)
                        notifier.onItemSelected(data);
                }
            });

            if (position == (nearByData.size() - 1)) {
                view.setVisibility(View.GONE);
            }
        }

    }

    public interface OnItemSelectedListener {
        void onItemSelected(NearByData data);
    }
}

