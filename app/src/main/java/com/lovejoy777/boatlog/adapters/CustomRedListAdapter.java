package com.lovejoy777.boatlog.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovejoy777.boatlog.R;

/**
 * Created by lovejoy777 on 12/10/15.
 */
public class CustomRedListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    private final String[] subtext;

    public CustomRedListAdapter(Activity context,
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
        View rowView = inflater.inflate(R.layout.adapter_listview_about, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        TextView txtSub = (TextView) rowView.findViewById(R.id.description);
        txtTitle.setText(web[position]);
        txtTitle.setTextColor(context.getResources().getColor(R.color.night_text));
        txtSub.setText((subtext[position]));
        txtSub.setTextColor(context.getResources().getColor(R.color.night_text));
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}