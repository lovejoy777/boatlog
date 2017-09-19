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
 * Created by steve on 18/09/17.
 */

public class ManLogListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public ManLogListAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.manlog_info, itemname);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.manlog_info, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.manlogName);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.manlogProgress);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        return rowView;

    }

    ;
}