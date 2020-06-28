package com.hyht.tdt;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.tianditu.android.maps.*;
import com.tianditu.maps.Overlay.ItemsOverlayList;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

public class MyGeoPoint extends ItemizedOverlay {

    private Context mContext;
    private List<OverlayItem> geoList = new
            ArrayList<OverlayItem>();


    private ItemsOverlayList mList = null;

    public MyGeoPoint(Drawable defaultMarker) {
        super(defaultMarker);
    }

    public MyGeoPoint(Drawable marker, Context context, List<GeoPoint> points) {
        super(boundCenterBottom(marker));
        this.mContext = context;
        for (int i = 0; i < points.size(); i++) {
            OverlayItem item = new OverlayItem(points.get(i), "P" + i, "point" + i);
            item.setMarker(marker);
            geoList.add(item);
        }
//一旦有了数据，在调用其他方法前，必须首先调用这个方法
        populate();
    }
    @Override
    protected OverlayItem createItem(int i) {
// 创建指定的条目，由父类调用
        return geoList.get(i);
    }
    @Override
    public int size() {
        return geoList.size();
    }

    /*
     * 在某个条目被点击时调用
     */
    @Override
    public boolean onTap(GeoPoint geoPoint, MapView mapView) {
        System.out.println("geoPoint = "+ geoPoint);
        Projection pro = mapView.getProjection();
        Point point = pro.toPixels(geoPoint, (Point)null);
        System.out.println("geoPoint = "+ point);
        getFocusID();
        System.out.println("Id = "+ getFocusID());
        getLastFocusedIndex();
        System.out.println("getLastFocusedIndex = "+ getLastFocusedIndex());
        getFocus();
        System.out.println("getFocus = "+ getFocus());/*
        getCenter();
        System.out.println("getCenter = "+ getCenter());*/


/*        int oldID = this.mList.getFocusID();
        boolean b = this.mList.onTap(point, mapView);
        int focusID = this.mList.getFocusID();
        this.onTap(focusID);
        if (oldID != focusID) {
            mapView.invalidate();
        }*/

        return true;
    }

}
/*
    MaterialDialog materialDialog = new MaterialDialog.Builder(mContext)
            .customView(R.layout.dialog_custom_point, true)
            .iconRes(R.drawable.ic_save)
            .title("保存单个点信息")
            .positiveText("确认")
            .negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            }).show();*/
