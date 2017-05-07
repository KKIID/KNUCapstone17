package kr.ac.bist.iot_noti.messaging;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by BIST120 on 2017-05-07.
 */

public class HttpGet {

    public String requestHttpGet(){
        try {
        URL requrl = new URL("http://bist.knu.ac.kr:9191/logs"/*서버 주소*/);
        HttpURLConnection urlConn = (HttpURLConnection) requrl.openConnection();

            urlConn.setRequestMethod("GET");

            int resCode = urlConn.getResponseCode();
            if(resCode!=HttpURLConnection.HTTP_OK) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String input;
            StringBuffer sb = new StringBuffer();
            while((input = reader.readLine())!=null){
                sb.append(input);
            }
            return sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
