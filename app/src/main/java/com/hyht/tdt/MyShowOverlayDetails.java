package com.hyht.tdt.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.hyht.tdt.R;
import com.hyht.tdt.entity.EntEntity;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.ItemizedOverlay;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.OverlayItem;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

public class MyShowOverlayDetails extends ItemizedOverlay {
    private Context mContext;
    private List<OverlayItem> geoList = new ArrayList<>();
    private EntEntity entEntity;

    public MyShowOverlayDetails(Drawable marker, Context context, EntEntity entEntity) {
        super(boundCenterBottom(marker));
        this.entEntity = entEntity;
        EntityToGeoPointUtil entityToGeoPointUtil= new EntityToGeoPointUtil();
        this.mContext = context;
        List<GeoPoint> points = entityToGeoPointUtil.getGeoPointList(entEntity);
        for (int i = 0; i < points.size(); i++) {
            OverlayItem item = new OverlayItem(points.get(i), "P" + i, "point" + i);
            item.setMarker(marker);
            geoList.add(item);
        }
        populate();
    }

    @Override
    public int size() {
        return geoList.size();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return geoList.get(i);
    }

    @Override
    public boolean onTap(GeoPoint geoPoint, MapView mapView) {
        boolean on = super.onTap(geoPoint, mapView);

        if(on == true){
            MaterialDialog materialDialog = new MaterialDialog.Builder(mContext)
                    .customView(R.layout.dialog_custom_select_one, true)
                    .iconRes(R.drawable.ic_save)
                    .title("保存单个点信息")
                    .positiveText("确认")
                    .negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    }).show();

            View view = materialDialog.getCustomView();
            TextView ent_name = view.findViewById(R.id.ent_name);
            ent_name.setText(entEntity.getEntName());
            TextView ent_code = view.findViewById(R.id.ent_code);
            ent_code.setText(entEntity.getEntCode());
            TextView ent_attribute = view.findViewById(R.id.ent_attribute);
            ent_attribute.setText(entEntity.getEntAttribute());
            TextView ent_address = view.findViewById(R.id.ent_address);
            ent_address.setText(entEntity.getEntAddress());
            TextView ent_owner = view.findViewById(R.id.ent_owner);
            ent_owner.setText(entEntity.getEntOwner());
            TextView ent_property = view.findViewById(R.id.ent_property);
            ent_property.setText(entEntity.getEntProperty());
            TextView ent_list = view.findViewById(R.id.ent_list);
            ent_list.setText(entEntity.getCoorList());
        }
        return on;
    }
}
