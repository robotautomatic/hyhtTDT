package com.hyht.tdt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import androidx.collection.LruCache;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyUtils {
    private static RequestQueue mQueue;
    private ImageLoader mLoader;
    private ImageLoader.ImageCache mCache;
    private static VolleyUtils mInstance;

    /**
     * 1.构造方法私有化
     * @param context
     */
    private VolleyUtils(Context context) {
        //做一些事情
        mQueue = Volley.newRequestQueue(context);
        mCache = new MyImageCache();
        mLoader = new ImageLoader(mQueue, mCache);
    }

    public RequestQueue getQueue() {
        return mQueue;
    }

    public ImageLoader getLoader() {
        return mLoader;
    }


    /**
     * 2.提供一个静态方法，返回一个当前类
     * @param context
     * @return
     */
    public static VolleyUtils create(Context context) {
        if (mInstance == null) {
            synchronized (VolleyUtils.class) {
                if (mInstance == null) {
                    mInstance = new VolleyUtils(context);
                }
            }
        }
        return mInstance;
    }


    public <T> void get(String url, final Class<T> clazz, final OnResponse<T> listener) {

        HashMap<String, String> map = new HashMap<>();
        listener.OnMap(map);
        String param = prepareParam(map);
        if (param.trim().length() >= 1) {
            url += "?" + param;
        }
        Log.e("Volley", "urlResult---->" + url);
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onResponse", "response-->" + response);

                System.out.println("sys response = "+response);
                Gson gson = new Gson();
                List<EntEntity> newsList_new = gson.fromJson(response, new TypeToken<List<EntEntity>>() {}.getType());
                System.out.println("gson.fromJson(response, clazz) = "+newsList_new);
                listener.onSuccess(gson.fromJson(response, clazz));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", "response-->" + error.getMessage());
                listener.onError(error.getMessage());
            }
        });

        mQueue.add(stringRequest);
    }


    private static String prepareParam(Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder();
        if (paramMap.isEmpty()) {
            return "";
        } else {
            for (String key : paramMap.keySet()) {
                String value =  paramMap.get(key);
                if (sb.length() < 1) {
                    sb.append(key).append("=").append(value);
                } else {
                    sb.append("&").append(key).append("=").append(value);
                }
            }
            return sb.toString();
        }
    }


    public <T> void post(String url, final Class<T> clazz, final OnResponse<T> listener) {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley", "response-->" + response);
                Gson gson = new Gson();
                listener.onSuccess(gson.fromJson(response, clazz));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "response-->" + error.getMessage());
                listener.onError(error.getMessage());
            }
        }) {
            /**
             * Post请求和Get请求的使用步骤上的区别在于请求条件的指定
             * 必须在StringRequest对象的后面添加{}，并且
             * 在{}内重写getParams方法，该方法的返回值就是所有的请求条件
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //将请求条件封装到map对象中
                Map<String, String> map = new HashMap<>();
                listener.OnMap(map);
                return map;
            }
        };
        mQueue.add(stringRequest);
    }

    public <T> void jsonRequest(String url, String json) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", "========" + response);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "========" + error.getMessage());
            }
        });

        mQueue.add(jsonObjectRequest);
    }


    public void loadImg(String url, ImageView view, int maxWidth, int maxHeight, int defaultImageResId, int errorImageResId) {
        mLoader.get(url, //图片的下载路径
                /**
                 * 通过getImageListener方法获取ImageListener接口对象
                 * 参数1： 图片下载完成后，由哪个控件显示图片
                 * 参数2： 设置图片下载过程中显示的默认图片
                 * 参数3： 设置一旦图片下载出错，就显示出错提示图片
                 * */
                ImageLoader.getImageListener(view, defaultImageResId, errorImageResId),
                maxWidth, maxHeight, //图片的最大宽高 指定成0的话就表示不管图片有多大
                ImageView.ScaleType.FIT_XY //图片的缩放模式
        );
    }

    public void loadImg(String url, ImageView view) {
        mLoader.get(url, //图片的下载路径
                ImageLoader.getImageListener(view, R.mipmap.ic_launcher, R.mipmap.ic_launcher),
                0, 0, //图片的最大宽高 指定成0的话就表示不管图片有多大
                ImageView.ScaleType.FIT_XY //图片的缩放模式
        );
    }

    public void loadImg(String url, ImageView view, int defaultImageResId, int errorImageResId) {
        mLoader.get(url, //图片的下载路径
                ImageLoader.getImageListener(view, defaultImageResId, errorImageResId),
                0, 0, //图片的最大宽高 指定成0的话就表示不管图片有多大
                ImageView.ScaleType.FIT_XY //图片的缩放模式
        );
    }

    /**
     * 分配一定内存空间，专门存取图片，一般为内存大小的1/8
     */
    private class MyImageCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        private MyImageCache() {
            //分配最大内存空间的1/8
            long maxMemory = Runtime.getRuntime().maxMemory() / 8;
            mCache = new LruCache<String, Bitmap>((int) maxMemory) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    //得到当前图片的大小
                    return value.getByteCount();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            if (getBitmap(url) == null)
                mCache.put(url, bitmap);
        }
    }

    /**
     * 自定义的类型
     * @param <T>
     */
    public class GsonRequest<T> extends Request<T> {
        private Response.Listener<T> mListener;
        private Gson mGson;
        private Class<T> mClazz;

        private GsonRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListenerlistener, Class<T> clazz) {
            super(method, url, errorListenerlistener);
            this.mListener = listener;
            mGson = new Gson();
            this.mClazz = clazz;
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                Log.e("Volley", "服务器返回JSON------>" + parsed);
                return Response.success(mGson.fromJson(parsed, mClazz), HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new VolleyError(e));
            }
        }

        @Override
        protected void deliverResponse(T response) {
            if (mListener != null) {
                mListener.onResponse(response);
            }
        }

        @Override
        protected void onFinish() {
            super.onFinish();
            mListener = null;
        }
    }
    public void uploadimage(String url,String filePath) {
        /*
                实现思路
                1.将要上传的图片转为 Bitmap 类型
                2.将Bitmap类型的图片编译成 Base64 的字节流
                3.通过 Volley 的post方法上传上去
            */
        // 实现第一步 将存储卡中的文件转为Bitmap 类型
        FileInputStream fis = null;

        {
            try {
                fis = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        // 然后使用上一个博客提到的Volley post上传参数一样上传上去就好了
        final String user_img =bitmapToBase64(bitmap);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 返回的json参数 response
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 请求错误就会进入这里
            } }) {
            //////////////// 在这里可以获取Cookie或者是设置Cookie
            // 像服务器post提交参数的方法
            @Override
            protected Map<String, String> getParams() {
                // 在这里设置需要post的参数
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("imageData", user_img); return map;
            }
        };
	    mQueue.add(stringRequest);
    }

    // 实现第二步 Bitmap 转 Base64 的字节流
    public String bitmapToBase64(Bitmap bitmap) {
        String result = null; ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush(); baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush(); baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public interface OnResponse<T> {

        void OnMap(Map<String, String> map);

        void onSuccess(T response);

        void onError(String error);
    }
}
