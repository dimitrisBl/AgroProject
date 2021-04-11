package com.example.agroproject.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.example.agroproject.R;
import com.example.agroproject.model.file.KmlFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ListViewAdapter extends BaseAdapter {

    private List<String> placemarkList;

    /**
     * Instantiate a new RecyclerViewAdapter
     *
     * @param placemarkList
     */
    public ListViewAdapter(List<String> placemarkList) {
        this.placemarkList = placemarkList;
    }


    @Override
    public int getCount() {
        return placemarkList.size();
    }

    @Override
    public Object getItem(int position) {
        return placemarkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // nothing
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View viewResult;

        if(convertView == null){
            viewResult = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.activity_list_view_row, parent, false);
        }else{
            viewResult = convertView;
        }
        // UI COMPONENTS
        TextView title = viewResult.findViewById(R.id.title);
        title.setText(placemarkList.get(position));
        TextView description = viewResult.findViewById(R.id.description);
        //description.setText(placemarkList.get(position).getDescription());


        return viewResult;
    }
}
