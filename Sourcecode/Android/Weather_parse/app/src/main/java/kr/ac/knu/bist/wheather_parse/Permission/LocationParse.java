package kr.ac.knu.bist.wheather_parse.Permission;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by BIST120 on 2017-05-22.
 */

public class LocationParse {
    private String URL = "https://openapi.naver.com/v1/map/geocode?&query=";
    private URL url;
    private HttpURLConnection conn;
    private BufferedReader reader;
    private JSONObject jsonObject;
    public void getLocation() throws Exception{
        String addr = URLEncoder.encode("불정로 6","UTF-8");
        URL = URL+addr;
        Log.d("TAG","URL:"+URL);
        url = new URL(URL);
        conn = (HttpURLConnection) url.openConnection();
        //conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Naver-Client-Id", "ew6Z1LJo5gxRQGKHrAXM");
        conn.setRequestProperty("X-Naver-Client-Secret","X9EB_rZkDm");

        int resCode = 0;
        resCode = conn.getResponseCode();

        if (resCode != HttpURLConnection.HTTP_OK) {

        }

        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String input = null;
        StringBuffer sb = new StringBuffer();

        while ((input = reader.readLine()) != null) {
            sb.append(input);
        }
        jsonObject = new JSONObject(sb.toString());
        Log.d("TAG","jsonObject"+jsonObject.getString("result"));
    }



}
