package com.lovejoy777.boatlog.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lovejoy777.boatlog.R;
import com.lovejoy777.boatlog.activities.Apps;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{

    List<Apps> mItems;

    public CardAdapter() {
        super();
        mItems = new ArrayList<Apps>();
        Apps apps = new Apps();
        apps.setName("LogBook");
        apps.setThumbnail(R.drawable.book);
        mItems.add(apps);

        apps = new Apps();
        apps.setName("Log");
        apps.setThumbnail(R.drawable.log);
        mItems.add(apps);

        apps = new Apps();
        apps.setName("Nav Aids");
        apps.setThumbnail(R.drawable.navaids);
        mItems.add(apps);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_card_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Apps apps = mItems.get(position);
        viewHolder.tvApps.setText(apps.getName());
        viewHolder.imgThumbnail.setImageResource(apps.getThumbnail());

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumbnail;
        public TextView tvApps;
        public RelativeLayout RL1;
        public CardView cardview1;


        public ViewHolder(View itemView) {
            super(itemView);

            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvApps = (TextView) itemView.findViewById(R.id.tv_apps);
            RL1 = (RelativeLayout) itemView.findViewById(R.id.RL1);
            RL1.setBackgroundColor(Color.BLACK);
            cardview1.setBackgroundColor(Color.BLACK);
        }
    }

}