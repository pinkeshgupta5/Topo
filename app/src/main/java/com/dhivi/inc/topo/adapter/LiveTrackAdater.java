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
import com.dhivi.inc.topo.plugin.holder.ArrivingTopos;
import com.dhivi.inc.topo.plugin.holder.LiveUsers;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 11/5/2017.
 */

public class LiveTrackAdater extends RecyclerView.Adapter<LiveTrackAdater.ViewHolder>  {

    OnItemSelectedListener notifier;
    ArrayList<LiveUsers>liveUsers;
    public LiveTrackAdater(OnItemSelectedListener notifier,ArrayList<LiveUsers>liveUsers) {
        this.notifier = notifier;
        this.liveUsers = liveUsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.live_track_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindContent(position);
    }

    @Override
    public int getItemCount() {
        return liveUsers.size();
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
        Context context;
        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        public void bindContent(final int position) {

            final LiveUsers topos = liveUsers.get(position);

            if(topos!=null)
            {
                title_textview.setText(topos.getTopo_user_name());
                desc_textview.setText(topos.getTopo_user_mobile());
                Picasso.with(context).load(context.getString(R.string.profile_image)+topos.getTopo_image_url()).error(R.drawable.defalut_profile).transform(new CircleTransform()).into(list_imageview);


            }
            total_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(topos!=null) {
                        total_layout.setBackgroundColor(context.getResources().getColor(R.color.selected_color));
                        notifier.onItemSelected(topos);
                    }
                }
            });

            if(position==(liveUsers.size()-1))
            {
                view.setVisibility(View.GONE);
            }
        }

    }

    public interface OnItemSelectedListener {
        void onItemSelected(LiveUsers topo);
    }
}
