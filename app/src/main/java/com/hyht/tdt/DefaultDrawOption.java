package com.hyht.tdt;

import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.tianditu.android.maps.renderoption.DrawableOption;
import com.tianditu.android.maps.renderoption.LineOption;
import com.tianditu.android.maps.renderoption.PlaneOption;

public class DefaultDrawOption {

    private DrawableOption mOption;
    private LineOption lineOption;
    private PlaneOption planeOption;

    public DefaultDrawOption() {
        mOption = new DrawableOption();
        lineOption = new LineOption();
        planeOption = new PlaneOption();



    }

    public DrawableOption getmOption() {
        mOption.setAnchor(0.5f, 1.0f);
        return mOption;
    }

    public LineOption getLineOption() {
        lineOption.setStrokeWidth(5);
        lineOption.setDottedLine(false);
        lineOption.setStrokeColor(0xAA000000);
        return lineOption;
    }

    public PlaneOption getPlaneOption() {
        planeOption.setStrokeWidth(5);
        planeOption.setFillColor(0xAAFF0000);
        planeOption.setStrokeColor(0xAA000000);
        return planeOption;
    }
}
