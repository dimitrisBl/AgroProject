package com.example.agroproject.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.agroproject.R;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.file.KmlFile;

import java.util.ArrayList;
import java.util.List;

public class DetailsListViewAdapter extends BaseAdapter {

    private List<Placemark> placemarks;

    public  DetailsListViewAdapter(List<Placemark> placemarks){
        this.placemarks = placemarks;
    }

    @Override
    public int getCount() {
        return placemarks.size();
    }

    @Override
    public Placemark getItem(int i) {
        return placemarks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View viewResult;

        if(convertView == null){
            viewResult = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.details_list_view_row, parent, false);
        }else{
            viewResult = convertView;
        }
        // UI COMPONENTS
        TextView title = viewResult.findViewById(R.id.title);
        title.setText(placemarks.get(position).getName());


//        TextView description = viewResult.findViewById(R.id.description);
//        description.setText(placemarkList.get(position).getDescription());
        return viewResult;
    }


}
