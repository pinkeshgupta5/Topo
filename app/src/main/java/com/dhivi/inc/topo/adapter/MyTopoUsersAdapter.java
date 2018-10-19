package com.dhivi.inc.topo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.holder.FetchContacts;
import com.dhivi.inc.topo.plugin.holder.TopoRequests;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.dhivi.inc.topo.utils.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Manoj on 11/2/2017.
 */

public class MyTopoUsersAdapter extends RecyclerView.Adapter<MyTopoUsersAdapter.ViewHolder> {

    OnItemSelectedListener notifier;
    ArrayList<FetchContacts> fetchContacts;

    public MyTopoUsersAdapter(OnItemSelectedListener notifier, ArrayList<FetchContacts> fetchContacts) {
        this.notifier = notifier;
        this.fetchContacts = fetchContacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.mytopo_user_listitem, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindContent(position);
    }

    @Override
    public int getItemCount() {
        return fetchContacts.size();
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
        @BindView(R.id.selected)
        CheckBox selected;
        Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        public void bindContent(final int position) {
            final FetchContacts contacts = fetchContacts.get(position);

            if (contacts != null) {

                title_textview.setText(contacts.getTopo_user_name());
                desc_textview.setText(contacts.getTopo_user_mobile());
                if (contacts.getTopo_image_url() != null && !contacts.getTopo_image_url().equals("")) {
                    Picasso.with(context).load(context.getString(R.string.profile_image) + contacts.getTopo_image_url()).error(R.drawable.defalut_profile).transform(new CircleTransform()).into(list_imageview);
                } else {
                    Picasso.with(context).load(R.drawable.defalut_profile).into(list_imageview);
                }

            }


            selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        notifier.onItemSelected(contacts.getTopo_user_id());
                        selected.setChecked(true);
                    } else {
                        notifier.onItemRemoved(contacts.getTopo_user_id());
                        selected.setChecked(false);
                    }
                }
            });

            total_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        selected.setChecked(!selected.isChecked());
                    }
                }
            });

           /* selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selected.isChecked()) {
                        notifier.onItemSelected(contacts.getTopo_user_id());
                        selected.setChecked(false);
                        } else {
                        selected.setChecked(true);
                        notifier.onItemRemoved(contacts.getTopo_user_id());
                        }
                    }

                 });*/
            if (position == (fetchContacts.size() - 1)) {
                view.setVisibility(View.GONE);
            }
        }

    }

    public interface OnItemSelectedListener {
        void onItemSelected(String name);

        void onItemRemoved(String name);
    }
}
