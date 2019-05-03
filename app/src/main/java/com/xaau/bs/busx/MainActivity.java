package com.xaau.bs.busx;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xaau.bs.busx.adapter.DessertAdapter;
import com.xaau.bs.busx.util.AlipayUtil;
import com.xaau.bs.busx.util.SharedPreferenceUtil;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private AMapLocationClient aMapLocationClient;
    private TextView currentTV;
    //tool按钮
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        SharedPreferenceUtil.saveCity(MainActivity.this,"西安");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //login按钮点击
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.login_bar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferenceUtil.getIsLogin(MainActivity.this)){
                    Intent intent=new Intent(MainActivity.this,AccountCenterActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //滑动
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recList = (RecyclerView) findViewById(R.id.scrollview);
        recList.setLayoutManager(llm);
        DessertAdapter adapter = new DessertAdapter();
        recList.setAdapter(adapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && checkSelfPermission(permissions[3]) != PackageManager.PERMISSION_GRANTED) {
            showMissingPermissionDialog();
        }
    }
    //检查权限
    private void checkPermissions(){
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };
        if (checkSelfPermission(permissions[3]) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 0);
        }
    }
    //提示权限
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("缺少必要的存储权限");
        // 拒绝, 退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    //设置权限
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "使用BusX即可快速查询公交!https://...");
            startActivity(Intent.createChooser(sendIntent, "分享BusX"));
        }else if (id == R.id.byAlipay) {
            if (AlipayUtil.hasInstalledAlipayClient(this)){
                AlipayUtil.startAlipayClient(this,"   ");
            }else{
                Toast.makeText(this, "检测到未安装支付宝，无法打赏", Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.byPayPal) {
            WebView webView = new WebView(this);
            webView.loadUrl("   ");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stop) {
            Intent intent=new Intent(MainActivity.this,StopActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_line) {
            Intent intent=new Intent(MainActivity.this,LineActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_route) {
            Intent intent=new Intent(MainActivity.this,RouteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent=new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onCityClick(View v){
        currentTV=findViewById(R.id.tv_current);
//        currentTV.setText(SharedPreferenceUtil.getCity(MainActivity.this));
        CityPicker.from(MainActivity.this)
                .enableAnimation(true)
                .setAnimationStyle(R.style.DefaultCityPickerAnimation)
                .setLocatedCity(null)
               // .setHotCities(hotCities)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        currentTV.setText(String.format("%s", data.getName()));
                        SharedPreferenceUtil.saveCity(MainActivity.this,data.getName());
                        Toast.makeText(
                                getApplicationContext(),
                                String.format("已选择：%s %s", data.getName(), data.getProvince()),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLocate() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aMapLocationClient=new AMapLocationClient(getApplicationContext());
                                AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
                                aMapLocationClient.setLocationListener(aMapLocationListener);
                                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                                mLocationOption.setInterval(2000);
                                aMapLocationClient.setLocationOption(mLocationOption);
                                aMapLocationClient.startLocation();
                            }
                            AMapLocationListener aMapLocationListener=new AMapLocationListener() {
                                @Override
                                public void onLocationChanged(AMapLocation amapLocation) {
                                    if (amapLocation != null) {
                                        if (amapLocation.getErrorCode() == 0) {
                                            StringBuilder cityBuilder=new StringBuilder(amapLocation.getCity());
                                            StringBuilder provinceBuilder=new StringBuilder(amapLocation.getProvince());
                                            CityPicker.from(MainActivity.this).locateComplete(
                                                    new LocatedCity(cityBuilder.deleteCharAt(cityBuilder.length()-1).toString(),
                                                            provinceBuilder.deleteCharAt(provinceBuilder.length()-1).toString(),
                                                            amapLocation.getCityCode()), LocateState.SUCCESS);
                                        } else {
                                            CityPicker.from(MainActivity.this).locateComplete(
                                                    new LocatedCity(null,null,null),LocateState.FAILURE);
                                            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                                            Log.e("this","location Error, ErrCode:"
                                                    + amapLocation.getErrorCode() + ", errInfo:"
                                                    + amapLocation.getErrorInfo());
                                        }
                                        aMapLocationClient.stopLocation();
                                    }
                                }
                            };
                        }, 1500);
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(aMapLocationClient != null){
            aMapLocationClient.onDestroy();
        }
    }
}
