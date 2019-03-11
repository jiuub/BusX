package com.xaau.bs.busx.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;




public class HttpUtils {
    private static URL jsonUrl , loginUrl;
    static{
        try {
            jsonUrl=new URL("http://192.168.123.201:8080/BusX_server_war_exploded/servlet/JsonServlet");
            loginUrl=new URL("http://192.168.123.201:8080/BusX_server_war_exploded/servlet/LoginServlet");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param params url参数
     * @return
     */
    public static String sendPostMessage(Map<String,String> params){
        StringBuilder builder=new StringBuilder();
        try {
            if(params!=null&&!params.isEmpty()){
                for (Map.Entry<String,String> entry:params.entrySet()){
                    builder.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(),"utf-8"))
                            .append("&");
                }
                builder.deleteCharAt(builder.length()-1);
            }
            HttpURLConnection urlConnection=(HttpURLConnection)loginUrl.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            byte[] mydata=builder.toString().getBytes();
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length",String.valueOf(mydata.length));
            OutputStream outputStream=urlConnection.getOutputStream();
            outputStream.write(mydata);
            int responseCode=urlConnection.getResponseCode();
            if (responseCode==200){
                return changeInputStream(urlConnection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String changeInputStream(InputStream inputStream) {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        byte[] date=new byte[1024];
        int len=0;
        String result="";
        if (inputStream!=null){
            try {
                while ((len=inputStream.read(date))!=-1){
                    outputStream.write(date,0,len);
                }
                result = new String(outputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public static String getJsonContent (Map<String,String> params){
        try {
            StringBuilder builder=new StringBuilder();
            if(params!=null&&!params.isEmpty()){
                for (Map.Entry<String,String> entry:params.entrySet()){
                    builder.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(),"utf-8"))
                            .append("&");
                }
                builder.deleteCharAt(builder.length()-1);
            }
            HttpURLConnection connection=(HttpURLConnection)jsonUrl.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            byte[] mydata=builder.toString().getBytes();
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",String.valueOf(mydata.length));
            OutputStream outputStream=connection.getOutputStream();
            outputStream.write(mydata);
            int code=connection.getResponseCode();
            System.out.println(code);
            if (code == 200) {
                return changeInputStream(connection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
