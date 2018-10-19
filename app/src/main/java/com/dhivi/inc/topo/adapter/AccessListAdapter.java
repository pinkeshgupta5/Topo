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
import com.dhivi.inc.topo.plugin.holder.AccessList;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 11/12/2017.
 */

public class AccessListAdapter extends RecyclerView.Adapter<AccessListAdapter.ViewHolder> {

    OnItemSelectedListener notifier;
    ArrayList<AccessList> accessLists;

    public AccessListAdapter(OnItemSelectedListener notifier, ArrayList<AccessList> accessLists) {
        this.notifier = notifier;
        this.accessLists = accessLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.access_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindContent(position);
    }

    @Override
    public int getItemCount() {
        return accessLists.size();
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
        @BindView(R.id.desc_textview1)
        TextView desc_textview1;
        @BindView(R.id.edit_layout)
        RelativeLayout edit_layout;
        @BindView(R.id.delete_layout)
        RelativeLayout delete_layout;
        Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        public void bindContent(final int position) {

            final AccessList topos = accessLists.get(position);

            if (topos != null) {
                title_textview.setText(topos.getTopo_user_name());
                desc_textview.setText(topos.getTopo_access_period());
                if (topos.getTopo_image_url() != null && !topos.getTopo_image_url().equals("")) {
                    Picasso.with(context).load(context.getString(R.string.profile_image) + topos.getTopo_image_url()).error(R.drawable.defalut_profile).transform(new CircleTransform()).into(list_imageview);
                } else {
                    Picasso.with(context).load(R.drawable.defalut_profile).into(list_imageview);
                }


            }
            delete_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (topos != null)
                        notifier.onItemDelete(topos);
                }
            });
            edit_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (topos != null)
                        notifier.onItemUpdate(topos);
                }
            });

            if (position == (accessLists.size() - 1)) {
                view.setVisibility(View.GONE);
            }
        }

    }

    public interface OnItemSelectedListener {
        void onItemDelete(AccessList topo);
        void onItemUpdate(AccessList topo);
    }
}

