package com.example.agroproject.model.area;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.List;


public class AreaService {

    static ListMultimap<FarmArea, InnerArea> multimap = ArrayListMultimap.create();

    static List<Area> areas = new ArrayList<>();

    public static void createArea(Area area){
        areas.add(area);
    }

    public static List<Area> getAreas() {
        return areas;
    }

    public void put(FarmArea farmArea, InnerArea innerArea){
          multimap.put(farmArea, innerArea);
    }

    public static ListMultimap<FarmArea, InnerArea> getMultimap() {
        return multimap;
    }


    




    //    /** List with MonitoringArea objects */
//    private List<IArea> areaList = new ArrayList<>();
//
//    /**
//     * This method create a monitoring area and adds this area in the
//     * list monitoringAreaList.
//     * @param area is a MonitoringArea object, this object
//     * have info's about monitoring area.
//     */
//    public void createMonitoringArea(IArea area){
//        if(area.getClass().getName().equals("FarmArea")){
//            areaList.add(new FarmArea(area.getName(), area.getDescription(), area.getPolygonOptions()));
//        }else{
//
//        }
//    }
//
//
//    public List<IArea> getAreaList() {
//        return areaList;
//    }
}
