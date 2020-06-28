package com.hyht.tdt;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xuexiang.xui.XUI;

public class MyApp extends Application {

    public static RequestQueue queue;

    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志.

        queue = Volley.newRequestQueue(getApplicationContext());
    }
}
