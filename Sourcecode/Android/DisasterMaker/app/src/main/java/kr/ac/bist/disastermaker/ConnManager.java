package kr.ac.bist.disastermaker;

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

public class ConnManager extends AsyncTask<String,Void,Void> {

    private  URL url = null;
    private HttpURLConnection conn = null;
    public final static String main_url = "http://bist.knu.ac.kr:9191/";
    public final static String log_url = "logs";

    @Override
    protected Void doInBackground(String... params)  {
        try {
            url = new URL(main_url+log_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);              //서버에서 읽기 모드
            conn.setDoOutput(true);             //서버로 쓰기 모드
            conn.setRequestMethod("POST");  //전송방식 POST
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append("name="+params[0]);        //php 변수에 값 대입

        OutputStreamWriter outStream = null;
        try {
            outStream = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter(outStream);
        writer.write(buffer.toString());
        writer.flush();

        InputStreamReader inputStream = null;
        try {
            inputStream = new InputStreamReader(conn.getInputStream(),"EUC-KR");
        } catch (IOException e) {
            e.printStackTrace();
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
        }
        String resultStr = builder.toString();
        return null;
    }
}
