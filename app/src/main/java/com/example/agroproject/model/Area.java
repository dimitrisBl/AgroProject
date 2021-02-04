package com.example.agroproject.model;

import android.content.Context;

import com.google.android.gms.maps.model.PolygonOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Area {

    private String areaName;
    private String description;
    private Context context;
    private Map<String, PolygonOptions> monitoringAreasMap = new HashMap<>();

    private MonitoringAreas monitoringAreas;

    public Area(Context context){
        this.context = context;
        monitoringAreas = new MonitoringAreas(context);
    }

    public void createArea(String areaName, String description, PolygonOptions polygonOptions){
        this.areaName = areaName;
        this.description = description;
        monitoringAreasMap.put(areaName, polygonOptions);
    }

    public String getAreaName() {
        return areaName;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, PolygonOptions> getMonitoringAreas() {
        return monitoringAreasMap;
    }


    public void saveArea(){
           for(Map.Entry<String, PolygonOptions> entry : monitoringAreasMap.entrySet()){
               monitoringAreas.addPolygonArea(monitoringAreasMap.get(entry));
           }
        monitoringAreas.saveArea();
    }

    public List<PolygonOptions> getSavedArea(){
        return monitoringAreas.getSavedArea();
    }
}
