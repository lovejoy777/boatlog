package com.lovejoy777.boatlog.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovejoy777.boatlog.R;
import com.lovejoy777.boatlog.activities.WeatherMainActivity;

public class RecyclerViewFragment extends Fragment {

    final String TAG = "fragment ";

    public RecyclerViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            Bundle bundle = this.getArguments();
            View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            WeatherMainActivity weatherMainActivity = (WeatherMainActivity) getActivity();
            recyclerView.setAdapter(weatherMainActivity.getAdapter(bundle.getInt("day")));
            return view;
        }catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }
    }

}
