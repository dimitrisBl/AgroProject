package com.example.agroproject.view.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.agroproject.R;
import com.example.agroproject.model.file.KmlFile;

import java.io.File;
import java.util.List;


public class FarmListViewAdapter extends BaseAdapter {

    private List<KmlFile> kmlFileList;
  
    /**
     * Instantiate a new ListViewAdapter
     *
     * @param kmlFileList have the KmlFile objects
     */
    public FarmListViewAdapter(List<KmlFile> kmlFileList) {
        this.kmlFileList = kmlFileList;
    }

    @Override
    public int getCount() {
        return kmlFileList.size();
    }


    @Override
    public KmlFile getItem(int position) {
        return kmlFileList.get(position);
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
        title.setText(kmlFileList.get(position).getName());

        ImageView myImage =  viewResult.findViewById(R.id.image);
        //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
       // myImage.setImageResource(R.drawable.map);
//        String imageUrl =  "drawable://" + R.drawable.map;
//        File imgFile = new  File(imageUrl);
//
//        if(imgFile.exists()){
//
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//            ImageView myImage = (ImageView) viewResult.findViewById(R.id.image);
//
//            myImage.setImageBitmap(myBitmap);
//
//        }

        TextView description = viewResult.findViewById(R.id.description);
        description.setText("Farm: "+kmlFileList.get(position).getFarmName());



//        TextView description = viewResult.findViewById(R.id.description);
//        description.setText(placemarkList.get(position).getDescription());
        return viewResult;
    }

    public String getURLForResource (int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }
}
