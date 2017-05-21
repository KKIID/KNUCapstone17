package kr.ac.bist.ars_iot.Engine;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import kr.ac.bist.ars_iot.Connection.ConnManager;

/**
 * Created by Bist on 2017-05-21.
 */

public class DeviceEngine {
    private static ArrayList<Device> devices = new ArrayList<>();

    public static ArrayList<Device> getDevices() {
        return devices;
    }
    public static void refreshDevices() {
        devices.clear();
        ConnManager manager = new ConnManager();
        String data = null;
        try {
            data = manager.execute("GET", ConnManager.main_url+ConnManager.dev_url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONArray array = null;
        try {
            array = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i = 0; i<array.length(); i++) {
            try {
                devices.add(new Device(array.getJSONObject(i).getString("name"),array.getJSONObject(i).getString("status")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public static String deviceList() {
        String s = "";
        for(int i = 0; i<devices.size();i++)
            s += (i+1) +"번 "+ devices.get(i).getName() +".";
        return s;
    }
    public static boolean isInList(int num) {
        return (num > 0) && (num <= devices.size());
    }
    public static void controlDevices(int selected) {
        ConnManager conn = new ConnManager();
        String[] params = {"id", selected+"" ,"status" ,(!getDevices().get(selected-1).getStatus())+""};
        conn.execute("PUT",ConnManager.main_url+ConnManager.dev_url,ConnManager.makeParams(params));
    }
}

class Device {
    private String name;
    private boolean status;

    public Device(String name, String status) {
        this.name = name;
        if(Boolean.parseBoolean(status))
            this.status = true;
        else
            this.status = false;
    }
    public String getName() {
        return name;
    }
    public boolean getStatus() {
        return status;
    }
}