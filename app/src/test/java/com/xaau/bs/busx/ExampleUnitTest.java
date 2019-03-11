package com.xaau.bs.busx;

import android.content.Context;

import com.xaau.bs.busx.domain.Bus;
import com.xaau.bs.busx.domain.Station;
import com.xaau.bs.busx.util.GsonTools;
import com.xaau.bs.busx.util.HttpUtils;
import com.xaau.bs.busx.util.SharedPreferenceUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private Context context;

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void http_isCorrect(){
        Map<String,String> params=new HashMap<String, String>();
        params.put("action_flag","line_go");
        params.put("busname","611路");
        String r=HttpUtils.getJsonContent(params);
        List<Station> list= GsonTools.toList(r,Station.class);
        System.out.println(list.toString());
//        List<List<Bus>> lists=new ArrayList<List<Bus>>();
//        List <Bus> listb=new ArrayList<Bus>();
//        for (int i=0;i<list.size();i++){
//            Station item = list.get(i);
//            params.put("action_flag","bus");
//            params.put("station",item.getSta_Name());
//            String r1=HttpUtils.getJsonContent(params,"utf-8");
//            //System.out.println(r1);
//            lists.add(GsonTools.toList(r1,Bus.class));
//        }
//        System.out.println(lists.toString());
//        for (int i=0;i<lists.size();i++){
////            listb=lists.get(i);
//            //System.out.println(listb.toString());
//            for (int y=0;y<lists.get(i).size();y++){
//                listb.add(lists.get(i).get(y));
//                System.out.println(lists.get(i).get(y).getBusName());
//            }
//        }
//
//        System.out.println(listb.get(20).getBusName());
//       // System.out.println(lists.get(4).get(1).getBusName());

    }

    @Test
    public void sendPostMessageTest(){
        Map<String,String> params=new HashMap<String, String>();
        params.put("username","123@123.com");
        params.put("password","123456789");
        params.put("sign","in");
        String s=HttpUtils.sendPostMessage(params);
        System.out.println(s);
    }
}