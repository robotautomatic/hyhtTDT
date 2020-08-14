package com.hyht.tdt;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.hyht.tdt.entity.EntEntity;
import com.hyht.tdt.utils.EntityToGeoPointUtil;
import com.hyht.tdt.utils.SpatialRelationUtil;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MapViewRender;
import com.tianditu.android.maps.Overlay;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.List;

public class OverlayShow extends Overlay {
    private List<GeoPoint> points;
    private EntEntity entEntity;
    private List<EntEntity> entEntities = new ArrayList<>();
    private List<List<GeoPoint>> pointsList;
    private Context context;

    public OverlayShow(EntEntity entEntity, Context context) {
        this.entEntity = entEntity;
        this.context = context;
        entEntities.add(entEntity);
    }

    public OverlayShow(List<EntEntity> entEntities, Context context) {
        this.entEntities = entEntities;
        this.context = context;
    }
    
    
    @Override
    public void draw(GL10 gl, MapView mapView, boolean shadow) {
        if (shadow)
            return;
        MapViewRender render = mapView.getMapViewRender();
        for (EntEntity entEntity : entEntities
        ) {
            switch (entEntity.getEntType()) {
                case 0: {
                }
                break;
                case 1: {
                    points = new EntityToGeoPointUtil().getGeoPointList(entEntity);
                    render.drawPolyLine(gl, new DefaultDrawOption().getLineOption(), points);
                }
                break;
                case 2: {
                    points = new EntityToGeoPointUtil().getGeoPointList(entEntity);
                    render.drawPolygon(gl, new DefaultDrawOption().getPlaneOption(), (ArrayList<GeoPoint>) points);
                }
                break;
            }
        }
    }

    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        EntEntity topEnt = new EntEntity();
        for (EntEntity entEntity : entEntities
             ) {
            points = new EntityToGeoPointUtil().getGeoPointList(entEntity);
            boolean isContain = SpatialRelationUtil.isPolygonContainsPoint1(points, p);
            if (isContain == true){
                topEnt = entEntity;
            }
        }
        if(topEnt != null){
            System.out.println("选中的是:" + topEnt.getEntName());
            System.out.println("on");
            final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                    .customView(R.layout.dialog_custom_select_one, true)
                    .iconRes(R.drawable.ic_save)
                    .title("显示详细信息")
                    .positiveText("确认")
                    .negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    }).show();
            View view = materialDialog.getCustomView();
            TextView ent_name = view.findViewById(R.id.ent_name);
            ent_name.setText(topEnt.getEntName());
            TextView ent_code = view.findViewById(R.id.ent_code);
            ent_code.setText(topEnt.getEntCode());
            TextView ent_attribute = view.findViewById(R.id.ent_attribute);
            ent_attribute.setText(topEnt.getEntAttribute());
            TextView ent_address = view.findViewById(R.id.ent_address);
            ent_address.setText(topEnt.getEntAddress());
            TextView ent_owner = view.findViewById(R.id.ent_owner);
            ent_owner.setText(topEnt.getEntOwner());
            TextView ent_property = view.findViewById(R.id.ent_property);
            ent_property.setText(topEnt.getEntProperty());
            TextView ent_list = view.findViewById(R.id.ent_list);
            ent_list.setText(topEnt.getCoorList());
        }
        return  true;
    }
}
