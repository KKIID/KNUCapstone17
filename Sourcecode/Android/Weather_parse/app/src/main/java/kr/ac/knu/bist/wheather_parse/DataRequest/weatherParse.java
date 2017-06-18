package kr.ac.knu.bist.wheather_parse.DataRequest;

import android.graphics.Bitmap;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import kr.ac.knu.bist.wheather_parse.R;

/**
 * Created by BIST120 on 2017-05-21.
 */

public class weatherParse implements Serializable{
    private String lat;
    private String lon;
    private final String appKey = "829e250e-cab9-350e-a03d-2f1ee3480b87";
    private final String URL0 = "http://apis.skplanetx.com/weather/current/hourly?version={version}&lat={lat}&lon={lon}";/*시간별날씨*/
    private final String URL1 = "http://apis.skplanetx.com/weather/dust?version={version}&lat={lat}&lon={lon}";/*미세먼지*/
    private final String URL2 =  "http://apis.skplanetx.com/gweather/current?version={version}&lat={lat}&lon={lon}";/*일출일몰*/
    private final String URL3 = "http://apis.skplanetx.com/weather/severe/alert?version={version}&lat={lat}&lon={lon}";/*기상특보*/
    private final String URL4 = "http://apis.skplanetx.com/weather/windex/laundry?version={version}&lat={lat}&lon={lon}";/*빨래지수*/
    private final String URL5 = "http://apis.skplanetx.com/weather/windex/uvindex?version={version}&lat={lat}&lon={lon}";/*자외선*/
    private final String URL6 = "http://apis.skplanetx.com/weather/windex/wctindex?version={version}&lat={lat}&lon={lon}";/*체감온도*/
    private final String URL7 = "http://apis.skplanetx.com/weather/windex/thindex?version={version}&lat={lat}&lon={lon}";/*불쾌지수*/
    private final String URL8 = "http://apis.skplanetx.com/weather/windex/sdindex?version={version}&lat={lat}&lon={lon}";/*피부질환가능지수*/
    private String[] URLs = null;
    private HttpURLConnection conn;
    private BufferedReader reader = null;


    public weatherParse(String lat/*위도*/, String lon/*경도*/) {
        Log.d("TAG","weather parse 위도 경도"+lat+"/"+lon);
        if(lat==null || lat.equals("")){/*어플 첫실행 시 위치가 없을 때 사용.*/
            lat = "37.56667";
        }
        if(lon==null || lon.equals("")){
            lon = "126.97806";
        }

        Log.d("TAG","weather parse 위도 경도"+lat+"/"+lon);
        this.lat = lat;
        this.lon = lon;

        URLs = new String[9];
        URLs[0] = URL0;URLs[1] = URL1;URLs[2] = URL2;URLs[3] = URL3;URLs[4] = URL4;URLs[5] = URL5;URLs[6] = URL6;URLs[7] = URL7;URLs[8] = URL8;
        for(int i=0; i<URLs.length;i++){
            URLs[i] = URLs[i].replace("{version}", "1");
            URLs[i] = URLs[i].replace("{lat}", lat);
            URLs[i] = URLs[i].replace("{lon}", lon);
            Log.d("TAG","url : "+URLs[i]);
        }

        Log.d("TAG",URLs[3]);
    }
    /*빨래 지수를 불러온다*/
    public ArrayList<String> getLaundary() throws Exception{
        ArrayList<String> LaundaryState = new ArrayList<>();
        JSONObject jsonObject = startJsonParsing(4);

        jsonObject = jsonObject.getJSONObject("weather").getJSONObject("wIndex").getJSONArray("laundry").getJSONObject(0).getJSONObject("day00");
        LaundaryState.add(jsonObject.getString("comment"));
        Log.d("TAG",jsonObject.getString("comment"));
        return LaundaryState;
    }
    /*자외선 지수를 불러온다*/
    public ArrayList<String> getUltraViolet() throws Exception{
        ArrayList<String> UltraViolet = new ArrayList<>();
        JSONObject jsonObject = startJsonParsing(5);

        jsonObject = jsonObject.getJSONObject("weather").getJSONObject("wIndex").getJSONArray("uvindex").getJSONObject(0).getJSONObject("day00");
        UltraViolet.add(jsonObject.getString("comment"));
        Log.d("TAG",jsonObject.getString("comment"));
        return UltraViolet;
    }
    /*체감 온도를 불러온다*/
    public ArrayList<String> getFeelTemp() throws Exception{
        ArrayList<String> FeelTemp = new ArrayList<>();
        JSONObject jsonObject = startJsonParsing(6);

        jsonObject = jsonObject.getJSONObject("weather").getJSONObject("wIndex").getJSONArray("wctIndex").getJSONObject(0).getJSONObject("current");
        FeelTemp.add(jsonObject.getString("index"));
        Log.d("TAG",jsonObject.getString("index"));
        return FeelTemp;
    }
    /*불쾌 지수를 불러온다*/
    public ArrayList<String> getDiscomfort() throws Exception{
        ArrayList<String> Discomfort = new ArrayList<>();
        JSONObject jsonObject = startJsonParsing(7);

        jsonObject = jsonObject.getJSONObject("weather").getJSONObject("wIndex").getJSONArray("thIndex").getJSONObject(0).getJSONObject("current");
        Discomfort.add(jsonObject.getString("index"));
        Log.d("TAG",jsonObject.getString("index"));
        return Discomfort;

    }
    /*피부 질환 가능 지수를 불러온다*/
    public ArrayList<String> getSkinProblem() throws Exception{
        ArrayList<String> SkinProblem = new ArrayList<>();
        JSONObject jsonObject = startJsonParsing(8);
        jsonObject = jsonObject.getJSONObject("weather").getJSONObject("wIndex").getJSONArray("sdindex").getJSONObject(0).getJSONObject("day00");
        SkinProblem.add(jsonObject.getString("grade"));
        Log.d("TAG",jsonObject.getString("grade"));
        return SkinProblem;
    }

    /*기상 특보를 불러온다*/
    public ArrayList<String> getalertWeather() throws Exception{
        ArrayList<String> alertWeather = new ArrayList<>();
        JSONObject jsonObject = startJsonParsing(3);


        String alertYn = jsonObject.getJSONObject("common").getString("alertYn");
        String stormYn = jsonObject.getJSONObject("common").getString("stormYn");
        String timeStart = jsonObject.getJSONObject("weather").getJSONArray("alert").getJSONObject(0).getString("timeStart");
        jsonObject = jsonObject.getJSONObject("weather").getJSONArray("alert").getJSONObject(0).getJSONObject("alert51");
        String varName = jsonObject.getString("varName");
        String stressName = jsonObject.getString("stressName");

        alertWeather.add(alertYn);//0
        alertWeather.add(stormYn);
        alertWeather.add(timeStart);
        alertWeather.add(varName);
        alertWeather.add(stressName);//4
        if(alertYn.equals("Y")) {
            alertWeather.add("true");//5
        }else{
            alertWeather.add("false");//5
        }
        if(stormYn.equals("Y")){
            alertWeather.add("true");//6
        }else{
            alertWeather.add("false");//6
        }
        Log.d("TAG","getAlertWeather");
        return alertWeather;
    }

    /*일출 일몰 시간은 불러온다.*/
    public ArrayList<String> getSunsetrise()throws Exception{
        ArrayList<String> sunState = new ArrayList<>();
        JSONObject jsonObject = startJsonParsing(2);
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

    /*대기질 상태를 불러온다.*/
    public ArrayList<String> getAirCondition() throws Exception {
        Log.d("TAG","getAirCondition");
        ArrayList<String> airCondition = new ArrayList<>();
        JSONObject jsonObject = startJsonParsing(1);

        jsonObject = jsonObject.getJSONObject("weather").getJSONArray("dust").getJSONObject(0).getJSONObject("pm10");
        airCondition.add(jsonObject.getString("value"));/*농도(㎍/㎥) - 0~30: 좋음, 31~80: 보통, 81~120: 약간나쁨, 121~200: 나쁨, 201~300: 매우나쁨*/
        airCondition.add(jsonObject.getString("grade"));/*등급 - 좋음, 보통, 약간나쁨, 나쁨, 매우나쁨*/
        Log.d("TAG","getAirCondition");
        return airCondition;
    }

    /*1시간 단위 날씨를 불러온다.*/
    public ArrayList<String> getWeather() throws Exception {
        Log.d("TAG","getWeather");
        ArrayList<String> weatherState = new ArrayList<>();

        JSONObject jsonObject = startJsonParsing(0);

        jsonObject = jsonObject.getJSONObject("weather").getJSONArray("hourly").getJSONObject(0);
        weatherState.add(jsonObject.getJSONObject("wind").getString("wdir"));/*0풍향*/
        weatherState.add(jsonObject.getJSONObject("wind").getString("wspd"));/*1풍속*/
        weatherState.add(jsonObject.getJSONObject("precipitation").getString("type"));/*2강수형태코드 - 0: 현상없음 - 1: 비 - 2: 비/눈 - 3: 눈*/
        weatherState.add(jsonObject.getJSONObject("precipitation").getString("sinceOntime"));/*31시간 누적 강수량 - if type=0/1/2 → 강우량 (mm) - if type=3     → 적설량 (cm)*/
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
        weatherState.add(jsonObject.getString("timeRelease"));/*10 : 발표 시간*/
        weatherState.add(jsonObject.getJSONObject("grid").getString("city"));/*11*/
        weatherState.add(jsonObject.getJSONObject("grid").getString("county"));/*12*/
        weatherState.add(jsonObject.getJSONObject("grid").getString("village"));/*13*/
        weatherState.add(jsonObject.getJSONObject("sky").getString("code"));/*14*/
        weatherState.add(jsonObject.getJSONObject("grid").getString("longitude"));/*15:long*/
        weatherState.add(jsonObject.getJSONObject("grid").getString("latitude"));/*16:lat*/
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
    /*주어진 URL로 Json 파싱을 시작한다.*/
    private JSONObject startJsonParsing(int position) throws Exception {
        JSONObject jsonObject = null;
        URL url = new URL(URLs[position]);
        conn = (HttpURLConnection) url.openConnection();
        //conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("appKey", appKey);
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
        return jsonObject;
    }

    public static int weatherIconUI(ArrayList<String> weatherState, ArrayList<String> sunSetRise) {
        Log.d("TAG",weatherState.get(14).toString());
        Calendar cal = Calendar.getInstance();
        int time = cal.get(Calendar.HOUR_OF_DAY);
        Boolean afterSunSet;
        Bitmap bitmap = null;
        /*sunSet이후를 파악하여 아이콘을 달리하기 위함.*/
        if(time>Integer.parseInt(sunSetRise.get(0).toString().substring(0,2))&&time<Integer.parseInt(sunSetRise.get(1).toString().substring(0,2))){
            afterSunSet=true;
        }else{
            afterSunSet=false;
        }
        switch(weatherState.get(14).toString()) {
            case "SKY_O00":
                return R.drawable.weather38;
            case "SKY_O01":
                return afterSunSet? R.drawable.weather01 : R.drawable.weather08;
            case "SKY_O02":
                return afterSunSet? R.drawable.weather02 : R.drawable.weather09;
            case "SKY_O03":
                return afterSunSet? R.drawable.weather03 : R.drawable.weather10;
            case "SKY_O04":
                return afterSunSet? R.drawable.weather12 : R.drawable.weather40;
            case "SKY_O05":
                return afterSunSet? R.drawable.weather13 : R.drawable.weather41;
            case "SKY_O06":
                return afterSunSet? R.drawable.weather14 : R.drawable.weather42;
            case "SKY_O07":
                return R.drawable.weather18;
            case "SKY_O08":
                return R.drawable.weather21;
            case "SKY_O09":
                return R.drawable.weather32;
            case "SKY_O10":
                return R.drawable.weather04;
            case "SKY_O11":
                return R.drawable.weather29;
            case "SKY_O12":
                return R.drawable.weather26;
            case "SKY_O13":
                return R.drawable.weather27;
            case "SKY_O14":
                return R.drawable.weather28;
            default :
                return R.mipmap.ic_launcher;
        }
    }
}