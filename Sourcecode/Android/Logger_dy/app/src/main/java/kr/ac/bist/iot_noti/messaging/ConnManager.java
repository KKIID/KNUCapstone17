package kr.ac.bist.iot_noti.messaging;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Bist on 2017-05-07.
 */

public class ConnManager extends AsyncTask<String, Void, String> {
    public static String main_url = "http://155.230.25.140:9191/";
    public static final String log_url = "logs/";
    public static final String user_url = "notis/";

    private HttpURLConnection conn;
    private String result;

    public void setMain_url(String main_url){
        this.main_url = main_url;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;

        try {
            url = new URL(params[1]);
            conn = (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setConnectTimeout(3000);
        conn.setUseCaches(false);
        switch(params[0]) {
            case "POST" :
                try {
                    result = doPost(conn, params[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "GET" :
                try {
                    result = doGet(conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default :
                return null;
        }
        return result;
    }

    public static String makeParams(String[] args) {
        String buf = "";
        for(int i = 0; i < args.length; i=i+2) {
            buf = buf + args[i] + "=" + args[i+1];
            if(i+2 != args.length)   buf += "&";
        }
        return buf;
    }
    private String doPost(HttpURLConnection conn, String param) {
        conn.setDoInput(true);
        conn.setDoOutput(true);
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return "ProtocolException";
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(param);

        OutputStreamWriter outStream = null;
        try {
            outStream = new OutputStreamWriter(conn.getOutputStream(),"EUC-KR");
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
        PrintWriter writer = new PrintWriter(outStream);
        writer.write(buffer.toString());
        writer.flush();

        InputStreamReader inputStream = null;
        try {
            inputStream = new InputStreamReader(conn.getInputStream(),"EUC-KR");
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
        BufferedReader reader = new BufferedReader(inputStream);
        StringBuilder builder = new StringBuilder();
        String str;
        try {
            while((str=reader.readLine())!=null){
                builder.append(str+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
        return builder.toString();
    }
    private String doGet(HttpURLConnection conn) {
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return "ProtocolException";
        }
        int resCode = 0;
        try {
            resCode = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
        if(resCode!=HttpURLConnection.HTTP_OK) return null;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
        String input;
        StringBuffer sb = new StringBuffer();
        try {
            while((input = reader.readLine())!=null){
                sb.append(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
        return sb.toString();
    }
}
