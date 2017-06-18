package kr.ac.knu.bist.wheather_parse.AlarmManage;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import kr.ac.knu.bist.wheather_parse.Activity.MainActivity;
import kr.ac.knu.bist.wheather_parse.CustomNotification.CustomNotification;
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
    private ArrayList<String> arr1=null,arr2=null,arr3=null,arr4=null,arr5=null,arr6=null,arr7=null,arr8=null,arr9=null;
    CustomNotification cus;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG","알람매니저 발생");
        /*설정한 주기마다 반복하는 부분이다. 현재 시간과 저장된 시간, 현재 위치와 저장된 위치를 비교하여 둘 중에 하나라도 다를 경우 API에 Request를 보내고
        * 데이터 Set을 갱신해준다.*/
        appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        longitude = appPreferences.getString("LONGITUDE", "");
        latitude = appPreferences.getString("LATITUDE", "");
        weatherParsing = new weatherParse(latitude,longitude);
        calendar = Calendar.getInstance();
        dataIO = new weatherIO(context);
        cus = new CustomNotification(context);
        if(dataIO.IsReadable()){
            items = dataIO.weatherRead();
            Boolean IsSameTime = (items.getSaveTime()-calendar.get(Calendar.HOUR_OF_DAY))==0;
            Boolean IsSameLocation = (items.getLatitude().equals(latitude))/*lon*/&&(items.getLongitude().equals(longitude)); /*lat*/
            Log.d("TAG",IsSameTime+"/"+IsSameLocation);
//            if(!(IsSameTime&&IsSameLocation)){//현재 시간과 저장되어 있는 데이터의 갱신 시간이 동일하지 않거나 위치가 같지 않다.
              if(true) {
                Log.d("TAG","현재 시간과 저장되어 있는 데이터의 갱신 시간이 동일하지 않거나 위치가 같지 않다.");
                Thread t = new Thread(new Runnable() {
                  @Override
                  public void run() {
                    /*데이터를 새롭게 넣어준다.*/
                      try {
                          arr1 = weatherParsing.getAirCondition();
                          arr2 = weatherParsing.getWeather();
                          arr3 = weatherParsing.getSunsetrise();
                          arr4 = weatherParsing.getalertWeather();
                          Boolean Is7oClock = (calendar.get(Calendar.HOUR_OF_DAY)-7)==0;
                          if(Is7oClock) {//오전 7시에 생활지수를 넣어준다.
                              arr5 = weatherParsing.getLaundary();
                              arr6 = weatherParsing.getUltraViolet();
                              arr7 = weatherParsing.getFeelTemp();
                              arr8 = weatherParsing.getDiscomfort();
                              arr9 = weatherParsing.getSkinProblem();
                          }
                          Log.d("TAG","현재시간"+calendar.get(Calendar.HOUR_OF_DAY)+"");
                          cus.showCustomNotification("시발 폭염",0,0,0,weatherParse.weatherIconUI(arr2,arr3));
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
                int NbrofSameElements = 0;
                for(int i = 0;i<arr4.size()-2;i++){/*폭염 상태(Alertweather)가 바뀌었는지 확인한다.*/

                    if(items.getAlertWeather().get(i).equals(arr4.get(i))){
                        NbrofSameElements++;/*새로 불러온 성분과 기존 성분이 같으면 값을 증가시킨다.*/
                    }
                }
                if(NbrofSameElements!=(arr4.size()-2)){
                    /*새로 불러온 성분과 기존 성분이 하나라도 같지 않으면 갱신*/
                }else{/*새로 불러온 성분과 기존 성분이 같으면 그냥둔다.*/
                    arr4 = items.getAlertWeather();/*이렇게 하면 5번 성분의 true, false가 변하지 않고 유지된다.*/
                }
                items = new weatherItems(calendar.get(Calendar.HOUR_OF_DAY), longitude,latitude, arr1,arr2,arr3,arr4,arr5,arr6,arr7,arr8,arr9);;
                dataIO.weatherWrite(items);
            }
        }else{/*파일이 존재하지 않는 경우 파일을 새로 만들어준다.*/
            Log.d("TAG","파일이 존재하지 않는 경우임");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                        /*데이터를 새롭게 넣어준다.*/
                    try {
                        arr1 = weatherParsing.getAirCondition();
                        arr2 = weatherParsing.getWeather();
                        arr3 = weatherParsing.getSunsetrise();
                        arr4 = weatherParsing.getalertWeather();
                        arr5 = weatherParsing.getLaundary();
                        arr6 = weatherParsing.getUltraViolet();
                        arr7 = weatherParsing.getFeelTemp();
                        arr8 = weatherParsing.getDiscomfort();
                        arr9 = weatherParsing.getSkinProblem();
                        Log.d("TAG","현재시간"+calendar.get(Calendar.HOUR_OF_DAY)+"");
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
            items = new weatherItems(calendar.get(Calendar.HOUR_OF_DAY), longitude,latitude, arr1,arr2,arr3,arr4,arr5,arr6,arr7,arr8,arr9);
            dataIO.weatherWrite(items);
        }

        items = dataIO.weatherRead();
        if(items !=null) {
            /*특보가 존재하고 오전 7시 이후에 특보가 존재하면 알려준다*/
            if (items.getAlertWeather().get(0).equals("Y")&&calendar.get(Calendar.HOUR_OF_DAY)>6) {//특보가 있음
                //Notification 생성
                Log.d("TAG", items.getAlertWeather().get(5));
                if(items.getAlertWeather().get(5).equals("true")){/*아직 알림을 알리지 않았을 때*/
                    Log.d("TAG","알람을 띄운다.");
                    CustomNotification cus = new CustomNotification(context);
                    cus.showCustomNotification("시발 폭염",0,0,0,2130903040);
                    items.getAlertWeather().set(5,"false");
                    dataIO.weatherWrite(items);
                }
                Log.d("TAG",items.getAlertWeather().get(2).toString()+"시간");
                Log.d("TAG",items.getAlertWeather().get(3).toString()+"특보이름");
                Log.d("TAG",items.getAlertWeather().get(4).toString()+"특보레벨");
            }
            if (items.getAlertWeather().get(1).equals("Y")) {//폭풍특보가 있음.

            }
        }
    }//onRecieve
}