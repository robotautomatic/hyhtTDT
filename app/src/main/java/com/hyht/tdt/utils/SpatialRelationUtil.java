package com.hyht.tdt.utils;

import com.tianditu.android.maps.GeoPoint;

import java.util.List;

public class SpatialRelationUtil {
    /**
     * 返回一个点是否在一个多边形区域内
     *
     * @param mPoints 多边形坐标点列表
     * @param point   待判断点
     * @return true 多边形包含这个点,false 多边形未包含这个点。
     */
    public static boolean isPolygonContainsPoint1(List<GeoPoint> mPoints, GeoPoint point) {
            return isPolygonContainsPoint(mPoints, point);
    }

    /**
     * 返回一个点是否在一个多边形区域内
     *
     * @param mPoints 多边形坐标点列表
     * @param point   待判断点
     * @return true 多边形包含这个点,false 多边形未包含这个点。
     */
    public static boolean isPolygonContainsPoint(List<GeoPoint> mPoints, GeoPoint point) {
        int nCross = 0;
        for (int i = 0; i < mPoints.size(); i++) {
            GeoPoint p1 = mPoints.get(i);
            GeoPoint p2 = mPoints.get((i + 1) % mPoints.size());
            // 取多边形任意一个边,做点point的水平延长线,求解与当前边的交点个数
            // p1p2是水平线段,要么没有交点,要么有无限个交点
            if (p1.getLongitudeE6() == p2.getLongitudeE6())
                continue;
            // point 在p1p2 底部 --> 无交点
            if (point.getLongitudeE6() < Math.min(p1.getLongitudeE6(), p2.getLongitudeE6()))
                continue;
            // point 在p1p2 顶部 --> 无交点
            if (point.getLongitudeE6() >= Math.max(p1.getLongitudeE6(), p2.getLongitudeE6()))
                continue;
            // 求解 point点水平线与当前p1p2边的交点的 X 坐标
            double x = (point.getLongitudeE6() - p1.getLongitudeE6()) * (p2.getLatitudeE6() - p1.getLatitudeE6()) / (p2.getLongitudeE6() - p1.getLongitudeE6()) + p1.getLatitudeE6();
            if (x > point.getLatitudeE6()) // 当x=point.x时,说明point在p1p2线段上
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }

    /**
     * 返回一个点是否在一个多边形边界上
     *
     * @param mPoints 多边形坐标点列表
     * @param point   待判断点
     * @return true 点在多边形边上,false 点不在多边形边上。
     */
    public static boolean isPointInPolygonBoundary(List<GeoPoint> mPoints, GeoPoint point) {
        for (int i = 0; i < mPoints.size(); i++) {
            GeoPoint p1 = mPoints.get(i);
            GeoPoint p2 = mPoints.get((i + 1) % mPoints.size());
            // 取多边形任意一个边,做点point的水平延长线,求解与当前边的交点个数

            // point 在p1p2 底部 --> 无交点
            if (point.getLongitudeE6() < Math.min(p1.getLongitudeE6(), p2.getLongitudeE6()))
                continue;
            // point 在p1p2 顶部 --> 无交点
            if (point.getLongitudeE6() > Math.max(p1.getLongitudeE6(), p2.getLongitudeE6()))
                continue;

            // p1p2是水平线段,要么没有交点,要么有无限个交点
            if (p1.getLongitudeE6() == p2.getLongitudeE6()) {
                double minX = Math.min(p1.getLatitudeE6(), p2.getLatitudeE6());
                double maxX = Math.max(p1.getLatitudeE6(), p2.getLatitudeE6());
                // point在水平线段p1p2上,直接return true
                if ((point.getLongitudeE6() == p1.getLongitudeE6()) && (point.getLatitudeE6() >= minX && point.getLatitudeE6() <= maxX)) {
                    return true;
                }
            } else { // 求解交点
                double x = (point.getLongitudeE6() - p1.getLongitudeE6()) * (p2.getLatitudeE6() - p1.getLatitudeE6()) / (p2.getLongitudeE6() - p1.getLongitudeE6()) + p1.getLatitudeE6();
                if (x == point.getLatitudeE6()) // 当x=point.x时,说明point在p1p2线段上
                    return true;
            }
        }
        return false;
    }

}
