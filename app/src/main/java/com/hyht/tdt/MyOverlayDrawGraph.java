package com.hyht.tdt;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import androidx.core.content.ContextCompat;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MapViewRender;
import com.tianditu.android.maps.Overlay;
import com.tianditu.android.maps.overlay.PolygonOverlay;
import com.tianditu.android.maps.renderoption.DrawableOption;
import com.tianditu.android.maps.renderoption.LineOption;
import com.tianditu.android.maps.renderoption.PlaneOption;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class MyOverlayDrawGraph extends Overlay {
    private Drawable mDrawable;
    private DrawableOption mOption;
    private LineOption lineOption;
    private PlaneOption planeOption;
    public int draw = 0;
    public int drawConfirm = 0;
    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();

    public int getDraw() {
        return draw;
    }

    public int getDrawConfirm() {
        return drawConfirm;
    }

    public ArrayList<GeoPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<GeoPoint> points) {
        this.points = points;
    }

    public Drawable getmDrawable() {
        return mDrawable;
    }

    public void setmDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }

    public DrawableOption getmOption() {
        return mOption;
    }

    public void setmOption(DrawableOption mOption) {
        this.mOption = mOption;
    }

    public LineOption getLineOption() {
        return lineOption;
    }

    public void setLineOption(LineOption lineOption) {
        this.lineOption = lineOption;
    }

    public PlaneOption getPlaneOption() {
        return planeOption;
    }

    public void setPlaneOption(PlaneOption planeOption) {
        this.planeOption = planeOption;
    }

    public void setDrawConfirm(int drawConfirm) {
        this.drawConfirm = drawConfirm;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    MyOverlayDrawGraph(Context context) {
        mDrawable = ContextCompat.getDrawable(context, R.mipmap.tuding);
        mOption = new DrawableOption();
        lineOption = new LineOption();
        planeOption = new PlaneOption();

        mOption.setAnchor(0.5f, 1.0f);

        lineOption.setStrokeWidth(5);
        lineOption.setDottedLine(false);
        lineOption.setStrokeColor(0xAA000000);

        planeOption.setStrokeWidth(5);
        planeOption.setFillColor(0xAAFF0000);
        planeOption.setStrokeColor(0xAA000000);

    }

    @Override
    public void draw(GL10 gl, MapView mapView, boolean shadow) {
        if (shadow)
            return;

        MapViewRender render = mapView.getMapViewRender();

        switch (draw) {
            case 0: {
                for (GeoPoint point : points
                ) {

                    render.drawDrawable(gl, mOption, mDrawable, point);
                }
            }
            break;
            case 1: {
                for (GeoPoint point : points
                ) {
                    render.drawDrawable(gl, mOption, mDrawable, point);
                }
                render.drawPolyLine(gl, lineOption, points);
            }
            break;
            case 2: {
                for (int i = 0;i < points.size(); i++
                ) {

                    GeoPoint point = points.get(i);
                    render.drawDrawable(gl, mOption, mDrawable, point);
                }
                render.drawPolygon(gl, planeOption, points);
            }
            break;
        }
    }

    @Override
    public boolean isVisible() {
        return super.isVisible();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event, MapView mapView) {
        return super.onKeyDown(keyCode, event, mapView);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event, MapView mapView) {
        return super.onKeyUp(keyCode, event, mapView);
    }

    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        if (drawConfirm == 0) {
            points.add(p);
        }
//            GeoPoint point = new GeoPoint(p.getLongitudeE6(),p.getLatitudeE6());
        System.out.println("选取点的经度 = " + p.getLatitudeE6() + "  选取点的纬度 = " + p.getLongitudeE6());
        System.out.println("draw = " + draw);
        return true;
    }

    @Override
    public boolean onLongPress(GeoPoint p, MapView mapView) {
        return super.onLongPress(p, mapView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        return super.onTouchEvent(event, mapView);
    }
}
