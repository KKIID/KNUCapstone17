package kr.ac.knu.bist.wheather_parse.Connection.Location;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by BIST120 on 2017-05-22.
 */



public class LocationParse {
    private final String requst_url = "https://openapi.naver.com/v1/map/geocode?&query=";
    private String URL;
    private URL url;
    private HttpURLConnection conn;
    private BufferedReader reader;
    private JSONObject jsonObject;
    private searchBuffer search;
    private ArrayList<searchBuffer> searchResult;

    public ArrayList<searchBuffer> getLocation(String address) throws Exception{
        Log.d("TAG","주소:"+address);
        searchResult = new ArrayList<>();
        String addr = URLEncoder.encode(address,"UTF-8");
        URL = requst_url+addr;
        Log.d("TAG","URL:"+URL);
        url = new URL(URL);
        conn = (HttpURLConnection) url.openConnection();
        //conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Naver-Client-Id", "ew6Z1LJo5gxRQGKHrAXM");
        conn.setRequestProperty("X-Naver-Client-Secret","X9EB_rZkDm");
        int resCode = 0;
        resCode = conn.getResponseCode();
        if (resCode != HttpURLConnection.HTTP_OK) {//error
            Log.d("TAG","잘못된 주소 요청!");
            URL = requst_url;
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String input = null;
        StringBuffer sb = new StringBuffer();

        while ((input = reader.readLine()) != null) {
            sb.append(input);
            Log.d("TAG","In While문 :"+input);
        }
        jsonObject = new JSONObject(sb.toString());
        //Log.d("TAG","In  :"+jsonObject);

        int temp = jsonObject.getJSONObject("result").getJSONArray("items").length();
        JSONArray tempJsonArray=  jsonObject.getJSONObject("result").getJSONArray("items");
        // Log.d("TAG",jsonObject.getJSONObject("result").getJSONArray("items").length()+"");
        for(int i=0;i<temp;i++){
            search = new searchBuffer(tempJsonArray.getJSONObject(i).getJSONObject("point").getDouble("x"),tempJsonArray.getJSONObject(i).getJSONObject("point").getDouble("y")
                    ,tempJsonArray.getJSONObject(i).getString("address"));
            searchResult.add(search);
        }
        Log.d("TAG","URL1"+URL);
        URL = requst_url;
        Log.d("TAG","URL2"+URL);
        return searchResult;
    }
}

