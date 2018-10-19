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
import com.dhivi.inc.topo.plugin.holder.Topos;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 11/5/2017.
 */

public class ArrivingToposAdapter extends RecyclerView.Adapter<ArrivingToposAdapter.ViewHolder> {

    OnItemSelectedListener notifier;
    ArrayList<ArrivingTopos> arrivingTopos;

    String firsturl = "https://maps.googleapis.com/maps/api/staticmap?&markers=icon:https://s3.ap-south-1.amazonaws.com/edispatch/Topo/Assets/img/companyImg/m_v5.png%7C";
    String secondurl = "&key=AIzaSyAvIsgCbwpTfO4ad3F6taz3kQydYeGv5mY&zoom=14&size=100x100&format=png&maptype=roadmap&style=feature:administrative%7Celement:geometry.fill%7Ccolor:0xd6e2e6&style=feature:administrative%7Celement:geometry.stroke%7Ccolor:0xcfd4d5&style=feature:administrative%7Celement:labels.text%7Cvisibility:off&style=feature:administrative%7Celement:labels.text.fill%7Ccolor:0x7492a8%7Cvisibility:off&style=feature:administrative.neighborhood%7Celement:labels.text.fill%7Clightness:25&style=feature:landscape%7Celement:geometry.fill%7Cvisibility:on&style=feature:landscape%7Celement:labels.text%7Cvisibility:off&style=feature:landscape.natural%7Celement:labels.text.fill%7Ccolor:0x7492a8&style=feature:poi%7Celement:labels%7Cvisibility:off&style=feature:poi%7Celement:labels.icon%7Csaturation:-100&style=feature:poi%7Celement:labels.text.fill%7Ccolor:0x588ca4&style=feature:poi.park%7Celement:geometry.fill%7Ccolor:0xa9de83&style=feature:poi.park%7Celement:geometry.stroke%7Ccolor:0xbae6a1&style=feature:poi.sports_complex%7Celement:geometry.fill%7Ccolor:0xc6e8b3&style=feature:poi.sports_complex%7Celement:geometry.stroke%7Ccolor:0xbae6a1&style=feature:road%7Celement:labels%7Cvisibility:off";
    String thirdurl = "&style=feature:road%7Celement:labels.icon%7Csaturation:-45%7Clightness:10%7Cvisibility:on&style=feature:road%7Celement:labels.text.fill%7Ccolor:0x41626b&style=feature:road.arterial%7Celement:geometry.fill%7Ccolor:0xffffff&style=feature:road.highway%7Celement:geometry.fill%7Ccolor:0xc1d1d6&style=feature:road.highway%7Celement:geometry.stroke%7Ccolor:0xa6b5bb&style=feature:road.highway%7Celement:labels.icon%7Cvisibility:on&style=feature:road.local%7Celement:geometry.fill%7Ccolor:0xffffff&style=feature:transit%7Celement:labels%7Cvisibility:off&style=feature:transit%7Celement:labels.icon%7Csaturation:-70&style=feature:transit.line%7Celement:geometry.fill%7Ccolor:0xb4cbd4&style=feature:transit.line%7Celement:labels.text.fill%7Ccolor:0x588ca4&style=feature:transit.station%7Celement:labels.text.fill%7Ccolor:0x008cb5%7Cvisibility:on&style=feature:transit.station.airport%7Celement:geometry.fill%7Csaturation:-100%7Clightness:-5&style=feature:water%7Celement:geometry.fill%7Ccolor:0xa6cbe3&style=feature:water%7Celement:labels%7Cvisibility:off";


    public ArrivingToposAdapter(OnItemSelectedListener notifier, ArrayList<ArrivingTopos> arrivingTopos) {
        this.notifier = notifier;
        this.arrivingTopos = arrivingTopos;
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
        return arrivingTopos.size();
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

            final ArrivingTopos topos = arrivingTopos.get(position);

            if (topos != null) {
                horizontal_textview.setText(topos.getTopo_name());
                title_textview.setText(topos.getTopo_name());
                desc_textview.setText(topos.getTopo_address());
               // String src = firsturl + topos.getTopo_lat() + "," + topos.getTopo_lon() + secondurl + thirdurl;
                String src = String.format(context.getString(R.string.mapurl), topos.getTopo_user_id(),topos.getTopo_img_url());
                Picasso.with(context).load(src).transform(new CircleTransform()).into(list_imageview);
                Picasso.with(context).load(src).into(horizontal_imageview);

            }
            total_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (topos != null)
                        notifier.onItemSelected(topos);
                }
            });

            if (position == (arrivingTopos.size() - 1)) {
                view.setVisibility(View.GONE);
            }
        }

    }

    public interface OnItemSelectedListener {
        void onItemSelected(ArrivingTopos topo);
    }
}
