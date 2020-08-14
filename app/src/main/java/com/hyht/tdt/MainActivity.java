package com.hyht.tdt;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.MbmsDownloadSession;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyht.tdt.entity.EntEntity;
import com.hyht.tdt.utils.FileProvider7;
import com.hyht.tdt.utils.FileUtil;
import com.hyht.tdt.utils.VolleyUtils;
import com.tianditu.android.maps.*;
import com.tianditu.android.maps.overlay.MarkerOverlay;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.widget.alpha.XUIAlphaButton;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheetItemView;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.strategy.InputCallback;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;
import com.xuexiang.xui.widget.layout.XUIButton;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static com.xuexiang.xui.XUI.getContext;

public class MainActivity extends Activity implements View.OnClickListener {

    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();

    String[] dpiItems = new String[]{
            "影像地图",
            "地形地图",
            "普通地图",
    };
    public static AdapterItem[] menuItems = new AdapterItem[]{
            new AdapterItem("取消显示", R.drawable.ic_all_overlay),
            new AdapterItem("全部显示", R.drawable.tuceng),
            new AdapterItem("点覆盖物", R.drawable.ic_point_overlay),
            new AdapterItem("线覆盖物", R.drawable.ic_line_overlay),
            new AdapterItem("面覆盖物", R.drawable.ic_plane_overlay),
    };

    private MapView mapView;
    private XUISimplePopup mListPopup;
    private MyOverlayDrawGraph mOverlay;
    private MyGeoPoint myGeoPoint;
    String mTempPhotoPath;
    Uri imageUri;

    //在API23+以上，不仅要在AndroidManifest.xml里面添加权限 还要在JAVA代码中请求权限：
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    ShowCustomDialog showCustomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在加载布局之前获取所需权限
        addPermission();
        setContentView(R.layout.activity_main);
        init();
        initComponents();


    }

    public void initComponents() {
        //声明初始化控件View(Button)
        Button btnOne = findViewById(R.id.switchLayer);
        btnOne.setOnClickListener(this);
        findViewById(R.id.switchLayer).setOnClickListener(this);

        Button btnDraw = findViewById(R.id.btn_draw);
        btnDraw.setOnClickListener(this);
        findViewById(R.id.btn_draw).setOnClickListener(this);


        findViewById(R.id.btn_draw_confirm).setOnClickListener(this);

        findViewById(R.id.btn_draw_rollback).setOnClickListener(this);

        findViewById(R.id.btn_draw_delete).setOnClickListener(this);

        findViewById(R.id.btn_draw_save).setOnClickListener(this);

        findViewById(R.id.btn_draw_exit).setOnClickListener(this);

        findViewById(R.id.btn_select).setOnClickListener(this);

        findViewById(R.id.btn_overlay).setOnClickListener(this);

        findViewById(R.id.btn_location).setOnClickListener(this);


    }


    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.switchLayer:
                selectLayer(v);
                break;
            case R.id.btn_draw:
                drawBottomSheetGrid();
                break;
            case R.id.btn_draw_confirm:
                mOverlay.setDrawConfirm(1);
                XToast.normal(this, "绘画完成！").show();

               /* Drawable marker = getResources().getDrawable(R.mipmap.tuding);
                points = mOverlay.getPoints();
                myGeoPoint = new MyGeoPoint(marker, MainActivity.this, points);
                System.out.println("pppoints = " + points);
                mapView.addOverlay(myGeoPoint);*/
                findViewById(R.id.btn_draw_rollback).setVisibility(View.GONE);

                break;
            case R.id.btn_draw_rollback:
                points = mOverlay.getPoints();
                if (points.size() != 0) {
                    points.remove(points.size() - 1);
                }
                mOverlay.setPoints(points);
                mapView.invalidate();
                XToast.normal(this, "取消上次点击！").show();
                break;
            case R.id.btn_draw_delete:
                points.clear();
                mOverlay.setPoints(points);
                mapView.invalidate();
                XToast.normal(this, "清除图像！").show();
                mapView.removeOverlay(myGeoPoint);
                break;
            case R.id.btn_draw_save:
                points =mOverlay.getPoints();
                if(points.size()==0){
                    XToast.normal(this,"请正确绘制图形").show();
                }else {
                    showCustomDialog = new ShowCustomDialog();
                    showCustomDialog.build();
                }

                break;
            case R.id.btn_draw_exit:
                mapView.removeOverlay(mOverlay);
                mapView.removeAllOverlay();
                mapView.invalidate();
                drawBottonVisual(false);
                LinearLayout ll = findViewById(R.id.ll_btn);
                while(ll.getChildCount() > 5){
                    ll.removeViewAt(0);
                }
                XToast.normal(this, "退出绘制").show();
                MyLocationOverlay myLocation2 = new MyLocationOverlay(MainActivity.this, mapView);
                myLocation2.enableCompass(); //显示指南针
                myLocation2.enableMyLocation(); //显示我的位置
                mapView.addOverlay(myLocation2);
                break;
            case R.id.btn_select:
                showCustomDialog = new ShowCustomDialog();
                showCustomDialog.buildSelect();
                break;
            case R.id.btn_overlay:
                showSaveOverlay(v);
                break;
            case R.id.btn_location:
                MyLocationOverlay myLocation = new MyLocationOverlay(this, mapView);
                myLocation.enableCompass(); //显示指南针
                myLocation.enableMyLocation(); //显示我的位置
                GeoPoint myLocationPoint = myLocation.getMyLocation();
                mapView.addOverlay(myLocation);
                MapController mapController = new MapController(mapView);
                mapController.animateTo(myLocationPoint);
                break;
        }
    }

    /**
     * 选择图片
     */
    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, 2);
        System.out.println("EXTERNAL_CONTENT_URI = " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        System.out.println("ACTION_PICK = " + Intent.ACTION_PICK);
    }

    /**
     * 拍照选择图片
     */
    private void takePhoto() {
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(Environment.getExternalStorageDirectory() + File.separator + "photoTest" + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File photoFile = new File(fileDir, "photo.jpeg");
        mTempPhotoPath = photoFile.getAbsolutePath();
        imageUri = FileProvider7.getUriForFile(MainActivity.this, photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, 1);
    }

    /**
     * 回调，
     *
     * @param requestCode 根据requestCode的值来调用方法。
     * @param resultCode
     * @param data        返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("1111111111111111111");
        System.out.println("requestCode = " + requestCode);
        System.out.println("resultCode = " + resultCode);
        System.out.println("data = " + data);
        View view;
        ImageView imageView;
        EditText editText;
        switch (requestCode) {
            case 1:
                if (resultCode != 0) {
                    view = showCustomDialog.materialDialog.getCustomView();
                    imageView = view.findViewById(R.id.image_choose);
                    imageView.setImageURI(null);
                    imageView.setImageURI(imageUri);
                    imageView.setVisibility(View.VISIBLE);
                    editText = view.findViewById(R.id.ent_image);
                    editText.setText(mTempPhotoPath);
                }
                break;

            case 2:

                if (data != null) {
                    XToast.normal(getContext(), "images");

                    System.out.println("22222222222222222222");
                    imageUri = data.getData();

                    System.out.println("uri = " + imageUri);

                    String filePath = FileUtil.getFilePathByUri(this, imageUri);

                    System.out.println("filePath = " + filePath);

                    view = showCustomDialog.materialDialog.getCustomView();
                    imageView = view.findViewById(R.id.image_choose);
                    imageView.setImageURI(imageUri);
                    imageView.setVisibility(View.VISIBLE);
                    editText = view.findViewById(R.id.ent_image);
                    editText.setText(filePath);
                }
                break;

            /*                setImageURI(data.getData());*/

        }
    }

    /**
     * 绘图的操作控件是否显示
     *
     * @param visualAble
     */
    void drawBottonVisual(Boolean visualAble) {
        if (visualAble) {
            findViewById(R.id.btn_draw_confirm).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_draw_rollback).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_draw_delete).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_draw_save).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_draw_exit).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btn_draw_confirm).setVisibility(View.GONE);
            findViewById(R.id.btn_draw_rollback).setVisibility(View.GONE);
            findViewById(R.id.btn_draw_delete).setVisibility(View.GONE);
            findViewById(R.id.btn_draw_save).setVisibility(View.GONE);
            findViewById(R.id.btn_draw_exit).setVisibility(View.GONE);
        }
    }


    /**
     * 显示自定义对话框
     */
    private class ShowCustomDialog {
        MaterialDialog materialDialog;

        void buildSelect() {

            final String urlStr = Constant.GET;
            //创建一个请求队列
            final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            //创建一个请求
            StringRequest stringRequest = new StringRequest(urlStr, new Response.Listener<String>() {
                //正确接受数据之后的回调
                @Override
                public void onResponse(final String response) {
                    System.out.println("responsesssssssssssss = " + response);
                    final Type type = new TypeToken<List<EntEntity>>() {
                    }.getType();
                    final Gson gson = new Gson();
                    final List<EntEntity> userList = gson.fromJson(response, type);
                    final List<String> list = new ArrayList<>();
                    for (EntEntity user : userList
                    ) {
                        list.add("名称：" + user.getEntName() + "  编码：" + user.getEntCode());
                    }

                    System.out.println("ppppppppprint = " + userList);
                    System.out.println("ppppppppprint list = " + list);

                    materialDialog = new MaterialDialog.Builder(MainActivity.this)
                            .items(list).itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, final int position, CharSequence text) {
                                    materialDialog = new MaterialDialog.Builder(MainActivity.this)
                                            .customView(R.layout.dialog_custom_select_one, true)
                                            .iconRes(R.drawable.ic_save)
                                            .title("详细信息")
                                            .positiveText("显示在地图上").onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    EntEntity entEntity = userList.get(position);
                                                    MyShowOverlayDetails myShowOverlayDetails = new MyShowOverlayDetails(getDrawable(R.mipmap.tuding), MainActivity.this, entEntity);
                                                    List<EntEntity> entEntityList = new ArrayList<>();
                                                    entEntityList.add(entEntity);
                                                    MyOverlayShow myOverlayShow = new MyOverlayShow(entEntityList);
                                                    mapView.addOverlay(myOverlayShow);
                                                    mapView.addOverlay(myShowOverlayDetails);
                                                    mapView.invalidate();
                                                }
                                            })
                                            .negativeText("编辑").onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    materialDialog = new MaterialDialog.Builder(MainActivity.this)
                                                            .customView(R.layout.dialog_custom,true)
                                                            .title("编辑")
                                                            .positiveText("确认").onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                                    EntEntity entEntity = userList.get(position);
                                                                    final int ent_id = entEntity.getId();
                                                                    final String ent_name = ((EditText) dialog.getCustomView().findViewById(R.id.ent_name)).getText().toString();
                                                                    final String ent_code = ((EditText) dialog.getCustomView().findViewById(R.id.ent_code)).getText().toString();
                                                                    final String ent_attribute = ((EditText) dialog.getCustomView().findViewById(R.id.ent_attribute)).getText().toString();
                                                                    final String ent_address = ((EditText) dialog.getCustomView().findViewById(R.id.ent_address)).getText().toString();
                                                                    final String ent_owner = ((EditText) dialog.getCustomView().findViewById(R.id.ent_owner)).getText().toString();
                                                                    final String ent_property = ((EditText) dialog.getCustomView().findViewById(R.id.ent_property)).getText().toString();
                                                                    String ent_image = ((EditText) dialog.getCustomView().findViewById(R.id.ent_image)).getText().toString();
                                                                    final String ent_list = ((EditText) dialog.getCustomView().findViewById(R.id.ent_list)).getText().toString();

                                                                    String urlStr = Constant.UPDATE;
                                                                    String imageData = "";
                                                                    if (!ent_image.isEmpty()) {
                                                                        FileInputStream fis = null;
                                                                        {
                                                                            try {
                                                                                fis = new FileInputStream(ent_image);
                                                                            } catch (FileNotFoundException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                                                                        imageData = VolleyUtils.create(getContext()).bitmapToBase64(bitmap);
                                                                    }

                                                                    final String finalEnt_image = ent_image;
                                                                    final String finalImageData = imageData;
                                                                    VolleyUtils.create(getContext())
                                                                            .post(urlStr, EntEntity.class, new VolleyUtils.OnResponse<EntEntity>() {
                                                                                @Override
                                                                                public void OnMap(Map<String, String> map) {
                                                                                    map.put("id", String.valueOf(ent_id));
                                                                                    map.put("entCode", ent_code);
                                                                                    map.put("entName", ent_name);
                                                                                    map.put("entAttribute", ent_attribute);
                                                                                    map.put("entAddress", ent_address);
                                                                                    map.put("entOwner", ent_owner);
                                                                                    map.put("entProperty", ent_property);
                                                                                    map.put("entImage", finalEnt_image);
                                                                                    map.put("coorList", ent_list);
                                                                                    map.put("imageData", finalImageData);

                                                                                }

                                                                                @Override
                                                                                public void onSuccess(EntEntity response) {
                                                                                    XToast.normal(MainActivity.this,"修改成功！").show();
                                                                                }

                                                                                @Override
                                                                                public void onError(String error) {
                                                                                    XToast.normal(MainActivity.this,"修改失败！").show();
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .show();
                                                    materialDialog.getCustomView().findViewById(R.id.btn_getImage).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            new BottomSheet.BottomListSheetBuilder(MainActivity.this)
                                                                    .setTitle("选择图片方式")
                                                                    .addItem("拍照选择")
                                                                    .addItem("相册选择")
                                                                    .setIsCenter(true)
                                                                    .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                                                                        @Override
                                                                        public void onClick(BottomSheet dialog, View itemView, int position, String tag) {
                                                                            dialog.dismiss();
                                                                            switch (position) {
                                                                                case 0: {
                                                                                    takePhoto();
                                                                                }
                                                                                break;
                                                                                case 1: {
                                                                                    choosePhoto();
                                                                                }
                                                                                break;
                                                                            }
                                                                        }
                                                                    })
                                                                    .build()
                                                                    .show();
                                                        }
                                                    });
                                                    materialDialog.getCustomView().findViewById(R.id.image_choose).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                                                            View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                                                            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                                                            ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                                                            //imageDownloader.download("图片地址",img); // 这个是加载网络图片的，可以是自己的图片设置方法
                                                            img.setImageURI(imageUri);
                                                            dialog.setView(imgEntryView); // 自定义dialog
                                                            dialog.show();
                                                            // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                                                            imgEntryView.setOnClickListener(new View.OnClickListener(){
                                                                public void onClick(View paramView) {
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                        }
                                                    });

                                                    View view = showCustomDialog.materialDialog.getCustomView();
                                                    EditText ent_name = view.findViewById(R.id.ent_name);
                                                    ent_name.setText(userList.get(position).getEntName());
                                                    EditText ent_code = view.findViewById(R.id.ent_code);
                                                    ent_code.setText(userList.get(position).getEntCode());
                                                    EditText ent_attribute = view.findViewById(R.id.ent_attribute);
                                                    ent_attribute.setText(userList.get(position).getEntAttribute());
                                                    EditText ent_address = view.findViewById(R.id.ent_address);
                                                    ent_address.setText(userList.get(position).getEntAddress());
                                                    EditText ent_owner = view.findViewById(R.id.ent_owner);
                                                    ent_owner.setText(userList.get(position).getEntOwner());
                                                    EditText ent_property = view.findViewById(R.id.ent_property);
                                                    ent_property.setText(userList.get(position).getEntProperty());
                                                    EditText ent_list = view.findViewById(R.id.ent_list);
                                                    ent_list.setText(userList.get(position).getCoorList());

                                                    final String s = "http://39.98.192.41:8080/" + userList.get(position).getEntImage();
                                                    ImageView ent_image_choose = view.findViewById(R.id.image_choose);
                                                    VolleyUtils.create(MainActivity.this).loadImg(s, ent_image_choose);
                                                    ent_image_choose.setVisibility(View.VISIBLE);

                                                    materialDialog.getCustomView().findViewById(R.id.image_choose).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                                                            View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                                                            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                                                            ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                                                            VolleyUtils.create(MainActivity.this).loadImg(s, img);
                                                            dialog.setView(imgEntryView); // 自定义dialog
                                                            dialog.show();
                                                            // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                                                            imgEntryView.setOnClickListener(new View.OnClickListener(){
                                                                public void onClick(View paramView) {
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            })
                                            .neutralText("删除").neutralColor(getColor(R.color.colorAccent)).onNeutral(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    MaterialDialog dia_delete = new MaterialDialog.Builder(MainActivity.this)
                                                            .title("警告").titleColor(getColor(R.color.colorAccent))
                                                            .content("此操作会将选中的覆盖物永久删除且无法恢复")
                                                            .positiveText("删除").positiveColor(getColor(R.color.colorAccent)).onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                                    StringRequest request_Delete = new StringRequest(StringRequest.Method.DELETE, Constant.DELETE+"?id="+userList.get(position).getId(), new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            XToast.normal(MainActivity.this, "删除成功！").show();
                                                                        }
                                                                    }, new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {

                                                                            XToast.normal(MainActivity.this,"删除失败！").show();
                                                                        }
                                                                    });
                                                                    requestQueue.add(request_Delete);
                                                                }
                                                            })
                                                            .negativeText("取消")
                                                            .show();
                                                }
                                            })
                                            .show();

                                    View view = showCustomDialog.materialDialog.getCustomView();
                                    TextView ent_name = view.findViewById(R.id.ent_name);
                                    ent_name.setText(userList.get(position).getEntName());
                                    TextView ent_code = view.findViewById(R.id.ent_code);
                                    ent_code.setText(userList.get(position).getEntCode());
                                    TextView ent_attribute = view.findViewById(R.id.ent_attribute);
                                    ent_attribute.setText(userList.get(position).getEntAttribute());
                                    TextView ent_address = view.findViewById(R.id.ent_address);
                                    ent_address.setText(userList.get(position).getEntAddress());
                                    TextView ent_owner = view.findViewById(R.id.ent_owner);
                                    ent_owner.setText(userList.get(position).getEntOwner());
                                    TextView ent_property = view.findViewById(R.id.ent_property);
                                    ent_property.setText(userList.get(position).getEntProperty());
                                    TextView ent_list = view.findViewById(R.id.ent_list);
                                    ent_list.setText(userList.get(position).getCoorList());

                                    final String s = "http://39.98.192.41:8080/" + userList.get(position).getEntImage();
                                    ImageView ent_image_choose = view.findViewById(R.id.image_choose);
                                    VolleyUtils.create(MainActivity.this).loadImg(s, ent_image_choose);
                                    ent_image_choose.setVisibility(View.VISIBLE);

                                    materialDialog.getCustomView().findViewById(R.id.image_choose).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                                            View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                                            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                                            ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                                            VolleyUtils.create(MainActivity.this).loadImg(s, img);
                                            dialog.setView(imgEntryView); // 自定义dialog
                                            dialog.show();
                                            // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                                            imgEntryView.setOnClickListener(new View.OnClickListener(){
                                                public void onClick(View paramView) {
                                                    dialog.cancel();
                                                }
                                            });
                                        }
                                    });

                                }
                            })
                            .iconRes(R.drawable.ic_save)
                            .title("查询所有覆盖物")
                            .positiveText("确认")
                            .neutralText("搜索")
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    DialogLoader.getInstance().showInputDialog(MainActivity.this,
                                            R.mipmap.tuding,
                                            "搜索",
                                            "根据输入的值对名称，属性，地址，所有者，类型进行模糊查询",
                                            new InputInfo(InputType.TYPE_CLASS_TEXT, "请输入"),
                                            new InputCallback() {
                                                @Override
                                                public void onInput(@NonNull DialogInterface dialog, CharSequence input) {
                                                }
                                            },
                                            "确认",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    if (dialog instanceof MaterialDialog) {
                                                        XToast.normal(getContext(), "你输入了:" + ((MaterialDialog) dialog).getInputEditText().getText().toString());
                                                        EntService entService = new EntService(MainActivity.this);
                                                        entService.setUrlStr(Constant.findAllLike + "?entCode=" + ((MaterialDialog) dialog).getInputEditText().getText().toString());
                                                        entService.buildSelect(new EntService.VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(String result) {
                                                                List<EntEntity> searchEntityList = gson.fromJson(result, type);
                                                                List<String> searchList = new ArrayList<>();
                                                                for (EntEntity user : searchEntityList
                                                                ) {
                                                                    searchList.add("名称：" + user.getEntName() + "  编码：" + user.getEntCode());
                                                                }
                                                                materialDialog.setItems(searchList.toArray(new CharSequence[searchList.size()]));
                                                            }
                                                        });
                                                        materialDialog.show();
                                                    }
                                                }
                                            },
                                            "取消",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }
                                    );
                                }
                            })
                            .negativeText("取消").show();
                }
            }, new Response.ErrorListener() {//发生异常之后的监听回调
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("errorerrorerror = " + error);
                    XToast.normal(getContext(), "错误，无法获取信息" + error.getMessage()).show();
                }
            });
            //将创建的请求添加到请求队列当中
            requestQueue.add(stringRequest);


        }

        void build() {
            materialDialog = new MaterialDialog.Builder(MainActivity.this)
                    .customView(R.layout.dialog_custom, true)
                    .iconRes(R.drawable.ic_save)
                    .title("保存")
                    .negativeText("取消")
                    .positiveText("确认").onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            final String ent_name = ((EditText) dialog.getCustomView().findViewById(R.id.ent_name)).getText().toString();
                            final String ent_code = ((EditText) dialog.getCustomView().findViewById(R.id.ent_code)).getText().toString();
                            final String ent_attribute = ((EditText) dialog.getCustomView().findViewById(R.id.ent_attribute)).getText().toString();
                            final String ent_address = ((EditText) dialog.getCustomView().findViewById(R.id.ent_address)).getText().toString();
                            final String ent_owner = ((EditText) dialog.getCustomView().findViewById(R.id.ent_owner)).getText().toString();
                            final String ent_property = ((EditText) dialog.getCustomView().findViewById(R.id.ent_property)).getText().toString();
                            String ent_image = ((EditText) dialog.getCustomView().findViewById(R.id.ent_image)).getText().toString();
                            final String ent_list = ((EditText) dialog.getCustomView().findViewById(R.id.ent_list)).getText().toString();
                            final int ent_type = mOverlay.draw;
                            final int size = points.size();
                            String ent_addition = "";
                            for (GeoPoint point : points
                            ) {
                                ent_addition = ent_addition + point.getLongitudeE6() + "," + point.getLatitudeE6() + "/";
                            }
                            String urlStr = Constant.ADD;
                            System.out.println("ent_imageent_imageent_imageent_imageent_imageent_image" + ent_image.isEmpty());
                            String imageData = "";
                            if (!ent_image.isEmpty()) {
                                FileInputStream fis = null;
                                {
                                    try {
                                        fis = new FileInputStream(ent_image);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                                imageData = VolleyUtils.create(getContext()).bitmapToBase64(bitmap);
                                System.out.println(imageData);
                            }
/*
                String UrlBody = "entType=" + ent_type + "&entCode=" + ent_code + "&entName=" + ent_name + "&entAttribute=" + ent_attribute + "&entAddress=" + ent_address
                        + "&entOwner=" + ent_owner + "&entProperty=" + ent_property + "&entImage=" + ent_image + "&entAddition=" + ent_addition + "&coorNum=" + size + "&coorList=" + ent_list;
*/
                            final String finalEnt_addition = ent_addition;
                            final String finalEnt_image = ent_image;
                            final String finalImageData = imageData;
                            VolleyUtils.create(getContext())
                                    .post(urlStr, EntEntity.class, new VolleyUtils.OnResponse<EntEntity>() {
                                        @Override
                                        public void OnMap(Map<String, String> map) {
                                            map.put("entType", String.valueOf(ent_type));
                                            map.put("entCode", ent_code);
                                            map.put("entName", ent_name);
                                            map.put("entAttribute", ent_attribute);
                                            map.put("entAddress", ent_address);
                                            map.put("entOwner", ent_owner);
                                            map.put("entProperty", ent_property);
                                            map.put("entImage", finalEnt_image);
                                            map.put("entAddition", finalEnt_addition);
                                            map.put("coorNum", String.valueOf(size));
                                            map.put("coorList", ent_list);
                                            map.put("imageData", finalImageData);

                                        }

                                        @Override
                                        public void onSuccess(EntEntity response) {
                                            Log.e("TAG", "response---->" + response);
                                            mapView.removeOverlay(mOverlay);
                                            mapView.removeAllOverlay();
                                            mapView.invalidate();
                                            drawBottonVisual(false);
                                            XToast.normal(MainActivity.this,"保存成功").show();
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.e("TAG", "error---->" + error);
                                        }
                                    });
                        }
                    }).show();
            materialDialog.getCustomView().findViewById(R.id.btn_getImage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.BottomListSheetBuilder(MainActivity.this)
                            .setTitle("选择图片方式")
                            .addItem("拍照选择")
                            .addItem("相册选择")
                            .setIsCenter(true)
                            .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                                @Override
                                public void onClick(BottomSheet dialog, View itemView, int position, String tag) {
                                    dialog.dismiss();
                                    switch (position) {
                                        case 0: {
                                            takePhoto();
                                        }
                                        break;
                                        case 1: {
                                            choosePhoto();
                                        }
                                        break;
                                    }
                                }
                            })
                            .build()
                            .show();
                }
            });
            materialDialog.getCustomView().findViewById(R.id.image_choose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                    final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                    ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                    //imageDownloader.download("图片地址",img); // 这个是加载网络图片的，可以是自己的图片设置方法
                    img.setImageURI(imageUri);
                    dialog.setView(imgEntryView); // 自定义dialog
                    dialog.show();
                    // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                    imgEntryView.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View paramView) {
                            dialog.cancel();
                        }
                    });
                }
            });
        }
    }

    public void addPermission() {
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void init() {
        mapView = (MapView) findViewById(R.id.main_mapview);
        //启用内置的地图缩放按钮
        mapView.setBuiltInZoomControls(true);
        //得到mapview的控制权，可以用它控制和驱动平移和缩放
        MapController mapController = mapView.getController();
        //用给定的经纬度构造一个GeoPoint，单位是微度（度*1E6）
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
        //设置地图中心点
        mapController.setCenter(point);
        //设置地图等级
        mapController.setZoom(13);
        ;
        //设置天地图log位置
        mapView.setLogoPos(MapView.LOGO_NONE);

        MyLocationOverlay myLocation = new MyLocationOverlay(this, mapView);
        myLocation.enableCompass(); //显示指南针
        myLocation.enableMyLocation(); //显示我的位置
        mapView.addOverlay(myLocation);/*
        GeoPoint mPoint = myLocation.getMyLocation();
        //动画移动到当前位置
        mapController.animateTo(mPoint);*/

        
    }

    //切换图层
    void selectLayer(View v) {
        mListPopup = new XUISimplePopup(this, dpiItems)
                .create(DensityUtils.dp2px(getContext(), 170), new XUISimplePopup.OnPopupItemClickListener() {
                    @Override
                    public void onItemClick(XUISimpleAdapter adapter, AdapterItem item, int position) {
                        switch (position) {
                            case 0: {
                                mapView.setMapType(MapView.TMapType.MAP_TYPE_IMG);// 设置地图显示为影像。
                            }
                            break;
                            case 1: {
                                mapView.setMapType(MapView.TMapType.MAP_TYPE_TERRAIN);
                                ;//设置地图显示为地形。
                            }
                            break;
                            case 2: {
                                mapView.setMapType(MapView.TMapType.MAP_TYPE_VEC);//设置地图显示为矢量。
                            }
                            break;
                        }
                        System.out.println("position  " + position);
                        System.out.println("item  " + item);
                        System.out.println("getTitle  " + item.getTitle());
                        System.out.println("toString  " + item.getTitle().toString());
                        XToast.normal(getContext(), item.getTitle().toString()).show();
                    }
                })
                .setHasDivider(true);
        mListPopup.showDown(v);
    }

    //切换显示覆盖物图层
    void showSaveOverlay(View v) {
        mListPopup = new XUISimplePopup(this, menuItems)
                .create(DensityUtils.dp2px(getContext(), 210), new XUISimplePopup.OnPopupItemClickListener() {
                    @Override
                    public void onItemClick(XUISimpleAdapter adapter, AdapterItem item, int position) {
                        EntService entService = new EntService(getContext());
                        switch (position) {
                            case 0: {
                                mapView.removeCache();
                                mapView.removeAllOverlay();
                                mapView.invalidate();
                            }
                            break;
                            //显示所有形状的覆盖物
                            case 1: {
                                entService.buildSelect(new EntService.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Type type = new TypeToken<List<EntEntity>>() {
                                        }.getType();
                                        Gson gson = new Gson();
                                        List<EntEntity> overlayList = gson.fromJson(result, type);
                                        MyOverlayShow myOverlayShow = new MyOverlayShow(overlayList);
                                        mapView.addOverlay(myOverlayShow);
                                        for (EntEntity entEntity : overlayList
                                        ) {
                                            MyShowOverlayDetails myShowOverlayDetails = new MyShowOverlayDetails(getDrawable(R.mipmap.tuding), MainActivity.this, entEntity);
                                            mapView.addOverlay(myShowOverlayDetails);
                                        }
                                    }
                                });
                            }
                            break;
                            //点
                            case 2: {
                                entService.buildSelect(new EntService.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Type type = new TypeToken<List<EntEntity>>() {
                                        }.getType();
                                        Gson gson = new Gson();
                                        List<EntEntity> overlayList = gson.fromJson(result, type);
                                        List<EntEntity> overlayListPoint = new ArrayList<>();
                                        for (EntEntity ent : overlayList
                                        ) {
                                            if (ent.getEntType() == 0) {
                                                overlayListPoint.add(ent);
                                            }
                                        }
                                        MyOverlayShow myOverlayShow = new MyOverlayShow(overlayListPoint);
                                        mapView.addOverlay(myOverlayShow);
                                        for (EntEntity entEntity : overlayListPoint
                                        ) {
                                            MyShowOverlayDetails myShowOverlayDetails = new MyShowOverlayDetails(getDrawable(R.mipmap.tuding), MainActivity.this, entEntity);
                                            mapView.addOverlay(myShowOverlayDetails);
                                        }
                                    }
                                });
                            }
                            break;
                            //线
                            case 3: {
                                entService.buildSelect(new EntService.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Type type = new TypeToken<List<EntEntity>>() {
                                        }.getType();
                                        Gson gson = new Gson();
                                        List<EntEntity> overlayList = gson.fromJson(result, type);
                                        List<EntEntity> overlayListPoint = new ArrayList<>();
                                        for (EntEntity ent : overlayList
                                        ) {
                                            if (ent.getEntType() == 1) {
                                                overlayListPoint.add(ent);
                                            }
                                        }
                                        MyOverlayShow myOverlayShow = new MyOverlayShow(overlayListPoint);
                                        mapView.addOverlay(myOverlayShow);
                                        for (EntEntity entEntity : overlayListPoint
                                        ) {
                                            MyShowOverlayDetails myShowOverlayDetails = new MyShowOverlayDetails(getDrawable(R.mipmap.tuding), MainActivity.this, entEntity);
                                            mapView.addOverlay(myShowOverlayDetails);
                                        }
                                    }
                                });
                            }
                            break;
                            //面
                            case 4: {
                                entService.buildSelect(new EntService.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Type type = new TypeToken<List<EntEntity>>() {
                                        }.getType();
                                        Gson gson = new Gson();
                                        List<EntEntity> overlayList = gson.fromJson(result, type);
                                        List<EntEntity> overlayListPolygon = new ArrayList<>();
                                        for (EntEntity ent : overlayList
                                        ) {
                                            if (ent.getEntType() == 2) {
                                                overlayListPolygon.add(ent);
                                            }
                                        }
                                        System.out.println("aa"+ overlayListPolygon);
                                        OverlayShow overlayShowPolygon = new OverlayShow(overlayListPolygon, MainActivity.this);
                                        mapView.addOverlay(overlayShowPolygon);
                                    }
                                });
                            }
                            break;
                        }
                        XToast.normal(getContext(), item.getTitle().toString()).show();
                    }
                });
        mListPopup.showDown(v);
    }

    //绘制图形
    void drawBottomSheetGrid() {

        final int TAG_SHARE_WECHAT_FRIEND = 0;
        final int TAG_SHARE_WECHAT_MOMENT = 1;
        final int TAG_SHARE_WEIBO = 2;
        final int TAG_SHARE_CHAT = 3;
        final BottomSheet.BottomGridSheetBuilder builder = new BottomSheet.BottomGridSheetBuilder(this);
        builder
                .addItem(R.drawable.huizhidian, "绘制点", TAG_SHARE_WECHAT_FRIEND, BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.zhexian, "绘制折线", TAG_SHARE_WECHAT_MOMENT, BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.huizhimian, "绘制多边形", TAG_SHARE_WEIBO, BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.huizhimian,"移动绘制",TAG_SHARE_CHAT,BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .setOnSheetItemClickListener(new BottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(BottomSheet dialog, BottomSheetItemView itemView) {
                        dialog.dismiss();
                        int tag = (int) itemView.getTag();
                        LinearLayout ll = findViewById(R.id.ll_btn);
                        while(ll.getChildCount() > 5){
                            ll.removeViewAt(0);
                        }
                        switch (tag) {
                            case 0: {

                                points.clear();
                                mapView.removeOverlay(mOverlay);
                                mOverlay = new MyOverlayDrawGraph(MainActivity.this);
                                mOverlay.setDrawConfirm(0);
                                mapView.addOverlay(mOverlay);
                                drawBottonVisual(true);
                            }
                            break;
                            case 1: {

                                points.clear();
                                mapView.removeOverlay(mOverlay);
                                mOverlay = new MyOverlayDrawGraph(MainActivity.this);
                                mOverlay.setDrawConfirm(0);
                                mOverlay.setDraw(1);
                                mapView.addOverlay(mOverlay);
                                drawBottonVisual(true);
                            }
                            break;
                            case 2: {

                                points.clear();
                                mapView.removeOverlay(mOverlay);
                                mOverlay = new MyOverlayDrawGraph(MainActivity.this);
                                mOverlay.setDrawConfirm(0);
                                mOverlay.setDraw(2);
                                mapView.addOverlay(mOverlay);
                                drawBottonVisual(true);
                            }
                            break;
                            case 3: {

                                new BottomSheet.BottomListSheetBuilder(MainActivity.this)
                                        .setTitle("选择")
                                        .addItem("手动输入")
                                        .addItem("时间间隔")
                                        .setIsCenter(true)
                                        .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                                            @Override
                                            public void onClick(BottomSheet dialog, View itemView, int position, String tag) {
                                                dialog.dismiss();
                                                switch (position) {
                                                    case 0: {
                                                        points.clear();
                                                        mapView.removeOverlay(mOverlay);
                                                        mOverlay = new MyOverlayDrawGraph(MainActivity.this);
                                                        mOverlay.setDrawConfirm(1);
                                                        mOverlay.setDraw(2);

                                                        LinearLayout linearLayout = findViewById(R.id.ll_btn);
                                                        Button btn_begin = DefaultButton(MainActivity.this);
                                                        btn_begin.setText("开始");


                                                        final MyLocationOverlay myLocation = new MyLocationOverlay(MainActivity.this, mapView);
                                                        myLocation.enableCompass(); //显示指南针
                                                        myLocation.enableMyLocation(); //显示我的位置
                                                        mapView.addOverlay(myLocation);
                                                        MapController mapController = new MapController(mapView);
                                                        mapController.animateTo(myLocation.getMyLocation());

                                                        mapView.addOverlay(mOverlay);
                                                        drawBottonVisual(true);

                                                        Button btn_add = DefaultButton(MainActivity.this);
                                                        linearLayout.addView(btn_add,0);
                                                        btn_add.setText("添加");
                                                        btn_add.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                XToast.normal(MainActivity.this,"add").show();
                                                                points.add(myLocation.getMyLocation());
                                                                mOverlay.setPoints(points);
                                                                mapView.invalidate();
                                                                System.out.println(points);
                                                            }
                                                        });
                                                    }
                                                    break;
                                                    case 1: {
                                                        points.clear();
                                                        mapView.removeOverlay(mOverlay);
                                                        mOverlay = new MyOverlayDrawGraph(MainActivity.this);
                                                        mOverlay.setDrawConfirm(1);
                                                        mOverlay.setDraw(2);
                                                        mapView.addOverlay(mOverlay);
                                                        drawBottonVisual(false);
                                                        final MyLocationOverlay myLocation = new MyLocationOverlay(MainActivity.this, mapView);
                                                        myLocation.enableCompass(); //显示指南针
                                                        myLocation.enableMyLocation(); //显示我的位置
                                                        mapView.addOverlay(myLocation);
                                                        MapController mapController = new MapController(mapView);
                                                        mapController.animateTo(myLocation.getMyLocation());

                                                        LinearLayout linearLayout = findViewById(R.id.ll_btn);
                                                        final Button btn_begin = DefaultButton(MainActivity.this);
                                                        btn_begin.setText("开始");
                                                        final GeoPoint[] geoPoint = {null};
                                                        final Handler handler = new Handler();
                                                        final Runnable runnable = new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                System.out.println(geoPoint[0]);
                                                                if(Math.sqrt(Math.pow(geoPoint[0].getLatitudeE6() - myLocation.getMyLocation().getLatitudeE6(),2)
                                                                        + Math.pow(geoPoint[0].getLongitudeE6() - myLocation.getMyLocation().getLongitudeE6(),2)) > 100){
                                                                    geoPoint[0] = myLocation.getMyLocation();
                                                                    points.add(geoPoint[0]);
                                                                    mOverlay.setPoints(points);
                                                                    mapView.invalidate();
                                                                    System.out.println(points);
                                                                }
                                                                mOverlay.setPoints(points);
                                                                mapView.invalidate();
                                                                System.out.println(points);System.out.println(points.size());
                                                                handler.postDelayed(this,2000);
                                                            }
                                                        };

                                                        btn_begin.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String name = (String) btn_begin.getText();
                                                                if (name == "开始"){
                                                                    btn_begin.setText("停止");
                                                                    if (geoPoint[0] == null){
                                                                        geoPoint[0] = myLocation.getMyLocation();
                                                                        points.add(geoPoint[0]);
                                                                        mOverlay.setPoints(points);
                                                                        mapView.invalidate();
                                                                        System.out.println(points);
                                                                    }
                                                                    handler.postDelayed(runnable,2000);
                                                                }else {
                                                                    btn_begin.setText("开始");
                                                                    handler.removeCallbacks(runnable);
                                                                }

                                                            }
                                                        });
                                                        linearLayout.addView(btn_begin,0);

                                                    }
                                                    break;
                                                }
                                            }
                                        })
                                        .build()
                                        .show();


                            }
                            break;

                        }
                        XToast.normal(getContext(), "tag:" + tag + ", content:" + itemView.toString()).show();
                    }
                }).build().show();
    }
    Button DefaultButton(Context context){
        Button btn_default = new XUIButton(context);
        btn_default.setBackground(getDrawable(R.drawable.rb_bg_selector));
        btn_default.setTextAppearance(R.style.Button_Radius);
        btn_default.setLayoutParams(new LinearLayout.LayoutParams(btn_default.getLayoutParams().WRAP_CONTENT,btn_default.getLayoutParams().WRAP_CONTENT));
        LinearLayout.LayoutParams  layoutParams = (LinearLayout.LayoutParams) btn_default.getLayoutParams();
        layoutParams.setMargins(20,20,20,20);
        btn_default.setLayoutParams(layoutParams);
        btn_default.setPaddingRelative(12,0,12,0);
        return btn_default;
    }

}
