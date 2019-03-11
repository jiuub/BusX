package com.xaau.bs.busx.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonTools {

    /**
     * 将json转化成bean
     * @param json
     * @param cls
     * @return
     */
    public static <T> T toBean(String json,Class<T> cls){
        T t=null;
        Gson gson=new Gson();
        t=gson.fromJson(json,cls);
        return t;
    }
    /**
     * 把json 转化成list
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String jsonString,Class<T> cls){
        Gson gson=new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(jsonString).getAsJsonArray();
        for(final JsonElement elem : array){
            list.add(gson.fromJson(elem, cls));
        }
        return list ;
    }

    /**
     * 把json 转化成list<Map>
     * @param jsonString
     * @return
     */
    public static <T> List<Map<String,T>> toListMaps(String jsonString){
        List<Map<String,T>> list=new ArrayList<Map<String, T>>();
        try{
            Gson gson=new Gson();
            list=gson.fromJson(jsonString,
                    new TypeToken<List<Map<String,T>>>(){
                    }.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 把json 转化成Map
     * @param jsonString
     * @param <T>
     * @return
     */
    public static <T> Map<String,T> toMaps(String jsonString){
        Map<String,T> map=new HashMap<String, T>();
        Gson gson=new Gson();
        map=gson.fromJson(jsonString,
                new TypeToken<Map<String,T>>(){
                }.getType());
        return map;
    }
}
