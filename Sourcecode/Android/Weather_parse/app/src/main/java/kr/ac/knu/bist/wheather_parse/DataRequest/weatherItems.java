package kr.ac.knu.bist.wheather_parse.DataRequest;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by BIST120 on 2017-06-17.
 */

public class weatherItems implements Serializable {
    private int saveTime;
    private String longitude, latitude;
    private ArrayList<String> airCondition;
    private ArrayList<String> weatherState;
    public weatherItems(int saveTime/*데이터가 저장된 시간*/,String longitude,String latitude, ArrayList<String> airCondition, ArrayList<String> weatherState, ArrayList<String> sunSetRise){
        this.saveTime = saveTime;
        this.airCondition = airCondition;
        this.weatherState = weatherState;
        this.sunSetRise = sunSetRise;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private ArrayList<String> sunSetRise;

    public int getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(int saveTime) {
        this.saveTime = saveTime;
    }



    public ArrayList<String> getAirCondition() {
        return airCondition;
    }

    public void setAirCondition(ArrayList<String> airCondition) {
        this.airCondition = airCondition;
    }

    public ArrayList<String> getWeatherState() {
        return weatherState;
    }

    public void setWeatherState(ArrayList<String> weatherState) {
        this.weatherState = weatherState;
    }

    public ArrayList<String> getSunSetRise() {
        return sunSetRise;
    }

    public void setSunSetRise(ArrayList<String> sunSetRise) {
        this.sunSetRise = sunSetRise;
    }
}