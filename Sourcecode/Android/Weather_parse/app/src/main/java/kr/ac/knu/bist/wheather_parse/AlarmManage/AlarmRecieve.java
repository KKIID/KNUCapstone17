package kr.ac.knu.bist.wheather_parse.AlarmManage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import kr.ac.knu.bist.wheather_parse.DataRequest.*;
/**
 * Created by BIST120 on 2017-06-17.
 */

public class AlarmRecieve extends BroadcastReceiver {
    private Calendar calendar =null;
    private weatherItems items = null;
    private weatherIO dataIO = null;
    private SharedPreferences appPreferences = null;
    private String longitude,latitude ="";
    private weatherParse weatherParsing;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG","알람매니저 발생");
        /*설정한 주기마다 반복하는 부분이다. 현재 시간과 저장된 시간, 현재 위치와 저장된 위치를 비교하여 둘 중에 하나라도 다를 경우 API에 Request를 보내고
        * 데이터 Set을 갱신해준다.*/
        appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        longitude = appPreferences.getString("LONGITUDE", "");
        latitude = appPreferences.getString("LATITUDE", "");

        calendar = Calendar.getInstance();
        dataIO = new weatherIO(context);
        if(dataIO.IsReadable()){
            items = dataIO.weatherRead();
            Boolean IsSameTime = (items.getSaveTime()-calendar.get(Calendar.HOUR_OF_DAY))==0;
            Log.d("TAG",items.getWeatherState().get(15)+"/" + longitude);
            Boolean IsSameLocation = (items.getLatitude().equals(longitude))/*lon*/&&(items.getLongitude().equals(latitude)); /*lat*/
            if(!IsSameTime||!IsSameLocation){//현재 시간과 저장되어 있는 데이터의 갱신 시간이 동일하지 않거나 위치가 같지 않다.
                Log.d("TAG","현재 시간과 저장되어 있는 데이터의 갱신 시간이 동일하지 않거나 위치가 같지 않다.");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        weatherParsing = new weatherParse(latitude,longitude);
                        /*데이터를 새롭게 넣어준다.*/
                        try {
                            items = new weatherItems(Integer.parseInt(weatherParsing.getWeather().get(10).substring(11, 13)), longitude,latitude,
                                    weatherParsing.getAirCondition(), weatherParsing.getWeather(), weatherParsing.getSunsetrise());
                            dataIO.weatherWrite(items);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }else{/*파일이 존재하지 않는 경우 파일을 새로 만들어준다.*/
            Log.d("TAG","파일이 존재하지 않는 경우임");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    weatherParsing = new weatherParse(latitude,longitude);
                        /*데이터를 새롭게 넣어준다.*/
                    try {
                        items = new weatherItems(Integer.parseInt(weatherParsing.getWeather().get(10).substring(11, 13)), longitude,latitude,
                                weatherParsing.getAirCondition(), weatherParsing.getWeather(), weatherParsing.getSunsetrise());
                        dataIO.weatherWrite(items);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}