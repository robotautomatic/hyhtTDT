package com.hyht.tdt;

import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLconnectionUtil {

    public void Get(final String UrlAction, String UrlBody){
        final String url = UrlAction;
        final String body = UrlBody;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myUrl = new URL(url);
                    //得到connection对象。
                    HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);//允许写出
                    connection.setDoInput(true);//允许读入
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(body);
                    writer.close();

                    //连接
                    connection.connect();
                    //得到响应码
                    int responseCode = connection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){
                        //得到响应流
                        InputStream inputStream = connection.getInputStream();
                        //将响应流转换成字符串
                        String str = IOUtils.toString(inputStream, "utf-8");
                        Log.d("result","result============="+str);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void Post(final String UrlAction, String UrlBody){
        final String url = UrlAction;
        final String body = UrlBody;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myurl = new URL(url);
                    //得到connection对象。
                    HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);//允许写出
                    connection.setDoInput(true);//允许读入
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(body);
                    writer.close();

                    //连接
                    connection.connect();
                    //得到响应码
                    int responseCode = connection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){
                        //得到响应流
                        InputStream inputStream = connection.getInputStream();
                        //将响应流转换成字符串
/*                                        StringBuilder sb = new StringBuilder();
                                        String line;
                                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                                        while ((line = br.readLine()) != null) {
                                            sb.append(line);
                                        }
                                        String str = sb.toString();*/
                        String str = IOUtils.toString(inputStream, "utf-8");

                        Log.d("result",str);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
