package kr.ac.knu.bist.wheather_parse.Service.AlarmManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Bist on 2017-06-17.
 */

public class AlarmManage {
    public AlarmManage(){

    }
    public void registAlarm(Context context){
        Log.d("TAG", "알람등록");
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmRecieve.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,1234/*알람매니저를 인식하는 코드가 됨.*/,intent,0);
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),1000,sender);
    }
}
