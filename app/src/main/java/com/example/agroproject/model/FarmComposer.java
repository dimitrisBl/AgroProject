package com.example.agroproject.model;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FarmComposer {

    private static final String TAG = "FarmComposer";

    private static Map<FarmArea, List<InnerFarmArea>> farmMap = new HashMap<>();



    public static void fillMap(List<FarmArea> farmAreaList, List<InnerFarmArea> innerFarmAreaList){
        List<InnerFarmArea> innerFarmList = new ArrayList<>();

        for(FarmArea farmArea : farmAreaList){
            for(InnerFarmArea innerFarmArea : innerFarmAreaList){
                if(innerFarmArea.getFarmArea().getName().equals(farmArea.getName())){
                    innerFarmList.add(innerFarmArea);
                }
            }
            farmMap.put(farmArea, innerFarmAreaList);
        }
    }


    public static Map<FarmArea, List<InnerFarmArea>> getFarmMap() {
        return farmMap;
    }
}
