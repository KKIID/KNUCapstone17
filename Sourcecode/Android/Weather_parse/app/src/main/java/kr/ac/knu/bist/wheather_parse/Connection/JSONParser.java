package kr.ac.knu.bist.wheather_parse.Connection;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by Bist on 2017-06-18.
 */

public class JSONParser {
    public static JSONArray parseJSONString(String string) throws JSONException {
        return new JSONArray(string);
    }
    public static String requestJSONString() throws ExecutionException, InterruptedException {
        ConnManager conn = new ConnManager();
        conn.execute("GET", ConnManager.main_url + ConnManager.dev_url);
        return conn.get();
    }
}
