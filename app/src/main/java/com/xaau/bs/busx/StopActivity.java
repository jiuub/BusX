package com.xaau.bs.busx;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xaau.bs.busx.adapter.LineBusAdapter;
import com.xaau.bs.busx.domain.Bus;
import com.xaau.bs.busx.util.GsonTools;
import com.xaau.bs.busx.util.HttpUtils;
import com.xaau.bs.busx.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopActivity extends AppCompatActivity {

    private RecyclerView recList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        recList = (RecyclerView) findViewById(R.id.bus_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop, menu);
        MenuItem searchItem=menu.findItem(R.id.action_search);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("this", "TextStation : " + query);
                getBus(query, SharedPreferenceUtil.getCity(StopActivity.this));
                searchView.clearFocus();
                recList.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void getBus(final String station,final String city){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,String> params=new HashMap<String, String>();
                params.put("action_flag","bus");
                params.put("station",station);
                params.put("buscity",city);
                String s= HttpUtils.getJsonContent(params);
                List<Bus> listBus=GsonTools.toList(s,Bus.class);

                List<Bus> buses=new ArrayList<Bus>();
                for (int i=0;i<listBus.size();i++){
                    Bus bus=listBus.get(i);
                    Map<String,String> param=new HashMap<String, String>();
                    param.put("action_flag","bus_detail");
                    param.put("busname",bus.getBusName());
                    param.put("buscity",city);
                    String s1=HttpUtils.getJsonContent(param);
                    Log.e("this",s1);
                    buses.add(GsonTools.toBean(s1,Bus.class));
                }
                Log.e("this",buses.toString());
                showBus(buses);
            }
        }).start();
    }
    private void showBus(final List bus){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recList.setLayoutManager(new LinearLayoutManager(StopActivity.this, LinearLayoutManager.VERTICAL, false));
                if (bus!=null&&!bus.isEmpty()){
                    LineBusAdapter lineBusAdapter=new LineBusAdapter(bus);
                    recList.setAdapter(lineBusAdapter);
                }else {
                    LineBusAdapter lineBusAdapter=new LineBusAdapter();
                    recList.setAdapter(lineBusAdapter);
                }

            }
        });
    }
}
