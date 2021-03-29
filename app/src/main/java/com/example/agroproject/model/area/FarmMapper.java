package com.example.agroproject.model.area;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FarmMapper {

    private  Map<FarmArea, InnerArea> farmMap = new HashMap<>();

    private FarmMapper(){}


    private static FarmMapper instance = null;


    public static FarmMapper getInstance(){
        if(instance==null){
            return  new FarmMapper();
        }
        return instance;
    }

    public  void fillTheMap(List<InnerArea> innerAreaList){
        for(InnerArea innerArea : innerAreaList){
            farmMap.put(innerArea.getFarmArea(),innerArea);
        }
    }

    public  Map<FarmArea, InnerArea> getFarmMap() {
        return farmMap;
    }
}
