package kr.ac.bist.iot_noti.Adapter;

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
    private int year;
    private int month;
    private int date;
    private int hour;
    private int min;
    private int sec;

    public NotiDate(String s) {
        year = Integer.parseInt(s.substring(0,4));
        month = Integer.parseInt(s.substring(5,7));
        date = Integer.parseInt(s.substring(8,10));
        hour = Integer.parseInt(s.substring(11,13));
        min = Integer.parseInt(s.substring(14,16));
        sec = Integer.parseInt(s.substring(17,19));
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
