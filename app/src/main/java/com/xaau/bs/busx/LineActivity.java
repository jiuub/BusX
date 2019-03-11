package com.xaau.bs.busx;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.xaau.bs.busx.adapter.LineStationAdapter;
import com.xaau.bs.busx.domain.Station;
import com.xaau.bs.busx.util.GsonTools;
import com.xaau.bs.busx.util.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private String direction="line_go";
    private String busname="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (busname!=null&&!busname.isEmpty()){
                    switch (direction.length()){
                        case 7:direction="line_back";break;
                        case 9:direction="line_go";break;
                    }
                    getStation(direction,busname);
                }
                Log.e("this",direction);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_line, menu);
        MenuItem searchItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("this", "TextSubmit : " + query);
                fab.setVisibility(View.VISIBLE);
                getStation(direction,query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                busname=newText;
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 站点请求
     * @param action_flag
     * @param busname
     */
    private void getStation(final String action_flag, final String busname){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String,String> params=new HashMap<String, String>();
                    params.put("action_flag",action_flag);
                    params.put("busname",busname);
                    String s=HttpUtils.getJsonContent(params);
                    List<Station> lineStation=GsonTools.toList(s,Station.class);
                    List<List<Map<String,Object>>> lists=new ArrayList<List<Map<String, Object>>>();
                    for (int i=0;i<lineStation.size();i++){
                        Station item = lineStation.get(i);
                        params.put("action_flag","bus");
                        params.put("station",item.getSta_Name());
                        String s1=HttpUtils.getJsonContent(params);
                        lists.add(GsonTools.toListMaps(s1));
                    }
                    Log.e("this",lists.toString());
                    showStation(lineStation,lists);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 适配数据
     * @param response
     */
    private void showStation(final List<Station> response,final List bus){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recList = (RecyclerView) findViewById(R.id.station_view);
                recList.setLayoutManager(new LinearLayoutManager(LineActivity.this, LinearLayoutManager.VERTICAL, false));
                LineStationAdapter lineStationAdapter=new LineStationAdapter(response,bus);
                recList.setAdapter(lineStationAdapter);
                Log.e("this",response.toString());
                Log.e("this",bus.toString());
            }
        });
    }
}
