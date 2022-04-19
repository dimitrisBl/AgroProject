package com.example.agroproject.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agroproject.R;
import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyStopsAdapter {
    //extends RecyclerView.Adapter<NearbyStopsAdapter.ViewHolder>{

//    private Context context;
//    private List<KmlFile> kmlFileList;
//
//    /** KmlLocalStorageProvider */
//    private KmlLocalStorageProvider kmlLocalStorageProvider;
//
//    /** kmlFile Map */
//    private Map<KmlFile, List<Placemark>> kmlFileMap = new HashMap<>();
//
//    public NearbyStopsAdapter(List<KmlFile> kmlFileList,Context context, Map<KmlFile,List<Placemark>> kmlFileMap){
//        this.kmlFileList = kmlFileList;
//        this.context = context;
//        // Instantiate a KmlLocalStorageProvider object
//        //kmlLocalStorageProvider = new KmlLocalStorageProvider(context);
//        //  Load the kmlFile Map from shared preferences storage
//        //kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
//        this.kmlFileMap = kmlFileMap;
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//     final View viewResult = LayoutInflater.from(parent.getContext()).
//                inflate(R.layout.activity_list_view_row, parent, false);
//
//        return new ViewHolder(viewResult,context);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
//        KmlFile kmlFile = kmlFileList.get(position);
//        List<Placemark> placemarks = kmlFileMap.get(kmlFile);
//
//        holder.setData(kmlFile,placemarks);
//    }
//
//    @Override
//    public int getItemCount() {
//        return kmlFileList.size();
//    }
//
//    //Recycling GoogleMap for list item
//    @Override
//    public void onViewRecycled(ViewHolder holder)
//    {
//        // Cleanup MapView here?
//        if (holder.gMap != null)
//        {
//            holder.gMap.clear();
//            holder.gMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//        }
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
//
//        private GoogleMap gMap;
//        private Context context;
//
//        private TextView description;
//        private TextView name;
//
//        private List<Placemark> placemarks;
//
//        private MapView mapView;
//        //.....
//
//        public ViewHolder(View view,Context context) {
//            super(view);
//            this.context = context;
//
//            mapView =  (MapView) view.findViewById(R.id.mapView);
//            description = view.findViewById(R.id.description);
//            name = view.findViewById(R.id.title);
//
//
//
//            if (mapView != null)
//            {
//                mapView.onCreate(null);
//                mapView.onResume();
//                mapView.getMapAsync(this);
//            }
//
//        }
//
//        public void setData(KmlFile kmlFile, List<Placemark> placemarks){
//
//            name.setText(kmlFile.getName());
//            description.setText(kmlFile.getFarmName());
//
//            this.placemarks = placemarks;
//
//            this.placemarks = placemarks;
//        }
//
//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//            //initialize the Google Maps Android API if features need to be used before obtaining a map
//            MapsInitializer.initialize(context);
//            gMap = googleMap;
//            // Setup satellite map
//            gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//
//            Placemark firstPlacemark = placemarks.get(0);
//            // Get center location for this Placemark
//            LatLng center = AreaUtilities.getAreaCenterPoint(firstPlacemark.getLatLngList());
//            // Create the LatLng object of the current location
//            LatLng currentLocation = new LatLng(center.latitude, center.longitude);
//            // Move the camera in current location
//
//
//
//            // Create PolygonOptions object for each placemark
//            PolygonOptions polygonOptions = new PolygonOptions()
//                    .strokeWidth(5f).addAll(firstPlacemark.getLatLngList()).strokeColor(Color.RED).clickable(true);
//            // Add polygon in the map
//            gMap.addPolygon(polygonOptions);
//            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20f));
//
////            for (Placemark placemark : placemarks) {
////                // Create PolygonOptions object for each placemark
////                PolygonOptions polygonOptions = new PolygonOptions()
////                        .strokeWidth(5f).addAll(placemark.getLatLngList()).strokeColor(Color.RED).clickable(false);
////                // Add polygon in the map
////                gMap.addPolygon(polygonOptions);
////            }
//
//            //you can move map here to item specific 'location'
//            //int pos = getPosition();
//            //get 'location' by 'pos' from data list
//            //then move to 'location'
//            //gMap.moveCamera(...);
//        }
//
//    }
}
