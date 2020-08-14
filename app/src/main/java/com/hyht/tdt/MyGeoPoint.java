package com.hyht.tdt;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.tianditu.android.maps.*;
import com.tianditu.maps.Overlay.ItemsOverlayList;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUIPopup;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MyGeoPoint extends ItemizedOverlay {

    private Context mContext;
    private List<OverlayItem> geoList = new ArrayList<>();
    private boolean on;

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
   /* @Override
    public boolean onTap(GeoPoint geoPoint, MapView mapView) {
        System.out.println("geoPoint111 = "+ geoPoint);
        Projection pro = mapView.getProjection();
        Point point = pro.toPixels(geoPoint, (Point)null);
        System.out.println("Point222 = "+ point);
        int oldID = super.getFocusID();
        getFocusID();
        int focusID = super.getFocusID();
        System.out.println("Id = "+ super.getFocusID());
        getLastFocusedIndex();
        System.out.println("getLastFocusedIndex = "+ super.getLastFocusedIndex());
        getFocus();
        System.out.println("getFocus = "+ super.getFocus());*//*
        getCenter();
        System.out.println("getCenter = "+ getCenter());*//*

        this.onTap(focusID);

        if (oldID != focusID) {
            mapView.invalidate();
        }
*//*        int oldID = this.mList.getFocusID();
        boolean b = this.mList.onTap(point, mapView);
        int focusID = this.mList.getFocusID();
        this.onTap(focusID);
        if (oldID != focusID) {
            mapView.invalidate();
        }*//*

        return false;
    }*/
    @Override
    protected boolean onTap(int i) {
        System.out.println("on = " + on);
        System.out.println("i = " + i);
        if (i != -1) {
/*            Toast.makeText(mContext,
                    geoList.get(i).getSnippet(), Toast.LENGTH_SHORT).show();*/

            System.out.println(" /   getFocusID = " + super.getFocusID());
            System.out.println(" //   getLastFocusedIndex = " + super.getLastFocusedIndex());
            System.out.println(" ///   getFocus = " + super.getFocus());
            System.out.println(" ////   getCenter = " + super.getCenter());
        }

        return true;
    }

    @Override
    public boolean onTap(GeoPoint geoPoint, MapView mapView) {
        on = super.onTap(geoPoint, mapView);

        if(on == true){
/*            MaterialDialog materialDialog = new MaterialDialog.Builder(mContext)
                    .customView(R.layout.dialog_custom_point, true)
                    .iconRes(R.drawable.ic_save)
                    .title("保存单个点信息")
                    .positiveText("确认")
                    .negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    }).show();*/
/*            BottomSheet.BottomListSheetBuilder builder = new BottomSheet.BottomListSheetBuilder(mContext);
            builder.addItem("删除这个点")
                    .setIsCenter(true)
                    .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(BottomSheet dialog, View itemView, int position, String tag) {
                            dialog.dismiss();
                            XToast.normal(mContext,"删除成功").show();
                        }
                    })
                    .build().show();*/

        }
        return on;
    }
}
