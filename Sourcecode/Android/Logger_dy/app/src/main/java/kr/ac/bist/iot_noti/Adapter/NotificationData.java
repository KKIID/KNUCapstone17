package kr.ac.bist.iot_noti.Adapter;

import java.util.Calendar;

/**
 * Created by Bist on 2017-05-07.
 */

public class NotificationData  {
    public String textTime;
    public String textContent;
    public NotificationData(String textContent, String textTime) {
        this.textTime = new NotiDate(textTime).toString();
        this.textContent = textContent;
    }
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
        sec = Integer.parseInt(s.substring(17,19));
    }
    public void setTime(Calendar cal) {
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        date = cal.get(Calendar.DATE);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        sec = cal.get(Calendar.SECOND);
    }

    public String getDay() {
        return String.format("%04d-%02d-%02d",year,month,date);
    }

    public String getTime() {
        return String.format("%02d:%02d:%02d",hour,min,sec);
    }

    public String toString() {
        return getDay() + " " +getTime();
    }
}
