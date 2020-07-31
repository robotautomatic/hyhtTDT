package com.hyht.tdt.utils;

import com.hyht.tdt.entity.EntEntity;
import com.tianditu.android.maps.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class EntityToGeoPointUtil {
    public List<GeoPoint> getGeoPointList(EntEntity entEntity){
        String addition = entEntity.getEntAddition();
        List<String> additionList = new ArrayList<>();
        String[] strArr = addition.split("/");
        for (String s : strArr
        ) {
            additionList.add(s);
        }
        List<GeoPoint> points = new ArrayList<>();
        for (String s : additionList
        ) {
            String[] pointAddition = s.split(",");
            GeoPoint point = new GeoPoint(Integer.valueOf(pointAddition[1]).intValue(),Integer.valueOf(pointAddition[0]).intValue());
            points.add(point);
        }
        return points;
    }
}
