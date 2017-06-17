package kr.ac.bist.ars_iot.Connection;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Bist on 2017-05-07.
 */

public class ConnManager extends AsyncTask<String, Void, String> {
    public static String main_url = "http://155.230.25.138:9191/";
    public static final String dev_url = "devs/";
    public static final String pass_url = "password/";

    private HttpURLConnection conn;
    private String result;

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
        conn.setConnectTimeout(1000);
        conn.setUseCaches(false);
        switch(params[0]) {
            case "POST" :
                try {
                    result = doPost(conn, params[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "PUT" :
                try {
                    result = doPut(conn, params[2]);
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
    private String doPost(HttpURLConnection conn, String param) throws Exception {
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        StringBuffer buffer = new StringBuffer();
        buffer.append(param);

        OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(),"EUC-KR");
        PrintWriter writer = new PrintWriter(outStream);
        writer.write(buffer.toString());
        writer.flush();

        InputStreamReader inputStream = new InputStreamReader(conn.getInputStream(),"EUC-KR");
        BufferedReader reader = new BufferedReader(inputStream);
        StringBuilder builder = new StringBuilder();
        String str;
        while((str=reader.readLine())!=null){
            builder.append(str+"\n");
        }
        return builder.toString();
    }
    private String doPut(HttpURLConnection conn, String param) throws Exception {
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");

        StringBuffer buffer = new StringBuffer();
        buffer.append(param);

        OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(),"EUC-KR");
        PrintWriter writer = new PrintWriter(outStream);
        writer.write(buffer.toString());
        writer.flush();

        InputStreamReader inputStream = new InputStreamReader(conn.getInputStream(),"EUC-KR");
        BufferedReader reader = new BufferedReader(inputStream);
        StringBuilder builder = new StringBuilder();
        String str;
        while((str=reader.readLine())!=null){
            builder.append(str+"\n");
        }
        return builder.toString();
    }
    private String doGet(HttpURLConnection conn) throws Exception {
        conn.setRequestMethod("GET");
        int resCode = conn.getResponseCode();
        if(resCode!=HttpURLConnection.HTTP_OK) return null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String input;
        StringBuffer sb = new StringBuffer();
        while((input = reader.readLine())!=null){
            sb.append(input);
        }
        return sb.toString();
    }
}
