package com.hyht.tdt;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyht.tdt.entity.EntEntity;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MapViewRender;
import com.tianditu.android.maps.Overlay;
import com.tianditu.android.maps.renderoption.LineOption;
import com.tianditu.android.maps.renderoption.PlaneOption;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.toast.XToast;
import org.json.JSONObject;

import javax.microedition.khronos.opengles.GL10;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.xuexiang.xui.XUI.getContext;

public class EntService {
    Context context;
    String urlStr = Constant.GET;

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public EntService(Context context) {
        this.context = context;
    }

    //查询所有的绘图
    void buildSelect(final VolleyCallback callback) {
        //创建一个请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //创建一个请求
        StringRequest stringRequest = new StringRequest(urlStr, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {//发生异常之后的监听回调
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("errorerrorerror = " + error);
                XToast.normal(getContext(),"错误，无法获取信息"+error.getMessage()).show();
            }
        });
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);
    }
    public interface VolleyCallback {
        void onSuccess(String result);
    }
}
