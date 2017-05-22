package kr.ac.knu.bist.wheather_parse.DataRequest;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by BIST120 on 2017-05-21.
 */

public class weatherParse {
    private String lat;
    private String lon;
    private String URL = "http://apis.skplanetx.com/weather/current/hourly?version={version}&lat={lat}&lon={lon}";
    private String URL2 = "http://apis.skplanetx.com/weather/dust?version={version}&lat={lat}&lon={lon}";
    private String URL3 =  "http://apis.skplanetx.com/gweather/current?version={version}&lat={lat}&lon={lon}";
    private JSONObject jsonObject;
    private java.net.URL url,url2,url3;
    private HttpURLConnection conn,conn2;
    private BufferedReader reader = null;
    private ArrayList<String> weatherState;
    private ArrayList<String> airCondition;
    private ArrayList<String> sunState;

    public weatherParse(String lat/*위도*/, String lon/*경도*/) {
        this.lat = lat;
        this.lon = lon;
        URL = URL.replace("{version}", "1");
        URL = URL.replace("{lat}", lat);
        URL = URL.replace("{lon}", lon);
        URL2 = URL2.replace("{version}", "1");
        URL2 = URL2.replace("{lat}", lat);
        URL2 = URL2.replace("{lon}", lon);
        URL3 = URL3.replace("{version}", "1");
        URL3 = URL3.replace("{lat}", lat);
        URL3 = URL3.replace("{lon}", lon);
        Log.d("TAG",URL3);
    }
    public ArrayList<String> getSunsetrise()throws Exception{
        sunState = new ArrayList<>();
        url3 = new URL(URL3);
        conn = (HttpURLConnection) url3.openConnection();
        //conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("appKey", "d516030a-f736-3cbe-bf29-21247b5e7dd7");

        int resCode = 0;
        resCode = conn.getResponseCode();

        if (resCode != HttpURLConnection.HTTP_OK) {
            return null;
        }

        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String input = null;
        StringBuffer sb = new StringBuffer();

        while ((input = reader.readLine()) != null) {
            sb.append(input);
            jsonObject = new JSONObject(input);
        }
        jsonObject = jsonObject.getJSONObject("gweather").getJSONArray("current").getJSONObject(0).getJSONObject("sun");
        String riseTime = jsonObject.getString("rise");
        NotiDate noti = new NotiDate(riseTime);
        sunState.add(noti.getTime());/*ex) "rise":"2014-05-18T20:19Z", "set":"2014-05-19T10:37Z"*/
        String setTime = jsonObject.getString("set");
        noti = new NotiDate(setTime);
        sunState.add(noti.getTime());
        Log.d("TAG","getSunState");
        return sunState;
    }

    public ArrayList<String> getAirCondition() throws Exception {
        Log.d("TAG","getAirCondition");
        airCondition = new ArrayList<>();
        url2 = new URL(URL2);
        conn = (HttpURLConnection) url2.openConnection();
        //conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("appKey", "d516030a-f736-3cbe-bf29-21247b5e7dd7");

        int resCode = 0;
        resCode = conn.getResponseCode();

        if (resCode != HttpURLConnection.HTTP_OK) {
            return null;
        }

        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String input = null;
        StringBuffer sb = new StringBuffer();

        while ((input = reader.readLine()) != null) {
            sb.append(input);
            jsonObject = new JSONObject(input);
        }
        jsonObject = jsonObject.getJSONObject("weather").getJSONArray("dust").getJSONObject(0).getJSONObject("pm10");
        airCondition.add(jsonObject.getString("value"));/*농도(㎍/㎥) - 0~30: 좋음, 31~80: 보통, 81~120: 약간나쁨, 121~200: 나쁨, 201~300: 매우나쁨*/
        airCondition.add(jsonObject.getString("grade"));/*등급 - 좋음, 보통, 약간나쁨, 나쁨, 매우나쁨*/
        Log.d("TAG","getAirCondition");
        return airCondition;
    }

    public ArrayList<String> getWeather() throws Exception {
        weatherState = new ArrayList<>();
        url = new URL(URL);
        conn = (HttpURLConnection) url.openConnection();
        //conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("appKey", "d516030a-f736-3cbe-bf29-21247b5e7dd7");

        int resCode = 0;
        resCode = conn.getResponseCode();

        if (resCode != HttpURLConnection.HTTP_OK) {
            return null;
        }

        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String input = null;
        StringBuffer sb = new StringBuffer();

        while ((input = reader.readLine()) != null) {
            sb.append(input);
            jsonObject = new JSONObject(input);
        }
        jsonObject = jsonObject.getJSONObject("weather").getJSONArray("hourly").getJSONObject(0);
        weatherState.add(jsonObject.getJSONObject("wind").getString("wdir"));/*풍향*/
        weatherState.add(jsonObject.getJSONObject("wind").getString("wspd"));/*풍속*/
        weatherState.add(jsonObject.getJSONObject("precipitation").getString("type"));/*강수형태코드 - 0: 현상없음 - 1: 비 - 2: 비/눈 - 3: 눈*/
        weatherState.add(jsonObject.getJSONObject("precipitation").getString("sinceOntime"));/*1시간 누적 강수량 - if type=0/1/2 → 강우량 (mm) - if type=3     → 적설량 (cm)*/
        weatherState.add(jsonObject.getJSONObject("sky").getString("name"));
            /*하늘상태코드명
            - SKY_O01: 맑음
            - SKY_O02: 구름조금
            - SKY_O03: 구름많음
            - SKY_O04: 구름많고 비
            - SKY_O05: 구름많고 눈
            - SKY_O06: 구름많고 비 또는 눈
            - SKY_O07: 흐림
            - SKY_O08: 흐리고 비
            - SKY_O09: 흐리고 눈
            - SKY_O10:  흐리고 비 또는 눈
            - SKY_O11: 흐리고 낙뢰
            - SKY_O12: 뇌우, 비
            - SKY_O13: 뇌우, 눈
            - SKY_O14: 뇌우, 비 또는 눈*/
        weatherState.add(jsonObject.getJSONObject("temperature").getString("tc"));/*1시간 현재기온*/
        weatherState.add(jsonObject.getJSONObject("temperature").getString("tmax"));/*오늘의 최고기온*/
        weatherState.add(jsonObject.getJSONObject("temperature").getString("tmin"));/*오늘의 최저기온*/
        weatherState.add(jsonObject.getString("humidity"));/*상대 습도*/
        weatherState.add(jsonObject.getString("lightning"));/*낙뢰 유무*/
        weatherState.add(jsonObject.getString("timeRelease"));/*발표 시간*/
        return weatherState;
    }
    class NotiDate {
        int year;
        int month;
        int date;
        int hour;
        int min;
        int sec;


        public NotiDate(String s) {
            setTime(s);
            Calendar cal = Calendar.getInstance();
            cal.set(year,month,date,hour,min,sec);
            cal.add(Calendar.HOUR_OF_DAY,9);
            setTime(cal);
        }

        public void setTime(String s) {
            year = Integer.parseInt(s.substring(0,4));
            month = Integer.parseInt(s.substring(5,7));
            date = Integer.parseInt(s.substring(8,10));
            hour = Integer.parseInt(s.substring(11,13));
            min = Integer.parseInt(s.substring(14,16));
        }
        public void setTime(Calendar cal) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            date = cal.get(Calendar.DATE);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            min = cal.get(Calendar.MINUTE);
        }

        public String getDay() {
            return String.format("%04d-%02d-%02d",year,month,date);
        }

        public String getTime() {
            return String.format("%02d:%02d",hour,min);
        }

        public String toString() {
            return getDay() + " " +getTime();
        }
    }


}


