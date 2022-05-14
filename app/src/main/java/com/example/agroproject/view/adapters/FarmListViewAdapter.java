package com.example.agroproject.view.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.agroproject.R;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.view.fragments.FarmAreaClickPopUp;

import java.util.List;


public class FarmListViewAdapter extends BaseAdapter {

    private List<KmlFile> kmlFileList;


    private DeleteKmlFileEventListener deleteKmlFileEventListener;

    /**
     * Instantiate a new ListViewAdapter
     *
     * @param kmlFileList have the KmlFile objects
     */
    public FarmListViewAdapter(List<KmlFile> kmlFileList) { this.kmlFileList = kmlFileList; }

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

        // Delete image view
        ImageView deleteImage = viewResult.findViewById(R.id.deleteBtn);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show dialog
                new AlertDialog.Builder(parent.getContext())
                        .setTitle("Are you sure?")
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                KmlFile kmlFile = getItem(position);
                                // Remove item from the list of adapter
                                kmlFileList.remove(position);
                                // Trigger the notify data changed of adapter
                                notifyDataSetChanged();
                                // Attach the DeleteKmlFileEventListener interface
                                deleteKmlFileEventListener = (DeleteKmlFileEventListener) parent.getContext();
                                // Trigger the delete kml file event
                                deleteKmlFileEventListener.deleteKmlFile(kmlFile);
                            }
                        })
                .setNegativeButton("No",null)
                .show();
            }
        });

        return viewResult;
    }


    public interface DeleteKmlFileEventListener{
        void deleteKmlFile(KmlFile kmlFile);
    }
}


