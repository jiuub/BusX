package com.xaau.bs.busx.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xaau.bs.busx.MainActivity;
import com.xaau.bs.busx.domain.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceUtil {

    public static void saveMessage(Context context,String email,boolean isLogin){
        SharedPreferences sharedPreferences=context.getSharedPreferences("userinfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("email",email);
        editor.putBoolean("isLogin",isLogin);
        editor.apply();
    }

    public static Map<String,Object> getMessage(Context context){
        Map<String,Object> map=new HashMap<String, Object>();
        SharedPreferences sharedPreferences=context.getSharedPreferences("userinfo",Context.MODE_PRIVATE);
        String email=sharedPreferences.getString("email","");
        boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
        map.put("email",email);
        map.put("isLogin",isLogin);
        return map;
    }

    /**
     * email
     * @param context
     * @return
     */
    public static String getEmail(Context context){
        Map<String,Object> map=SharedPreferenceUtil.getMessage(context);
        Gson gson=new Gson();
        return GsonTools.toBean(gson.toJson(map), UserInfo.class).getEmail();
    }

    public static boolean getIsLogin(Context context){
        Map<String,Object> map= SharedPreferenceUtil.getMessage(context);
        Gson gson=new Gson();
        return GsonTools.toBean(gson.toJson(map), UserInfo.class).isLogin();
    }
}
