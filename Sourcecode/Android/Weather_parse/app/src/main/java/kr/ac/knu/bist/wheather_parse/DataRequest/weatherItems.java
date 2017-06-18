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
    private ArrayList<String> alertWeather, Laundary, UltraViolet,FeelTemp, Discomfort, SkinProblem;
    public weatherItems(int saveTime/*데이터가 저장된 시간*/,String longitude,String latitude, ArrayList<String> airCondition, ArrayList<String> weatherState,
                        ArrayList<String> sunSetRise, ArrayList<String> alertWeather, ArrayList<String> Laundary, ArrayList<String> UltraViolet, ArrayList<String> FeelTemp,
                        ArrayList<String> Discomfort, ArrayList<String> SkinProblem){
        this.saveTime = saveTime;
        this.airCondition = airCondition;
        this.weatherState = weatherState;
        this.sunSetRise = sunSetRise;
        this.alertWeather = alertWeather;
        this.Laundary = Laundary;
        this.UltraViolet = UltraViolet;
        this.FeelTemp = FeelTemp;
        this.Discomfort =Discomfort;
        this.SkinProblem = SkinProblem;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ArrayList<String> getLaundary() {
        return Laundary;
    }

    public void setLaundary(ArrayList<String> laundary) {
        Laundary = laundary;
    }

    public ArrayList<String> getUltraViolet() {
        return UltraViolet;
    }

    public void setUltraViolet(ArrayList<String> ultraViolet) {
        UltraViolet = ultraViolet;
    }

    public ArrayList<String> getFeelTemp() {
        return FeelTemp;
    }

    public void setFeelTemp(ArrayList<String> feelTemp) {
        FeelTemp = feelTemp;
    }

    public ArrayList<String> getDiscomfort() {
        return Discomfort;
    }

    public void setDiscomfort(ArrayList<String> discomfort) {
        Discomfort = discomfort;
    }

    public ArrayList<String> getSkinProblem() {
        return SkinProblem;
    }

    public void setSkinProblem(ArrayList<String> skinProblem) {
        SkinProblem = skinProblem;
    }

    public ArrayList<String> getAlertWeather() {
        return alertWeather;
    }

    public void setAlertWeather(ArrayList<String> alertWeather) {
        this.alertWeather = alertWeather;
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