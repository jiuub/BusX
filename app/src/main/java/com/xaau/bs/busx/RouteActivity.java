package com.xaau.bs.busx;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.xaau.bs.busx.adapter.BusResultListAdapter;
import com.xaau.bs.busx.util.AMapUtil;
import com.xaau.bs.busx.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static com.amap.api.services.route.RouteSearch.BUS_LEASE_WALK;


public class RouteActivity extends Activity {
    private MapView mMapView;
    private AMap aMap;
    private AMapLocationClient aMapLocationClient;
    private GeocodeSearch geocodeSearch;
    private RouteSearch routeSearch;
    private BusStationSearch busStationSearch;

    private BusRouteResult mBusRouteResult;
    private List<Tip> autoTips;

    private LatLng lng;
    private LatLonPoint latLonPoint_start,latLonPoint_end;
    private String addressName,city;

    private ProgressDialog progDialog=null;
    private LinearLayout mBusResultLayout;
//    private RelativeLayout mBottomLayout;
//    private TextView mRotueTimeDes, mRouteDetailDes;
    private ListView mBusResultList;
    private FloatingActionButton loc;
    private AutoCompleteTextView deptText,desText;

    private boolean isFirstInput=true;
    private boolean isFirstCenter=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.route_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        if(aMap==null){
            aMap=mMapView.getMap();
            locDoc();
            locClick();
        }
        geocodeSearch=new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(onGeocodeSearchListener);

        routeSearch =new RouteSearch(this);
        routeSearch.setRouteSearchListener(onRouteSearchListener);

        aMapLocationClient=new AMapLocationClient(getApplicationContext());
        aMapLocationClient.setLocationListener(aMapLocationListener);

//        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
//        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
//        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
        mBusResultList = (ListView) findViewById(R.id.bus_result_list);
        deptText=(AutoCompleteTextView)findViewById(R.id.text_dept);
        desText=(AutoCompleteTextView)findViewById(R.id.text_des) ;

        deptText.addTextChangedListener(textWatcher);
        deptText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (autoTips!=null&&autoTips.size()>position){
                    Tip tip=autoTips.get(position);
                    try{
                        latLonPoint_start=tip.getPoint();
                        aMap.moveCamera(CameraUpdateFactory.newLatLng(AMapUtil.convertToLatLng(latLonPoint_start)));
                        aMap.addMarker(new MarkerOptions().position(lng).title("我")).showInfoWindow();
                        aMap.addMarker(new MarkerOptions().position(AMapUtil.convertToLatLng(latLonPoint_start)).title("出发地")).showInfoWindow();
                        hideKeyBoard(deptText);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        desText.addTextChangedListener(textWatcher);
        desText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(autoTips!=null&&autoTips.size()>position){
                    Tip tip=autoTips.get(position);
                    try{
                        latLonPoint_end=tip.getPoint();
                        aMap.moveCamera(CameraUpdateFactory.newLatLng(AMapUtil.convertToLatLng(latLonPoint_end)));
                        aMap.addMarker(new MarkerOptions().position(lng).title("我")).showInfoWindow();
                        aMap.addMarker(new MarkerOptions().position(AMapUtil.convertToLatLng(latLonPoint_end)).title("目的地")).showInfoWindow();
                        hideKeyBoard(desText);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    //导航按钮
    public void onDriveClick(View view) {
        hideKeyBoard(view);
        if (latLonPoint_start == null) {
            Snackbar.make(view, "起点为空", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            loc();
            deptText.setText(addressName);
            aMap.addMarker(new MarkerOptions().position(lng).title("我")).showInfoWindow();
        }else if (latLonPoint_end == null){
            Snackbar.make(view, "终点为空", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            getRoute(city);
            loc.hide();
            mMapView.setVisibility(View.GONE);
            mBusResultLayout.setVisibility(View.VISIBLE);
        }
    }



    //定位蓝点
    private void locDoc(){
        MyLocationStyle myLocationStyle=new MyLocationStyle();
       // myLocationStyle.interval(5000);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        myLocationStyle.strokeWidth(5);
        myLocationStyle.strokeColor(Color.argb(180, 3, 145, 255));
        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMyLocationChangeListener(onMyLocationChangeListener);
    }
    AMap.OnMyLocationChangeListener onMyLocationChangeListener=new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (location!=null){
                lng=new LatLng(location.getLatitude(),location.getLongitude());
                if (isFirstCenter){
                    LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    getAddress(AMapUtil.convertToLatLonPoint(latLng));
                    isFirstCenter=false;
                }
            }
        }
    };



    private void locClick(){
        //定位按钮 Marker
        loc = (FloatingActionButton) findViewById(R.id.btn_loc);
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("this","loc");
                String[] permissions = {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                };
                if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 0);
                }
                aMap.animateCamera(CameraUpdateFactory.newLatLng(lng));
                List<Marker> mAllMarker = aMap.getMapScreenMarkers();
//                for (int i=0;i<mAllMarker.size();i++){
//                    markers[i]=mAllMarker.get(i);
//                }
                Marker[] markers=mAllMarker.toArray(new Marker[2]);
                for (int i=1;i<markers.length;i++){
                   if (markers[i]!=null){
                       markers[i].remove();
                    }
                }
                desText.setText(null);
                latLonPoint_end=null;
                aMap.addMarker(new MarkerOptions().position(lng).title("我")).showInfoWindow();
                deptText.setText(addressName);
                if (latLonPoint_start!=null){
                    latLonPoint_start=AMapUtil.convertToLatLonPoint(lng);
                }
            }
        });
    }



    //输入内容自动提示
    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newText=s.toString().trim();
            if(newText.length()>0){
                InputtipsQuery inputtipsQuery=new InputtipsQuery(newText,city);
                inputtipsQuery.setCityLimit(true);
                Inputtips inputtips=new Inputtips(RouteActivity.this,inputtipsQuery);
                inputtips.setInputtipsListener(inputtipsListener);
                inputtips.requestInputtipsAsyn();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    Inputtips.InputtipsListener inputtipsListener=new Inputtips.InputtipsListener() {
        @Override
        public void onGetInputtips(List<Tip> list, int code) {
            if (code==AMapException.CODE_AMAP_SUCCESS){
                autoTips=list;
                List<String> stringList=new ArrayList<String>();
                for (int i=0;i<list.size();i++){
                    stringList.add(list.get(i).getName());
                }
                ArrayAdapter<String> aAdapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.item_autotext,stringList);
                deptText.setAdapter(aAdapter);
                desText.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();
                if(isFirstInput){
                    isFirstInput=false;
                    deptText.showDropDown();
                    desText.showDropDown();
                }
            }
        }
    };



    //坐标转地址
    private void getAddress(LatLonPoint latLonPoint){
        RegeocodeQuery regeocodeQuery=new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);
    }
    //地址转坐标
    private void getLatLon(String address,String city){
        GeocodeQuery geocodeQuery=new GeocodeQuery(address,city);
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
    }
    GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener=new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
            //获取地址描述信息
            if(rCode== AMapException.CODE_AMAP_SUCCESS){
                if(regeocodeResult!=null&&regeocodeResult.getRegeocodeAddress()!=null
                        &&regeocodeResult.getRegeocodeAddress().getFormatAddress()!=null){
                    addressName=regeocodeResult.getRegeocodeAddress().getFormatAddress()+"附近";
                    city=regeocodeResult.getRegeocodeAddress().getCity();
                }
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
            //获取坐标信息
        }
    };



    //公交站点查询
    private void getBusStop(String station,String citycode){
        BusStationQuery busStationQuery=new BusStationQuery(station, citycode);
        busStationSearch=new BusStationSearch(this,busStationQuery);
        busStationSearch.setOnBusStationSearchListener(onBusStationSearchListener);
        busStationSearch.searchBusStationAsyn();
    }
    BusStationSearch.OnBusStationSearchListener onBusStationSearchListener=new BusStationSearch.OnBusStationSearchListener() {
        @Override
        public void onBusStationSearched(BusStationResult busStationResult, int i) {
            //获取公交站点信息
        }
    };



    //路径规划
    private void getRoute(String city){
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(latLonPoint_start, latLonPoint_end);
        RouteSearch.BusRouteQuery busRouteQuery = new RouteSearch.BusRouteQuery(fromAndTo, BUS_LEASE_WALK, city, 0);
        routeSearch.calculateBusRouteAsyn(busRouteQuery);
        Log.e("this","路径规划start");

    }
    RouteSearch.OnRouteSearchListener onRouteSearchListener=new RouteSearch.OnRouteSearchListener() {
        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {
            dissmissProgressDialog();
//            mBottomLayout.setVisibility(View.GONE);
            if (errorCode == 1000) {
                if (busRouteResult != null && busRouteResult.getPaths() != null) {
                    Log.e("this","路径规划Result");
                    if (busRouteResult.getPaths().size() > 0) {
                        Log.e("this","路径规划Paths");
                        aMap.clear();// 清理地图上的所有覆盖物
                        mBusRouteResult = busRouteResult;
                        BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(RouteActivity.this, mBusRouteResult);
                        mBusResultList.setAdapter(mBusResultListAdapter);
                    } else {
                        Log.e("this","路径规划noPaths");
                        Toast.makeText(RouteActivity.this,"公交不可到达",Toast.LENGTH_LONG).show();
                        mBusResultLayout.setVisibility(View.GONE);
                        loc.show();
                        mMapView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("this","路径规划noResult");
                    ToastUtil.show(RouteActivity.this, "没有数据");
                }
            } else {
                Log.v("this",errorCode+"");
                ToastUtil.showerror(RouteActivity.this, errorCode);
            }
        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }
    };



    //显示进度框
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索...");
        progDialog.show();
    }
    //隐藏进度框
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    //收起键盘
    private void hideKeyBoard(View view){
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view,InputMethodManager.SHOW_FORCED);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }



    //定位
    public void loc(){
        aMapLocationClient.setLocationOption(locOption());
        if (aMapLocationClient!=null){
            aMapLocationClient.stopLocation();
            aMapLocationClient.startLocation();
        }else {
            aMapLocationClient.startLocation();
        }
    }
    //定位参数
    private AMapLocationClientOption locOption(){
        AMapLocationClientOption locOption=new AMapLocationClientOption();
        locOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        locOption.setInterval(2121212121);
        return locOption;
    }
    //定位监听
    AMapLocationListener aMapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    latLonPoint_start=new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    Log.v("this",latLonPoint_start+"");
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.v("this", aMapLocation.getErrorCode()+"");
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("BusX").setMessage("提示").setIcon(R.drawable.route_bus_normal).setCancelable(false)
                .setPositiveButton("返回地图", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBusResultLayout.setVisibility(View.GONE);
                        loc.show();
                        mMapView.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        if(aMapLocationClient != null){
            aMapLocationClient.onDestroy();
        }
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
