package com.lovejoy777.boatlog.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovejoy777.boatlog.R;

/**
 * Created by lovejoy777 on 14/11/13.
 */

public class CustomListAdapter extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    private final String[] subtext;
    public CustomListAdapter(Activity context,
                             String[] web, String[] subtext, Integer[] imageId) {
        super(context, R.layout.adapter_listview_about, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.subtext = subtext;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.adapter_listview_about, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        TextView txtSub = (TextView) rowView.findViewById(R.id.description);
        txtTitle.setText(web[position]);
        //txtTitle.setTextColor(Color.RED);
        txtSub.setText((subtext[position]));
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}