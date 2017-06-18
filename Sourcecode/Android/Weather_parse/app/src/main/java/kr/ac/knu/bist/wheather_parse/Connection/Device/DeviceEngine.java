package kr.ac.knu.bist.wheather_parse.Connection.Device;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import kr.ac.knu.bist.wheather_parse.Connection.ConnManager;


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

    public static void addDevice(String name) {
        ConnManager connManager = new ConnManager();
        String[] string = new String[0];
        try {
            string = new String[]{"name", URLEncoder.encode(name,"UTF-8")};
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connManager.execute("POST", ConnManager.main_url + ConnManager.dev_url, ConnManager.makeParams(string));
    }
    public static void deleteDevice(int id) {
        ConnManager connManager = new ConnManager();
        connManager.execute("DELETE",ConnManager.main_url+ConnManager.dev_url+id);
    }
    public static void controlDevice(int id, boolean status, char code) {
        ConnManager conn = new ConnManager();
        String[] params = {"id", id+"" ,"status" , !status+"", "code", code+""};
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