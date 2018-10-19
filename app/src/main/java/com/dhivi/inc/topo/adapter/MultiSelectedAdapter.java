package com.dhivi.inc.topo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.holder.FetchContacts;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 12/7/2017.
 */

public class MultiSelectedAdapter extends RecyclerView.Adapter<MultiSelectedAdapter.ViewHolder> {

    OnItemSelectedListener notifier;
    String[] workingsdays;
    ArrayList<String> dayslist;

    public MultiSelectedAdapter(OnItemSelectedListener notifier, String[] workingsdays, ArrayList<String> dayslist) {
        this.notifier = notifier;
        this.workingsdays = workingsdays;
        this.dayslist = dayslist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.multiselection_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindContent(position);
    }

    @Override
    public int getItemCount() {
        return workingsdays.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.total_layout)
        RelativeLayout total_layout;
        @BindView(R.id.title_textview)
        TextView title_textview;
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

            title_textview.setText(workingsdays[position]);

            if (dayslist != null && dayslist.size() > 0) {
                if (dayslist.contains(workingsdays[position])) {
                    selected.setChecked(true);
                } else {
                    selected.setChecked(false);
                }
            }

            selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        notifier.onItemSelected(workingsdays[position]);
                        selected.setChecked(true);
                    } else {
                        notifier.onItemRemoved(workingsdays[position]);
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

            if (position == (workingsdays.length - 1)) {
                view.setVisibility(View.GONE);
            }
        }

    }

    public interface OnItemSelectedListener {
        void onItemSelected(String name);

        void onItemRemoved(String name);
    }
}

