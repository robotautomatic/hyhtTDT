package com.hyht.tdt;

import com.hyht.tdt.entity.EntEntity;
import com.hyht.tdt.utils.EntityToGeoPointUtil;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MapViewRender;
import com.tianditu.android.maps.Overlay;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.List;

public class MyOverlayShow extends Overlay {
    List<EntEntity> overlayList;

    public MyOverlayShow(List<EntEntity> overlayList) {
        this.overlayList = overlayList;
    }

    @Override
    public void draw(GL10 gl, MapView mapView, boolean shadow) {
        if (shadow)
            return;
        EntityToGeoPointUtil entityToGeoPointUtil = new EntityToGeoPointUtil();

        for (EntEntity entEntity : overlayList
             ) {
            List<GeoPoint> points = entityToGeoPointUtil.getGeoPointList(entEntity);
            MapViewRender render = mapView.getMapViewRender();
            switch (entEntity.getEntType()) {
                case 0: {
                }
                break;
                case 1: {
                    render.drawPolyLine(gl, new DefaultDrawOption().getLineOption(), points);
                }
                break;
                case 2: {
                    render.drawPolygon(gl, new DefaultDrawOption().getPlaneOption(), (ArrayList<GeoPoint>) points);
                }
                break;
            }
        }


    }
}
