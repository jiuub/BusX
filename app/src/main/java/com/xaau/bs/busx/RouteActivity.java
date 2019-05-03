package com.xaau.bs.busx;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
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
import com.amap.api.maps.MapView;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.xaau.bs.busx.adapter.BusResultListAdapter;
import com.xaau.bs.busx.util.SharedPreferenceUtil;
import com.xaau.bs.busx.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static com.amap.api.services.route.RouteSearch.BUS_LEASE_WALK;


public class RouteActivity extends AppCompatActivity {
    private MapView mMapView;
    private AMap aMap;
    private AMapLocationClient aMapLocationClient;
    private RouteSearch routeSearch;

    private BusRouteResult mBusRouteResult;
    private List<Tip> autoTips;

    private LatLonPoint latLonPoint_start,latLonPoint_end;
    private String city= "";

    private ProgressDialog progDialog=null;
    private LinearLayout mBusResultLayout;

    private ListView mBusResultList;
    private AutoCompleteTextView deptText,desText;

    private boolean isFirstInput=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.route_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        init();
        loc();
    }

    private void init(){
        if(aMap==null){
            aMap=mMapView.getMap();
        }

        routeSearch =new RouteSearch(this);
        routeSearch.setRouteSearchListener(onRouteSearchListener);

        mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
        mBusResultList = (ListView) findViewById(R.id.bus_result_list);
        deptText=(AutoCompleteTextView)findViewById(R.id.text_dept);
        desText=(AutoCompleteTextView)findViewById(R.id.text_des) ;

        city=SharedPreferenceUtil.getCity(RouteActivity.this);

        deptText.addTextChangedListener(textWatcher);
        deptText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (autoTips!=null&&autoTips.size()>position){
                    Tip tip=autoTips.get(position);
                    try{
                        latLonPoint_start=tip.getPoint();
                        deptText.clearFocus();
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
                        hideKeyBoard(desText);
                        desText.clearFocus();
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
            Snackbar.make(view, "定位失败,请手动输入", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if (latLonPoint_end == null || TextUtils.isEmpty(desText.getText().toString())){
            Snackbar.make(view, "终点为空", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if (latLonPoint_start.equals(latLonPoint_end)){
            Snackbar.make(view, "出发地与目的地相同", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            getRoute(city);
            mBusResultLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onResetClick(View view) {
        hideKeyBoard(view);
        deptText.setText(null);
        latLonPoint_start = null;
        loc();
        desText.setText(null);
        latLonPoint_end = null;

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
            dismissProgressDialog();
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
                        Toast.makeText(RouteActivity.this,"当前位置不能通过公交车前往目的地",Toast.LENGTH_LONG).show();
                        mBusResultLayout.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("this","路径规划noResult");
                    ToastUtil.show(RouteActivity.this, "没有数据");
                }
            } else {
                Log.e("this","onBusRouteSearched: "+errorCode);
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
        if (progDialog == null){
            progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(true);
            progDialog.setMessage("正在搜索...");
            progDialog.show();
        }
    }
    //隐藏进度框
    private void dismissProgressDialog() {
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
        aMapLocationClient=new AMapLocationClient(getApplicationContext());
        aMapLocationClient.setLocationListener(aMapLocationListener);
        aMapLocationClient.setLocationOption(locOption());
        aMapLocationClient.startLocation();
    }
    //定位参数
    private AMapLocationClientOption locOption(){
        AMapLocationClientOption locOption=new AMapLocationClientOption();
        locOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locOption.setInterval(2000);
        locOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
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
                    Log.e("this",latLonPoint_start+"");
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("this", aMapLocation.getErrorCode()+"");
                }
            }
            aMapLocationClient.stopLocation();
        }
    };

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
